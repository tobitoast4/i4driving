package org.opentrafficsim.i4driving.sim0mq;

import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.naming.NamingException;

import org.djunits.unit.SpeedUnit;
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
import org.djutils.serialization.SerializationException;
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
import org.opentrafficsim.road.gtu.generator.GtuSpawner;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristics;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.swing.gui.OtsAnimationPanel;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;
import org.opentrafficsim.swing.gui.OtsSwingApplication;
import org.sim0mq.Sim0MQException;
import org.sim0mq.message.Sim0MQMessage;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.language.DsolException;
import picocli.CommandLine.Option;

/**
 * OTS co-simulation server.
 * @author wjschakel
 */
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
    @Option(names = "--bigEndian", description = "Big-endianness", defaultValue = "false")
    private Boolean bigEndian;

    /** Port number. */
    @Option(names = "--port", description = "Port number", defaultValue = "5556")
    private int port;

    /** Show GUI. */
    @Option(names = "--gui", description = "Whether to show GUI", defaultValue = "false")
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

        /** GTU characteristics. */
        private LaneBasedGtuCharacteristics gtuCharacteristics;

        /** Simulator. */
        private OtsAnimator simulator;

        /** Network. */
        private RoadNetwork network;

        /** Application. */
        private OtsSimulationApplication<AbstractOtsModel> app;

        /** Ids of vehicles that are externally controlled. */
        private Set<String> externalGtuIds = new LinkedHashSet<>();

        /** {@inheritDoc} */
        @Override
        public void run()
        {
            this.context = new ZContext(1);
            this.responder = this.context.createSocket(SocketType.CHANNEL);
            this.responder.bind("tcp://*:" + port);
            System.out.println("Ots is running");

            try
            {
                while (!Thread.currentThread().isInterrupted())
                {
                    // Wait for next request from the client
                    byte[] request = this.responder.recv(ZMQ.DONTWAIT);
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
                        // String id
                        // Length x coordinate
                        // Length y coordinate
                        // Direction vehicle direction
                        // Speed speed
                        // Acceleration acceleration

                    }
                    else if ("VEHICLE".equals(message.getMessageTypeId()))
                    {
                        // String id
                        // boolean external or not
                        // Length init x
                        // Length init y
                        // Direction init direction
                        // Speed init speed
                        // String vehicle type [car/truck]
                        // Length vehicle length
                        // Length reference to nose
                        // int number of parameters
                        // - with each: String parameter, {varies} value
                        // String[] road ids
                        // TODO String[] is not supported by sim0mq
                        Object[] payload = message.createObjectArray();
                        int index = 8;
                        String id = (String) payload[index++];
                        System.out.println("Ots received VEHICLE message for GTU " + id);
                        boolean external = (boolean) payload[index++];
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
                        Object roadIds = payload[index++]; // see TODO above

                        LanePosition lanePosition = getLanePosition(initX, initY, initDirection);
                        // TODO: GtuSpawner to too limited for our purposes, i.e. direction and route
                        new GtuSpawner().spawnGtu(id, this.gtuCharacteristics, this.network, initSpeed, lanePosition);

                    }
                    else if ("DELETE".equals(message.getMessageTypeId()))
                    {
                        // String id

                    }
                    else if ("NETWORK".equals(message.getMessageTypeId()))
                    {
                        System.out.println("Ots received NETWORK message");
                        this.lastNetworkMessage = message;
                        setupSimulation();
                    }
                    else if ("START".equals(message.getMessageTypeId()))
                    {
                        System.out.println("Ots received START message");
                        if (this.simulator != null && !this.simulator.isStartingOrRunning())
                        {
                            this.simulator.start();
                        }
                    }
                    else if ("STOP".equals(message.getMessageTypeId()))
                    {
                        System.out.println("Ots received STOP message");
                        if (this.simulator != null && !this.simulator.isStoppingOrStopped())
                        {
                            this.simulator.stop();
                            this.network = null;
                        }
                        if (this.app != null)
                        {
                            this.app.dispose();
                            this.app = null;
                        }
                    }
                    else if ("RESET".equals(message.getMessageTypeId()))
                    {
                        System.out.println("Ots received RESET message");
                        setupSimulation();
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
            System.out.println("Ots terminated");
            System.exit(0);
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
            this.externalGtuIds.clear();

            if (this.app != null)
            {
                this.app.dispose();
                this.app = null;
            }

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
                OtsAnimationPanel animationPanel = new OtsAnimationPanel(this.network.getExtent(), new Dimension(100, 100),
                        this.simulator, model, OtsSwingApplication.DEFAULT_COLORER, this.network);
                animationPanel.enableSimulationControlButtons();
                this.app = new OtsSimulationApplication<AbstractOtsModel>(model, animationPanel);
            }

            Speed gtuSpeed = new Speed(50, SpeedUnit.KM_PER_HOUR);
            Lane lane = ((CrossSectionLink) this.network.getLink("AB")).getLanes().get(1);

            // Spawn Fellow 1
            Length f1Position = Length.instantiateSI(20.0);
            LanePosition f1Laneposition = new LanePosition(lane, f1Position);

            GtuSpawner f1Gtuspawner = new GtuSpawner();
            f1Gtuspawner.spawnGtu("Fellow 1", gtuCharacteristics, this.network, gtuSpeed, f1Laneposition);

            // Spawn Fellow 2
            Length f2Position = Length.instantiateSI(10.0);
            LanePosition f2Laneposition = new LanePosition(lane, f2Position);

            GtuSpawner f2Gtuspawner = new GtuSpawner();
            f2Gtuspawner.spawnGtu("Fellow 2", gtuCharacteristics, this.network, gtuSpeed, f2Laneposition);
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
                sentOperationalPlanMessage(event);
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
                this.externalGtuIds.remove(gtuId);
                sentDeleteMessage(gtuId);
            }
        }

        /**
         * Sent operational plan, if the GTU is not externally controlled.
         * @param event event
         * @throws RemoteException exception
         */
        private void sentOperationalPlanMessage(final Event event) throws RemoteException
        {
            String gtuId = (String) ((Object[]) event.getContent())[0];
            if (this.externalGtuIds.contains(gtuId))
            {
                return;
            }

            Gtu gtu = this.network.getGTU(gtuId);
            OperationalPlan plan = gtu.getOperationalPlan();
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
                this.responder.send(Sim0MQMessage.encodeUTF8(OtsTransceiver.this.bigEndian, OtsTransceiver.this.federation,
                        OtsTransceiver.this.ots, OtsTransceiver.this.client, "PLAN", this.messageId++, payload), 0);
                System.out.println(this.simulator.getSimulatorTime() + ": Ots sent PLAN message for GTU " + gtuId);
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
                this.responder.send(Sim0MQMessage.encodeUTF8(OtsTransceiver.this.bigEndian, OtsTransceiver.this.federation,
                        OtsTransceiver.this.ots, OtsTransceiver.this.client, "DELETE", this.messageId++, payload), 0);
                System.out.println(this.simulator.getSimulatorTime() + ": Ots sent DELETE message for GTU " + gtuId);
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
