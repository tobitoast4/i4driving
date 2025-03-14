package org.opentrafficsim.i4driving.sim0mq;

import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.naming.NamingException;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Direction;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djunits.value.vfloat.vector.FloatAccelerationVector;
import org.djunits.value.vfloat.vector.FloatDurationVector;
import org.djunits.value.vfloat.vector.FloatLengthVector;
import org.djutils.cli.CliUtil;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.Point2d;
import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.immutablecollections.ImmutableList;
import org.djutils.logger.CategoryLogger;
import org.djutils.serialization.SerializationException;
import org.opentrafficsim.animation.colorer.SynchronizationColorer;
import org.opentrafficsim.animation.gtu.colorer.AccelerationGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.animation.gtu.colorer.IdGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SpeedGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SwitchableGtuColorer;
import org.opentrafficsim.core.dsol.AbstractOtsModel;
import org.opentrafficsim.core.dsol.OtsAnimator;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.geometry.OtsLine2d;
import org.opentrafficsim.core.geometry.OtsLine2d.FractionalFallback;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlan;
import org.opentrafficsim.core.gtu.plan.operational.Segment;
import org.opentrafficsim.core.network.Link;
import org.opentrafficsim.core.network.Network;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.draw.OtsDrawingException;
import org.opentrafficsim.i4driving.messages.Commands;
import org.opentrafficsim.i4driving.messages.DefaultGsonBuilder;
import org.opentrafficsim.i4driving.tactical.CommandsHandler;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.road.gtu.generator.GtuSpawner;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristics;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.swing.gui.OtsAnimationPanel;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;
import org.pmw.tinylog.Level;
import org.sim0mq.Sim0MQException;
import org.sim0mq.message.Sim0MQMessage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.google.gson.Gson;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.language.DsolException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * OTS co-simulation server.
 * @author wjschakel
 */
@Command(description = "OTS Transceiver for co-simulation", name = "OTS", mixinStandardHelpOptions = true,
        showDefaultValues = true, version = "20250219")
public class OtsTransceiver
{

    /** Federation id to receive/sent messages. */
    @Option(names = "--federationId", description = "Federation id to receive/sent messages", defaultValue = "Ots_ExternalSim")
    private String federation;

    /** Ots id to receive/sent messages. */
    @Option(names = "--otsId", description = "Ots id to receive/sent messages", defaultValue = "Ots")
    private String ots;

    /** Client id to receive/sent messages. */
    @Option(names = "--clientId", description = "Client id to receive/sent messages", defaultValue = "ExternalSim")
    private String client;

    /** Endianness. */
    @Option(names = "--bigEndian", description = "Big-endianness", defaultValue = "false", negatable = true)
    private Boolean bigEndian;

    /** Port number. */
    @Option(names = "--port", description = "Port number", defaultValue = "5556")
    private int port;

    /** Show GUI. */
    @Option(names = "--no-gui", description = "Whether to show GUI", defaultValue = "true", negatable = true)
    private boolean showGui;

    /**
     * Constructor.
     * @param args command line arguments.
     * @throws Exception on any exception during simulation.
     */
    protected OtsTransceiver(final String... args) throws Exception
    {
        CliUtil.execute(this, args);
    }

    /**
     * Main method.
     * @param args command line arguments
     * @throws Exception exception
     */
    public static void main(final String[] args) throws Exception
    {
        new OtsTransceiver(args).start();
    }

    /**
     * Starts worker thread.
     */
    private void start()
    {
        new Worker().start();
    }

    /**
     * Worker thread to listen to messages and respond.
     * <p>
     * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved.
     * <br>
     * BSD-style license. See <a href="https://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
     * </p>
     * @author <a href="https://github.com/wjschakel">Wouter Schakel</a>
     */
    protected class Worker extends Thread implements EventListener
    {

        /** */
        private static final long serialVersionUID = 20241210L;

        /** */
        private ZContext context;

        /** the socket. */
        private ZMQ.Socket responder;

        /** Next message id. */
        private int messageId = 0;

        /** Last network message. */
        private Sim0MQMessage lastNetworkMessage;

        /** List of vehicle message payloads before simulation start. */
        private List<Object[]> preStartVehiclePayloads = new ArrayList<>();

        /** GTU characteristics. */
        private LaneBasedGtuCharacteristics gtuCharacteristics;

        /** Simulator. */
        private OtsAnimator simulator;

        /** Network. */
        private RoadNetwork network;

        /** Application. */
        private OtsSimulationApplication<AbstractOtsModel> app;

        /** Ids of GTUs for which plan messages are sent. */
        private Set<String> planGtuIds = new LinkedHashSet<>();

        /** Ids of GTUs that are externally controlled. */
        private Set<String> externalGtuIds = new LinkedHashSet<>();

        /** Command handlers. */
        private Map<String, CommandsHandler> commandHandlers = new LinkedHashMap<>();

        /** GSON builder to parser JSON strings. */
        private Gson gson = DefaultGsonBuilder.get();

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            this.context = new ZContext(1);
            this.responder = this.context.createSocket(SocketType.CHANNEL);
            this.responder.bind("tcp://*:" + port);
            CategoryLogger.setAllLogLevel(Level.DEBUG);
            CategoryLogger.setAllLogMessageFormat("[{date: YYYY-MM-dd HH:mm:ss.SSS}] {level}: {message}");
            CategoryLogger.always().debug("Ots is running");

            try
            {
                // Note on synchronicity and possible dead-locks:
                // OTS is single-threaded. All changes during the simulation should be scheduled in the simulator.
                while (!Thread.currentThread().isInterrupted())
                {
                    // Wait for next request from the client
                    byte[] request = null;
                    request = this.responder.recv(ZMQ.DONTWAIT);
                    while (request == null)
                    {
                        try
                        {
                            // In order to allow resources to go to other processes, we sleep before checking again
                            Thread.sleep(3);
                        }
                        catch (InterruptedException e)
                        {
                        }
                        request = this.responder.recv(ZMQ.DONTWAIT);
                    }
                    Sim0MQMessage message = Sim0MQMessage.decode(request);
                    if ("EXTERNAL".equals(message.getMessageTypeId()))
                    {
                        Object[] payload = message.createObjectArray();
                        int index = 8;
                        String id = (String) payload[index++];
                        Length x = (Length) payload[index++];
                        Length y = (Length) payload[index++];
                        Direction direction = (Direction) payload[index++];
                        Speed speed = (Speed) payload[index++];
                        Acceleration acceleration = (Acceleration) payload[index++];
                        OrientedPoint2d loc = new OrientedPoint2d(x.si, y.si, direction.si);
                        this.simulator.scheduleEventNow(this, "scheduledDeadReckoning",
                                new Object[] {id, loc, speed, acceleration});
                    }
                    else if ("VEHICLE".equals(message.getMessageTypeId()))
                    {
                        Object[] payload = message.createObjectArray();
                        int index = 8;
                        String id = (String) payload[index++];
                        CategoryLogger.always().debug("Ots received VEHICLE message for GTU " + id);
                        generateVehicle(payload, true);
                    }
                    else if ("MODE".equals(message.getMessageTypeId()))
                    {
                        Object[] payload = message.createObjectArray();
                        String id = (String) payload[8];
                        CategoryLogger.always().debug("Ots received MODE message for GTU " + id);
                        String mode = (String) payload[9];
                        this.simulator.scheduleEventNow(this, "scheduledChangeControlMode", new Object[] {id, mode});
                    }
                    else if ("COMMAND".equals(message.getMessageTypeId()))
                    {
                        Object[] payload = message.createObjectArray();
                        String id = (String) payload[8];
                        CategoryLogger.always().debug("Ots received COMMAND message for GTU " + id);
                        String json = (String) payload[9];
                        this.simulator.scheduleEventNow(this, "scheduledPerformCommand", new Object[] {id, json});
                    }
                    else if ("DELETE".equals(message.getMessageTypeId()))
                    {
                        Object[] payload = message.createObjectArray();
                        String id = (String) payload[8];
                        CategoryLogger.always().debug("Ots received DELETE message for GTU " + id);
                        this.simulator.scheduleEventNow(this, "scheduledDelete", new Object[] {id});
                    }
                    else if ("NETWORK".equals(message.getMessageTypeId()))
                    {
                        CategoryLogger.always().debug("Ots received NETWORK message");
                        this.lastNetworkMessage = message;
                        setupSimulation();
                        sentReadyMessage((int) message.createObjectArray()[6]);
                    }
                    else if ("START".equals(message.getMessageTypeId()))
                    {
                        CategoryLogger.always().debug("Ots received START message");
                        if (this.simulator != null && !this.simulator.isStartingOrRunning())
                        {
                            this.simulator.start();
                        }
                    }
                    else if ("STOP".equals(message.getMessageTypeId()))
                    {
                        CategoryLogger.always().debug("Ots received STOP message");
                        stopSimulation();
                        this.lastNetworkMessage = null;
                        this.preStartVehiclePayloads.clear();
                    }
                    else if ("RESET".equals(message.getMessageTypeId()))
                    {
                        CategoryLogger.always().debug("Ots received RESET message");
                        setupSimulation();
                        sentReadyMessage((int) message.createObjectArray()[6]);
                    }
                    else if ("TERMINATE".equals(message.getMessageTypeId()))
                    {
                        CategoryLogger.always().debug("Ots received TERMINATE message");
                        stopSimulation();
                        break;
                    }
                    else
                    {
                        System.err.println("Cannot process a " + message.getMessageTypeId() + " message.");
                    }
                }
            }
            catch (Sim0MQException | SerializationException | NumberFormatException | GtuException | OtsGeometryException
                    | NetworkException | RemoteException | DsolException | OtsDrawingException | SimRuntimeException
                    | NamingException e)
            {
                e.printStackTrace();
            }
            this.responder.close();
            this.context.destroy();
            this.context.close();
            CategoryLogger.always().debug("Ots terminated");
            System.exit(0);
        }

        /**
         * Generates vehicle.
         * @param payload message payload
         * @param addToPreStartList whether to add the message to the list of pre-start vehicle messages if not started yet
         * @throws GtuException exception
         * @throws OtsGeometryException exception
         * @throws NetworkException exception
         * @throws RemoteException exception
         */
        private void generateVehicle(final Object[] payload, final boolean addToPreStartList)
                throws GtuException, OtsGeometryException, NetworkException, RemoteException
        {
            int index = 8;
            String id = (String) payload[index++];
            String mode = (String) payload[index++];
            Length initX = (Length) payload[index++];
            Length initY = (Length) payload[index++];
            Direction initDirection = (Direction) payload[index++];
            Speed initSpeed = (Speed) payload[index++];
            String vehicleType = (String) payload[index++];
            Length vehicleLength = (Length) payload[index++];
            Length refToNose = (Length) payload[index++];
            int numParams = (int) payload[index++];
            for (int i = 0; i < numParams; i++)
            {
                String parameter = (String) payload[index++];
                Object value = payload[index++];
            }
            Object roadIds = payload[index++]; // TODO String[] is not supported by sim0mq

            LanePosition lanePosition = getLanePosition(initX, initY, initDirection);
            // TODO: GtuSpawner to too limited for our purposes, i.e. direction and route
            new GtuSpawner().spawnGtu(id, this.gtuCharacteristics, this.network, initSpeed, lanePosition);
            if (this.simulator == null || !this.simulator.isStartingOrRunning())
            {
                scheduledChangeControlMode(id, mode);
                sentReadyMessage((int) payload[6]);
                if (addToPreStartList)
                {
                    this.preStartVehiclePayloads.add(payload);
                }
            }
            else
            {
                this.simulator.scheduleEventNow(this.simulator, "scheduledChangeControlMode", new Object[] {id, mode});
            }
        }

        /**
         * Scheduled delete running in the simulator.
         * @param id GTU id
         */
        @SuppressWarnings("unused") // scheduled
        private void scheduledDelete(final String id)
        {
            this.network.getGTU(id).destroy();
        }

        /**
         * Method that runs scheduled in the simulator to apply dead reckoning.
         * @param id GTU id
         * @param loc location
         * @param speed speed
         * @param acceleration acceleration
         */
        @SuppressWarnings("unused") // scheduled
        private void scheduledDeadReckoning(final String id, final OrientedPoint2d loc, final Speed speed,
                final Acceleration acceleration)
        {
            ScenarioTacticalPlanner planner = getTacticalPlanner(id);
            if (planner != null)
            {
                planner.deadReckoning(loc, speed, acceleration);
            }
        }

        /**
         * Perform command on GTU.
         * @param id GTU id
         * @param json JSON string of command
         */
        @SuppressWarnings("unused") // scheduled
        private void scheduledPerformCommand(final String id, final String json)
        {
            Commands.Command command = this.gson.fromJson(json, Commands.Command.class);
            Function<String, CommandsHandler> function =
                    (gtuId) -> new CommandsHandler(this.network, new Commands(gtuId, null), null);
            this.commandHandlers.computeIfAbsent(id, function).executeCommand(command);
        }

        /**
         * Initializes or changes the control mode of the GTU with given id.
         * @param id GTU id
         * @param mode control mode, Ots, Hybrid or External
         */
        private void scheduledChangeControlMode(final String id, final String mode)
        {
            switch (mode.toLowerCase())
            {
                case "ots":
                {
                    this.planGtuIds.add(id);
                    if (this.externalGtuIds.remove(id))
                    {
                        getTacticalPlanner(id).stopDeadReckoning();
                    }
                    break;
                }
                case "hybrid":
                {
                    this.planGtuIds.add(id);
                    if (this.externalGtuIds.add(id))
                    {
                        getTacticalPlanner(id).startDeadReckoning();
                    }
                    break;
                }
                case "external":
                {
                    this.planGtuIds.remove(id);
                    if (this.externalGtuIds.add(id))
                    {
                        getTacticalPlanner(id).startDeadReckoning();
                    }
                    break;
                }
                default:
                    System.err.println("Unknown control mode " + mode);
                    break;
            }
        }

        /**
         * Notifies the external simulator that OTS is ready to start a simulation.
         * @param msgId message id of message that was processed
         * @throws RemoteException
         */
        private void sentReadyMessage(final int msgId) throws RemoteException
        {
            try
            {
                this.responder.send(Sim0MQMessage.encodeUTF8(OtsTransceiver.this.bigEndian, OtsTransceiver.this.federation,
                        OtsTransceiver.this.ots, OtsTransceiver.this.client, "READY", this.messageId++, new Object[] {msgId}),
                        0);
            }
            catch (Sim0MQException | SerializationException ex)
            {
                throw new RemoteException("Exception while sending operational plan.", ex);
            }
        }

        /**
         * Returns the scenario based tactical planner for GTU with given id.
         * @param gtuId GTU id
         * @return scenario based tactical planner for GTU with given id, {@code null} if the vehicle does not exist
         */
        private ScenarioTacticalPlanner getTacticalPlanner(final String gtuId)
        {
            Gtu gtu = this.network.getGTU(gtuId);
            if (gtu == null)
            {
                return null;
            }
            return (ScenarioTacticalPlanner) gtu.getTacticalPlanner();
        }

        /**
         * Stops any running simulation.
         */
        private void stopSimulation()
        {
            this.planGtuIds.clear();
            this.externalGtuIds.clear();
            this.commandHandlers.clear();
            if (this.simulator != null && !this.simulator.isStoppingOrStopped())
            {
                this.simulator.stop();
                this.simulator = null;
                this.network = null;
            }
            if (this.app != null)
            {
                this.app.dispose();
                this.app = null;
            }
        }

        /**
         * Setup simulation.
         * @throws GtuException exception
         * @throws OtsGeometryException exception
         * @throws NetworkException exception
         * @throws RemoteException exception
         * @throws DsolException exception
         * @throws OtsDrawingException exception
         * @throws SimRuntimeException exception
         * @throws NamingException exception
         */
        private void setupSimulation() throws GtuException, OtsGeometryException, NetworkException, RemoteException,
                DsolException, OtsDrawingException, SimRuntimeException, NamingException
        {
            stopSimulation();

            if (this.lastNetworkMessage == null)
            {
                return;
            }

            // String to OpenDRIVE network
            // or
            // Length x start
            // Length y start
            // Length x end
            // Length y end
            // int number of lanes
            // Speed speed limit

            // An animator supports real-time running. No GUI will be shown if no animation panel is created.
            this.simulator = new OtsAnimator("Test animator");

            CoSimModel model = new CoSimModel(this.simulator);
            this.simulator.initialize(Time.ZERO, Duration.ZERO, Duration.instantiateSI(60.0), model);
            this.network = (RoadNetwork) model.getNetwork();
            this.gtuCharacteristics = model.getGtuCharacteristics();
            listenToEvents();

            if (OtsTransceiver.this.showGui)
            {
                GtuColorer colorer = new SwitchableGtuColorer(0, new IdGtuColorer(),
                        new SpeedGtuColorer(new Speed(150, SpeedUnit.KM_PER_HOUR)),
                        new AccelerationGtuColorer(Acceleration.instantiateSI(-6.0), Acceleration.instantiateSI(2)),
                        new SynchronizationColorer());
                OtsAnimationPanel animationPanel = new OtsAnimationPanel(this.network.getExtent(), new Dimension(100, 100),
                        this.simulator, model, colorer, this.network);
                animationPanel.enableSimulationControlButtons();
                this.app = new OtsSimulationApplication<AbstractOtsModel>(model, animationPanel);
            }

            // Reset pre-start vehicles
            for (Object[] payload : this.preStartVehiclePayloads)
            {
                generateVehicle(payload, false); // do not add these messages to the list
            }
        }

        /**
         * Returns the lane position closest to the given location.
         * @param initX X coordinate
         * @param initY Y coordinate
         * @param initDirection direction
         * @return lane position closest to the given location
         */
        private LanePosition getLanePosition(final Length initX, final Length initY, final Direction initDirection)
        {
            OrientedPoint2d point = new OrientedPoint2d(initX.si, initY.si, initDirection.si);
            double minDistance = Double.POSITIVE_INFINITY;
            LanePosition lanePosition = null;
            for (Link link : this.network.getLinkMap().values())
            {
                if (link instanceof CrossSectionLink roadLink)
                {
                    for (Lane lane : roadLink.getLanesAndShoulders())
                    {
                        double fraction = lane.getCenterLine().projectFractional(link.getStartNode().getHeading(),
                                link.getEndNode().getHeading(), initX.si, initY.si, FractionalFallback.ENDPOINT);
                        Point2d pointOnLane = lane.getCenterLine().getLocationFractionExtended(fraction);
                        double distance = pointOnLane.distance(point);
                        if (distance < minDistance)
                        {
                            minDistance = distance;
                            lanePosition = new LanePosition(lane, lane.getCenterLine().getLength().times(fraction));
                        }
                    }
                }
            }
            return lanePosition;
        }

        @Override
        public void notify(final Event event) throws RemoteException
        {
            EventType eventType = event.getType();
            if (eventType.equals(LaneBasedGtu.LANEBASED_MOVE_EVENT))
            {
                String gtuId = (String) ((Object[]) event.getContent())[0];
                if (!this.planGtuIds.contains(gtuId))
                {
                    return;
                }
                sentOperationalPlanMessage(gtuId);
            }
            else if (eventType.equals(Network.GTU_ADD_EVENT))
            {
                String gtuId = (String) event.getContent();
                Gtu gtu = this.network.getGTU(gtuId);
                gtu.addListener(this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
            }
            else if (eventType.equals(Network.GTU_REMOVE_EVENT))
            {
                String gtuId = (String) event.getContent();
                Gtu gtu = this.network.getGTU(gtuId);
                gtu.removeListener(this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
                this.planGtuIds.remove(gtuId);
                this.externalGtuIds.remove(gtuId);
                this.commandHandlers.remove(gtuId);
                sentDeleteMessage(gtuId);
            }
        }

        /**
         * Sent operational plan.
         * @param gtuId GTU id
         * @throws RemoteException exception
         */
        private void sentOperationalPlanMessage(final String gtuId) throws RemoteException
        {
            Gtu gtu = this.network.getGTU(gtuId);
            OperationalPlan plan = ((ScenarioTacticalPlanner) gtu.getTacticalPlanner()).pullLastIntendedPlan();
            if (plan == null)
            {
                // Do not sent plan upon a move triggered by external control
                return;
            }
            Speed speed = plan.getStartSpeed();
            OtsLine2d line = plan.getPath();
            float[] x = new float[line.size()];
            float[] y = new float[line.size()];
            try
            {
                for (int i = 0; i < line.size(); i++)
                {
                    x[i] = (float) line.get(i).x;
                    y[i] = (float) line.get(i).y;
                }
            }
            catch (OtsGeometryException ex)
            {
                throw new RemoteException("Unable to obtain coordinate from path.", ex);
            }
            ImmutableList<Segment> segments = plan.getOperationalPlanSegmentList();
            float[] t = new float[segments.size()];
            float[] a = new float[segments.size()];
            for (int i = 0; i < segments.size(); i++)
            {
                t[i] = (float) segments.get(i).duration().si;
                a[i] = (float) segments.get(i).acceleration().si;
            }

            Object[] payload = new Object[6];
            payload[0] = gtuId;
            payload[1] = speed;
            payload[2] = new FloatLengthVector(x);
            payload[3] = new FloatLengthVector(y);
            payload[4] = new FloatDurationVector(t);
            payload[5] = new FloatAccelerationVector(a);
            try
            {
                this.responder.send(
                        Sim0MQMessage.encodeUTF8(OtsTransceiver.this.bigEndian, OtsTransceiver.this.federation,
                                OtsTransceiver.this.ots, OtsTransceiver.this.client, "PLAN", this.messageId++, payload),
                        ZMQ.DONTWAIT);
                CategoryLogger.always().debug("[{0.000}s] Ots sent PLAN message for GTU {}", this.simulator.getSimulatorTime(),
                        gtuId);
            }
            catch (Sim0MQException | SerializationException ex)
            {
                throw new RemoteException("Exception while sending operational plan.", ex);
            }
        }

        /**
         * Sent delete message to external sim.
         * @param gtuId GTU id
         * @throws RemoteException exception
         */
        private void sentDeleteMessage(final String gtuId) throws RemoteException
        {
            Object[] payload = new Object[1];
            payload[0] = gtuId;
            try
            {
                this.responder.send(
                        Sim0MQMessage.encodeUTF8(OtsTransceiver.this.bigEndian, OtsTransceiver.this.federation,
                                OtsTransceiver.this.ots, OtsTransceiver.this.client, "DELETE", this.messageId++, payload),
                        ZMQ.DONTWAIT);
                CategoryLogger.always().debug("[{0.000}s] Ots sent DELETE message for GTU {}",
                        this.simulator.getSimulatorTime(), gtuId);
            }
            catch (Sim0MQException | SerializationException ex)
            {
                throw new RemoteException("Exception while sending operational plan.", ex);
            }
        }

        /**
         * Listen to relevant events.
         * @throws RemoteException exception
         */
        private void listenToEvents() throws RemoteException
        {
            this.network.addListener(this, Network.GTU_ADD_EVENT);
            this.network.addListener(this, Network.GTU_REMOVE_EVENT);
        }
    }

    /**
     * Co-simulation model.
     */
    private final class CoSimModel extends AbstractOtsModel
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** Network. */
        private RoadNetwork network;

        /** GTU characteristics. */
        private LaneBasedGtuCharacteristics gtuCharacteristics;

        /**
         * Constructor.
         * @param simulator simulator
         */
        private CoSimModel(final OtsSimulatorInterface simulator)
        {
            super(simulator);
        }

        @Override
        public Network getNetwork()
        {
            return this.network;
        }

        /**
         * Returns GTU characteristics.
         * @return GTU characteristics
         */
        public LaneBasedGtuCharacteristics getGtuCharacteristics()
        {
            return this.gtuCharacteristics;
        }

        @Override
        public void constructModel() throws SimRuntimeException
        {
            // For now, a fixed two-lane test network
            try
            {
                TwoLaneTestSimulation simulation = new TwoLaneTestSimulation(getSimulator());
                this.network = simulation.getNetwork();
                this.gtuCharacteristics = simulation.getGtuCharacteristics();
            }
            catch (GtuException | OtsGeometryException | NetworkException ex)
            {
                ex.printStackTrace();
            }
        }
    }

}
