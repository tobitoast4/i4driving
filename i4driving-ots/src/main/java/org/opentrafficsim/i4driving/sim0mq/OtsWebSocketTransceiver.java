package org.opentrafficsim.i4driving.sim0mq;

import com.google.gson.Gson;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.language.DsolException;
import org.djunits.unit.AccelerationUnit;
import org.djunits.unit.DirectionUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.*;
import org.djutils.cli.CliUtil;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.djutils.event.EventType;
import org.djutils.immutablecollections.ImmutableList;
import org.djutils.logger.CategoryLogger;
import org.djutils.serialization.TypedMessage;
import org.djutils.serialization.serializers.Serializer;
import org.json.JSONObject;
import org.opentrafficsim.animation.colorer.SynchronizationColorer;
import org.opentrafficsim.animation.gtu.colorer.*;
import org.opentrafficsim.base.parameters.ParameterType;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.AbstractOtsModel;
import org.opentrafficsim.core.dsol.OtsAnimator;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.geometry.OtsLine2d;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlan;
import org.opentrafficsim.core.gtu.plan.operational.Segment;
import org.opentrafficsim.core.network.Network;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.core.perception.HistoryManagerDevs;
import org.opentrafficsim.draw.OtsDrawingException;
import org.opentrafficsim.i4driving.messages.Commands;
import org.opentrafficsim.i4driving.messages.DefaultGson;
import org.opentrafficsim.i4driving.object.ActiveModeCrossing;
import org.opentrafficsim.i4driving.tactical.CommandsHandler;
import org.opentrafficsim.i4driving.tactical.NetworkUtil;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.swing.gui.OtsAnimationPanel;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;
import org.xml.sax.SAXException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * OTS co-simulation server.
 * @author wjschakel
 */
@Command(description = "OTS Transceiver for co-simulation", name = "OTS", mixinStandardHelpOptions = true,
        showDefaultValues = true, version = "20250619")
public class OtsWebSocketTransceiver implements EventListener, WebSocketListener
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
    @Option(names = "--no-gui", description = "Whether to show GUI", defaultValue = "false", negatable = true) // false=default
    private boolean showGui = true;

    /** ID prefix of generated GTUs. */
    @Option(names = "--idPrefix", description = "Prefix to ID of generated traffic.", defaultValue = "OTS_")
    private String idPrefix;

    /** Mixed in model arguments. */
    @Mixin
    private ScenarioTacticalPlannerFactory tacticalFactory = new ScenarioTacticalPlannerFactory();

    private WebSocketClient webSocketClient;

    /**
     * Constructor.
     * @param args command line arguments.
     * @throws Exception on any exception during simulation.
     */
    protected OtsWebSocketTransceiver(final String... args) throws Exception
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
        new OtsWebSocketTransceiver(args).start();
    }

    /**
     * Starts worker thread.
     */
    private void start()
    {
        try {
            URI uri = new URI("ws://localhost:8099");
            webSocketClient = new WebSocketClient(uri);
            webSocketClient.setListener(this);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        setupSimulation();
    }


    /** */
    private static final long serialVersionUID = 20241210L;

    /** Last routes sent. */
    private RoutesJson lastRoutesJson;

    /** GTU characteristics generator. */
    private LaneBasedGtuCharacteristicsGeneratorOd characteristicsGeneratorOd;

    /** Parameter factory. */
    private ParameterFactorySim0mq parameterFactory;

    /** GTU spawner. */
    private GtuSpawnerOd gtuSpawner;

    /** Simulator. */
    private OtsAnimator simulator;

    /** Network. */
    private RoadNetwork network;

    /** Application. */
    private OtsSimulationApplication<AbstractOtsModel> app;

    /** GTU that is being externally generated, for which no VEHICLE message should be sent. */
    private String externallyGeneratedGtuId;

    /** Ids of GTUs for which plan messages are sent. */
    private Set<String> planGtuIds = new LinkedHashSet<>();

    /** Ids of GTUs that are externally controlled. */
    private Set<String> externalGtuIds = new LinkedHashSet<>();

    /** Ids of active mode objects, and to which crossing they pertain. */
    private Map<String, ActiveModeCrossing> activeIds = new LinkedHashMap<>();

    /** Command handlers. */
    private Map<String, CommandsHandler> commandHandlers = new LinkedHashMap<>();

    /** Ids of GTUs that are externally deleted, i.e. do not sent back a delete message. */
    private Set<String> deleteGtuIds = new LinkedHashSet<>();

    /** GSON builder to parser JSON strings. */
    private Gson gson = DefaultGson.GSON;

    /** Run until time. */
    private Duration runUntil;

    /** {@inheritDoc} */
    @Override
    public void onEvent(JSONObject data)
    {
        try
        {
            String messageType = data.getString("type");
            JSONObject messageData = data.getJSONObject("data");
            if ("EXTERNAL".equals(messageType))  // update GTU
            {
                String id = messageData.getString("id");
                Length x = new Length(messageData.getDouble("x"), LengthUnit.METER);
                Length y = new Length(messageData.getDouble("y"), LengthUnit.METER);
                Direction direction = new Direction(messageData.getDouble("direction"), DirectionUnit.EAST_RADIAN);
                Speed speed = new Speed(messageData.getDouble("speed"), SpeedUnit.KM_PER_HOUR);
                Acceleration acceleration = new Acceleration(messageData.getDouble("acceleration"), AccelerationUnit.METER_PER_SECOND_2);
                OrientedPoint2d loc = new OrientedPoint2d(x.si, y.si, direction.si);
                if (this.activeIds.containsKey(id))
                {
                    this.simulator.scheduleEventNow(this, "updateActiveModeObject", new Object[] {id, loc, speed});
                }
                else
                {
                    this.simulator.scheduleEventNow(this, "scheduledDeadReckoning",
                            new Object[] {id, loc, speed, acceleration});
                }
            }
            else if ("VEHICLE".equals(messageType))  // create GTU
            {
                String id = messageData.getString("id");
                CategoryLogger.always().debug("Ots received VEHICLE message for GTU " + id);
                generateVehicle(messageData);
            }
//            else if ("MODE".equals(messageType))
//            {
//                String id = messageData.getString("id");
//                CategoryLogger.always().debug("Ots received MODE message for GTU " + id);
//                String mode = (String) payload[9];
//                this.simulator.scheduleEventNow(this, "scheduledChangeControlMode", new Object[] {id, mode});
//            }
//            else if ("COMMAND".equals(messageType))
//            {
//                String id = messageData.getString("id");
//                CategoryLogger.always().debug("Ots received COMMAND message for GTU " + id);
//                String json = (String) payload[9];
//                this.simulator.scheduleEventNow(this, "scheduledPerformCommand", new Object[] {id, json});
//            }
            else if ("DELETE".equals(messageType))
            {
                String id = messageData.getString("id");
                CategoryLogger.always().debug("Ots received DELETE message for GTU " + id);
                this.deleteGtuIds.add(id);
                this.simulator.scheduleEventNow(this, "scheduledDelete", new Object[] {id});
            }
//            else if ("ROUTES".equals(messageType))
//            {
//                CategoryLogger.always().debug("Ots received ROUTES message");
//                Object[] payload = message.createObjectArray();
//                this.lastRoutesJson = DefaultGson.GSON.fromJson((String) payload[8], RoutesJson.class);
//            }
//            else if ("ODMATRIX".equals(messageType))
//            {
//                CategoryLogger.always().debug("Ots received ODMATRIX message");
//                Object[] payload = message.createObjectArray();
//                this.lastOdJson = DefaultGson.GSON.fromJson((String) payload[8], OdMatrixJson.class);
//            }
//            else if ("NETWORK".equals(messageType))
//            {
//                CategoryLogger.always().debug("Ots received NETWORK message");
//                this.lastNetworkMessage = message;
//                setupSimulation();
//            }
            else if ("START".equals(messageType))
            {
                CategoryLogger.always().debug("Ots received START message");
                this.simulator.setSpeedFactor(1.0);
                if (this.simulator != null && !this.simulator.isStartingOrRunning())
                {
                    this.simulator.start();
                }
            }
            else if ("STOP".equals(messageType))
            {
                CategoryLogger.always().debug("Ots received STOP message");
                stopSimulation();
                clearSimulationSetupData();
            }
            else if ("RESET".equals(messageType))
            {
                CategoryLogger.always().debug("Ots received RESET message");
                setupSimulation();
            }
            else if ("TERMINATE".equals(messageType))
            {
                CategoryLogger.always().debug("Ots received TERMINATE message");
                stopSimulation();
                clearSimulationSetupData();
            }
//            else if ("PROGRESS".equals(messageType))
//            {
//                Object[] payload = message.createObjectArray();
//                Duration until = (Duration) payload[8];
//                CategoryLogger.always().debug("Ots received PROGRESS message until {}", until);
//                this.simulator.setSpeedFactor(1000.0);
//                while (this.simulator.isStartingOrRunning())
//                {
//                    try
//                    {
//                        // Simulator is still stopping from previous step
//                        System.out.println("Waiting for next PROGRESS");
//                        Thread.sleep(3);
//                    }
//                    catch (InterruptedException e)
//                    {
//                    }
//                }
//                this.simulator.runUpToAndIncluding(until);
//                this.progressMessageId = (int) payload[6];
//                this.runUntil = until;
//            }
            else
            {
                System.err.println("Cannot process a " + messageType + " message.");
            }
        }
//        catch (Sim0MQException | SerializationException | NumberFormatException | GtuException | OtsGeometryException
        catch (NumberFormatException | GtuException | OtsGeometryException | NetworkException
               | RemoteException | SimRuntimeException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        CategoryLogger.always().debug("Ots terminated");
        System.exit(0);
    }

    /**
     * Generates vehicle.
     * @param messageData message payload
     * @throws GtuException exception
     * @throws OtsGeometryException exception
     * @throws NetworkException exception
     * @throws RemoteException exception
     * @throws InvocationTargetException if a parameter cannot be set
     * @throws IllegalAccessException if a parameter cannot be set
     */
    private void generateVehicle(final JSONObject messageData) throws GtuException,
            OtsGeometryException, NetworkException, RemoteException, IllegalAccessException, InvocationTargetException
    {
        boolean running = this.simulator != null && this.simulator.getSimulatorTime().gt0();
        String id = messageData.getString("id");
        String mode = Utils.tryGetString(messageData, "mode", "external");
        Length initX = new Length(messageData.getDouble("x"), LengthUnit.METER);
        Length initY = new Length(messageData.getDouble("y"), LengthUnit.METER);
        Direction initDirection = new Direction(messageData.getDouble("direction"), DirectionUnit.EAST_RADIAN);
        OrientedPoint2d position = new OrientedPoint2d(initX.si, initY.si, initDirection.si);
        Speed initSpeed = new Speed(messageData.getDouble("speed"), SpeedUnit.KM_PER_HOUR);
//        Acceleration acceleration = new Acceleration(messageData.getDouble("acceleration"), AccelerationUnit.METER_PER_SECOND_2);
        if (mode.toLowerCase().equals("active"))
        {
            if (running)
            {
                this.simulator.scheduleEventNow(this, "addActiveModeObject", new Object[] {id, position, initSpeed});
            }
            else
            {
                addActiveModeObject(id, position, initSpeed);
            }
            return;
        }
        String vehicleType = Utils.tryGetString(messageData, "vehicleType", "NL.CAR");
        GtuType gtuType =
                Defaults.getByName(GtuType.class, vehicleType.startsWith("NL.") ? vehicleType : "NL." + vehicleType);
        Length vehicleLength = new Length(Utils.tryGetDouble(messageData, "length", 4.0), LengthUnit.METER);
        Length vehicleWidth = new Length(Utils.tryGetDouble(messageData, "width", 1.8), LengthUnit.METER);
        Length refToNose = new Length(Utils.tryGetDouble(messageData, "refToNose", 2), LengthUnit.METER);
        JSONObject jsonParameters = Utils.tryGetJSONObject(messageData, "parameters");

        Map<String, Object> parameterMap = new LinkedHashMap<>();
        if (jsonParameters != null) {   // apply the parameters
            Iterator<String> keys = jsonParameters.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonParameters.get(key);  // You can also use getString(), getInt(), etc. if type is known

                parameterMap.put(key, value);
            }
        }

        ArrayList<Node> routeNodes = new ArrayList<>();
        routeNodes.add(this.network.getNode("A"));
        routeNodes.add(this.network.getNode("B"));
        Route route = new Route("main", DefaultsNl.CAR, routeNodes);
//        String routId = "A-B";  // TODO: Get routeId dynamically by x / y
//        Route route = this.network.getRoute(routId);

        this.externallyGeneratedGtuId = id;
        if (running)
        {
            this.simulator.scheduleEventNow(this, "spawnGtu", new Object[] {id, gtuType, vehicleLength, vehicleWidth,
                    refToNose, route, initSpeed, position, mode, parameterMap});
        }
        else
        {
            spawnGtu(id, gtuType, vehicleLength, vehicleWidth, refToNose, route, initSpeed, position, mode, parameterMap);
        }
    }

    /**
     * Spawn GTU scheduled.
     * @param id id
     * @param gtuType GTU type
     * @param vehicleLength length
     * @param vehicleWidth width
     * @param refToNose distance from reference point to front
     * @param route route
     * @param initSpeed speed
     * @param position position
     * @param mode mode
     * @param parameterMap map of parameters
     * @param <T> helper casting type
     * @throws GtuException when initial GTU values are not correct
     * @throws OtsGeometryException when the initial path is wrong
     * @throws NetworkException when the GTU cannot be placed on the given position
     * @throws InvocationTargetException if a parameter cannot be set
     * @throws IllegalAccessException if a parameter cannot be set
     */
    @SuppressWarnings("checkstyle:parameternumber")
    private <T extends Enum<T>> void spawnGtu(final String id, final GtuType gtuType, final Length vehicleLength,
            final Length vehicleWidth, final Length refToNose, final Route route, final Speed initSpeed,
            final OrientedPoint2d position, final String mode, final Map<String, Object> parameterMap)
            throws GtuException, OtsGeometryException, NetworkException, IllegalAccessException, InvocationTargetException
    {
        Set<ParameterType<?>> setParameters = new LinkedHashSet<>();
        for (Entry<String, Object> parameterEntry : parameterMap.entrySet())
        {
            String parameter = parameterEntry.getKey();
            Object value = parameterEntry.getValue();
            boolean singleShot = false;
            if (parameter.startsWith("--"))
            {
                if (!singleShot)
                {
                    OtsWebSocketTransceiver.this.tacticalFactory.setSingleShotMode();
                    singleShot = true;
                }
                String methodName = "set" + parameter.substring(2).toLowerCase();
                for (Method method : ScenarioTacticalPlannerFactory.class.getMethods())
                {
                    if (method.getName().toLowerCase().equals(methodName) && method.getParameterTypes().length == 1)
                    {
                        if (method.getParameterTypes()[0].equals(boolean.class))
                        {
                            method.invoke(OtsWebSocketTransceiver.this.tacticalFactory, (boolean) value);
                        }
                        else if (method.getParameterTypes()[0].equals(int.class))
                        {
                            method.invoke(OtsWebSocketTransceiver.this.tacticalFactory, (int) value);
                        }
                        else if (method.getParameterTypes()[0].equals(double.class))
                        {
                            method.invoke(OtsWebSocketTransceiver.this.tacticalFactory, (double) value);
                        }
                        else if (method.getParameterTypes()[0].isEnum())
                        {
                            @SuppressWarnings("unchecked")
                            Class<T> clazz = (Class<T>) method.getParameterTypes()[0];
                            method.invoke(OtsWebSocketTransceiver.this.tacticalFactory, Enum.valueOf(clazz, (String) value));
                        }
                        else
                        {
                            CategoryLogger.always().warn("Unable to set parameter " + parameter);
                            break;
                        }
                    }
                }
            }
            else
            {
                setParameterValue(parameter, value, setParameters);
            }
        }
        /*-
         * Method notify() needs a GTU inside planGtuIds to send an OperationPlan to ExternalSim. For different modes:
         *  Ots: register here, spawn (plan is sent), schedule control mode change (without effect)
         *  Hybrid: spawn, schedule control mode change (starts dead reckoning, plan is sent)
         *  External: spawn, schedule control mode change (starts dead reckoning, never sent plan)
         * Note that dead reckoning can only be started after a spawn.
         */
        if (mode.toLowerCase().equals("ots"))
        {
            this.planGtuIds.add(id);
        }

//        Speed maximumSpeed = new Speed(200, SpeedUnit.KM_PER_HOUR);
//        Acceleration maximumAcceleration = new Acceleration(4, AccelerationUnit.METER_PER_SECOND_2);
//        Acceleration maximumDeceleration = new Acceleration(8, AccelerationUnit.METER_PER_SECOND_2);
//        LaneBasedGtu gtu = new LaneBasedGtu(id, gtuType, vehicleLength, vehicleWidth, maximumSpeed, vehicleLength.divide(2), network);
//
//        gtu.setMaximumAcceleration(maximumAcceleration);
//        gtu.setMaximumDeceleration(maximumDeceleration);
////        gtu.setVehicleModel(templateGtuType.getVehicleModel());
////        gtu.setNoLaneChangeDistance(this.noLaneChangeDistance);
////        gtu.setInstantaneousLaneChange(this.instantaneousLaneChanges);
////        gtu.setErrorHandler(this.errorHandler);
//
//        gtu.init(templateGtuType.getStrategicalPlannerFactory().create(gtu, templateGtuType.getRoute(),
//                templateGtuType.getOrigin(), templateGtuType.getDestination()), position, speed);

        this.gtuSpawner.spawnGtu(id, gtuType, vehicleLength, vehicleWidth, refToNose, route, initSpeed, position);
        OtsWebSocketTransceiver.this.tacticalFactory.resetMode();
        setParameters.forEach((p) -> this.parameterFactory.clearParameterValue(p));
        scheduledChangeControlMode(id, mode);
    }

    /**
     * Add active mode object.
     * @param id id
     * @param location location
     * @param speed speed
     * @throws NetworkException no valid location
     */
    private void addActiveModeObject(final String id, final OrientedPoint2d location, final Speed speed)
            throws NetworkException
    {
        LanePosition position = NetworkUtil.getLanePosition(this.network, location);
        ActiveModeCrossing crossing = new ActiveModeCrossing(position, false);
        this.activeIds.put(id, crossing);
        updateActiveModeObject(id, location, speed);
    }

    /**
     * Updates crossing arrival time of active mode object.
     * @param id id
     * @param location location
     * @param speed speed
     */
    private void updateActiveModeObject(final String id, final OrientedPoint2d location, final Speed speed)
    {
        ActiveModeCrossing crossing = this.activeIds.get(id);
        Length width = crossing.getLane().getWidth(crossing.getLongitudinalPosition());
        Length distance = Length.instantiateSI(Math.max(location.distance(crossing.getLocation()) - width.si / 2.0, 0.0));
        crossing.setArrival(id, distance, speed);
    }

    /**
     * Sets parameter in parameter factory.
     * @param <T> value type
     * @param parameter parameter type id
     * @param value value
     * @param setParameters set of parameters that are set, to which the parameter should be added
     */
    @SuppressWarnings("unchecked")
    private <T> void setParameterValue(final String parameter, final Object value,
            final Set<ParameterType<?>> setParameters)
    {
        ParameterType<T> param = (ParameterType<T>) Parameters.get(parameter);
        this.parameterFactory.setParameterValue(param, (T) value);
        setParameters.add(param);
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
     * Stops any running simulation. It leaves data to be able to reset the simulation.
     */
    private void stopSimulation()
    {
        if (this.simulator != null && !this.simulator.isStoppingOrStopped())
        {
            this.simulator.stop();
            this.simulator = null;
            this.network = null;
        }
        this.planGtuIds.clear();
        this.externalGtuIds.clear();
        this.commandHandlers.clear();
        if (this.app != null)
        {
            this.app.dispose();
            this.app = null;
        }
    }

    /**
     * Clears the information that is used to setup a simulation. This is the information that would be reused in a reset
     * message.
     */
    private void clearSimulationSetupData()
    {
        this.lastRoutesJson = null;
    }

    /**
     * Setup simulation.
     * @throws InvocationTargetException if a parameter cannot be set
     * @throws IllegalAccessException if a parameter cannot be set
     */
    private void setupSimulation()
    {
        try {

            stopSimulation();

            // An animator supports real-time running. No GUI will be shown if no animation panel is created.
            this.simulator = new OtsAnimator("Test animator");
            this.simulator.addListener(this, SimulatorInterface.STOP_EVENT);

            String simulationString = null;
            SimulationType simulationType = SimulationType.SILAB;

            CoSimModel model = new CoSimModel(this.simulator, simulationString, simulationType);
            Duration runtime = simulationType == null ? Duration.instantiateSI(60.0) : Duration.instantiateSI(36000.0);
            this.simulator.initialize(Time.ZERO, Duration.ZERO, runtime, model);
            this.simulator.getReplication().setHistoryManager(
                    new HistoryManagerDevs(this.simulator, Duration.instantiateSI(5.0), Duration.instantiateSI(10.0)));
            this.network = (RoadNetwork) model.getNetwork();
            this.characteristicsGeneratorOd = model.getSim0mqSimulation().getGtuCharacteristicsGeneratorOd();
            this.parameterFactory = model.getSim0mqSimulation().getParameterFactory();
            this.gtuSpawner = new GtuSpawnerOd(this.network, this.characteristicsGeneratorOd);

            listenToEvents();

            if (true)
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

            // Routes
            if (this.lastRoutesJson != null)
            {
                this.lastRoutesJson.createRoutes(this.network, DefaultsNl.VEHICLE, model.getSim0mqSimulation());
            }

            // OD background traffic with default model
    //        if (this.lastOdJson != null)
    //        {
    //            Collection<GtuType> gtuTypes = Set.of(DefaultsNl.CAR, DefaultsNl.VAN, DefaultsNl.BUS, DefaultsNl.TRUCK);
    //            OdMatrix od = this.lastOdJson.asOdMatrix(this.network, gtuTypes, model.getSim0mqSimulation());
    //            OdOptions odOptions = new OdOptions().set(OdOptions.GTU_TYPE, this.characteristicsGeneratorOd)
    //                    .set(OdOptions.GTU_ID, new IdGenerator(OtsWebSocketTransceiver.this.idPrefix));
    //            OdApplier.applyOd(this.network, od, odOptions, DefaultsRoadNl.ROAD_USERS);
    //        }

            JSONObject avVehicleData = new JSONObject();
            avVehicleData.put("id", "AV");
            avVehicleData.put("mode", "ots");
            avVehicleData.put("x", 10);
            avVehicleData.put("y", 1.6);
            avVehicleData.put("direction", 0);
            avVehicleData.put("speed", 0);
            generateVehicle(avVehicleData);
        }
        catch (NetworkException | RemoteException | DsolException | OtsDrawingException | SimRuntimeException | NamingException
               | OtsGeometryException | InvocationTargetException | GtuException | IllegalAccessException e)
        {

        }
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
            if (simulator.isStartingOrRunning()) {
                sendOperationalPlanMessage(gtuId);
            }
        }
        else if (eventType.equals(Network.GTU_ADD_EVENT))
        {
            String gtuId = (String) event.getContent();
            Gtu gtu = this.network.getGTU(gtuId);
            if (!gtuId.equals(this.externallyGeneratedGtuId))
            {
                this.simulator.scheduleEventNow(this, "sendVehicleMessage", new Object[] {gtu});
                this.planGtuIds.add(gtuId);
                this.externallyGeneratedGtuId = null;
            }
            gtu.addListener(this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
        }
        else if (eventType.equals(Network.GTU_REMOVE_EVENT))
        {
            String gtuId = (String) event.getContent();
            Gtu gtu = this.network.getGTU(gtuId);
            gtu.removeListener(this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
            this.planGtuIds.remove(gtuId);
            this.externalGtuIds.remove(gtuId);
            ActiveModeCrossing crossing = this.activeIds.remove(gtuId);
            if (crossing != null)
            {
                crossing.removeArrival(gtuId);
            }
            this.commandHandlers.remove(gtuId);
            if (!this.deleteGtuIds.remove(gtuId))
            {
                sendDeleteMessage(gtuId);
            }
        }
        else if (eventType.equals(SimulatorInterface.STOP_EVENT))
        {
            if (this.runUntil != null && this.simulator.getSimulatorTime().eq(this.runUntil))
            {
                this.runUntil = null;
            }
            // if not, stopped for some other reason, perhaps a stop button in the GUI
        }
    }

    /**
     * Sent VEHICLE message.
     * @param gtu
     */
    @SuppressWarnings("unused") // scheduled
    private void sendVehicleMessage(final Gtu gtu)
    {
        String gtuId = gtu.getId();
        OrientedPoint2d p = gtu.getLocation();
        String routeId = gtu.getStrategicalPlanner().getRoute().getId();
        JSONObject vehicleData = new JSONObject();
        vehicleData.put("id", gtuId);
        vehicleData.put("x", p.x);
        vehicleData.put("y", p.y);
        vehicleData.put("dirZ", p.dirZ);
        vehicleData.put("speed", gtu.getSpeed().getInUnit(SpeedUnit.KM_PER_HOUR));
        vehicleData.put("type", gtu.getType().getId());
        vehicleData.put("length", gtu.getLength().si);
        vehicleData.put("width", gtu.getWidth().si);
        vehicleData.put("width", gtu.getWidth().si);
        vehicleData.put("front", gtu.getFront().dx().si);
        vehicleData.put("routeId", routeId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "VEHICLE");
        jsonObject.put("data", vehicleData);
        String msg = jsonObject.toString();
        webSocketClient.sendMessage(msg);
        String log = String.format("[%.3fs] Ots sent VEHICLE message for GTU %s on route %s",
                this.simulator.getSimulatorTime().si, gtuId, routeId);
        System.out.println(log);
    }

    /**
     * Send operational plan.
     * @param gtuId GTU id
     * @throws RemoteException exception
     */
    private void sendOperationalPlanMessage(final String gtuId) throws RemoteException
    {
        LaneBasedGtu gtu = (LaneBasedGtu) this.network.getGTU(gtuId);
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

//        Object[] payload = new Object[6];
//        payload[0] = gtuId;
//        payload[1] = speed;
//        payload[2] = new FloatLengthVector(x);
//        payload[3] = new FloatLengthVector(y);
//        payload[4] = new FloatDurationVector(t);
//        payload[5] = new FloatAccelerationVector(a);

        OrientedPoint2d position = gtu.getLocation();
        double gtuspeed = gtu.getSpeed().si;
        double acceleration = gtu.getAcceleration().getSI();
        boolean isBrakingLightsOn = false;
        try {
            isBrakingLightsOn = gtu.isBrakingLightsOn();
        } catch (Exception e) {
            System.out.println(e);
        }
        String turnIndicatorStatus = gtu.getTurnIndicatorStatus().name();
        JSONObject dataJson = new JSONObject();
        dataJson.put("speed", gtuspeed);
        dataJson.put("acceleration", acceleration);
        dataJson.put("isBrakingLightsOn", isBrakingLightsOn);
        dataJson.put("turnIndicatorStatus", turnIndicatorStatus);
        dataJson.put("laneChange", "");
        JSONObject positionJson = new JSONObject();
        positionJson.put("x", position.getX());
        positionJson.put("y", position.getY());
        dataJson.put("position", positionJson);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "PLAN");
        jsonObject.put("data", dataJson);
        webSocketClient.sendMessage(jsonObject.toString());
        String log = String.format("[%.3fs] Ots sent PLAN message for GTU %s", this.simulator.getSimulatorTime().si, gtuId);
        System.out.println(log);
    }

    /**
     * Send delete message to external sim.
     * @param gtuId GTU id
     * @throws RemoteException exception
     */
    private void sendDeleteMessage(final String gtuId) throws RemoteException
    {
        JSONObject vehicleData = new JSONObject();
        vehicleData.put("id", gtuId);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "DELETE");
        jsonObject.put("data", vehicleData);
        String msg = jsonObject.toString();
        webSocketClient.sendMessage(msg);

        String log = String.format("[%.3fs] Ots sent DELETE message for GTU %s", this.simulator.getSimulatorTime().si, gtuId);
        System.out.println(log);
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

    /**
     * Co-simulation model. This intermediates between an OTS model, and the different supported network/OD types.
     */
    private final class CoSimModel extends AbstractOtsModel
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** String definition of the network. */
        private final String simulationString;

        /** Network type. */
        private final SimulationType simulationType;

        /** Simulation. */
        private Sim0mqSimulation simulation;

        /**
         * Constructor.
         * @param simulator simulator
         * @param simulationString network string
         * @param simulationType network type
         */
        private CoSimModel(final OtsSimulatorInterface simulator, final String simulationString,
                final SimulationType simulationType)
        {
            super(simulator);
            this.simulationString = simulationString;
            this.simulationType = simulationType;
        }

        @Override
        public Network getNetwork()
        {
            return this.simulation.getNetwork();
        }

        /**
         * Returns the sim0mq simulation.
         * @return sim0mq simulation
         */
        public Sim0mqSimulation getSim0mqSimulation()
        {
            return this.simulation;
        }

        @Override
        public void constructModel() throws SimRuntimeException
        {
            try
            {
                if (this.simulationString == null)
                {
                    this.simulation = new TwoLaneTestSimulation(getSimulator(), OtsWebSocketTransceiver.this.tacticalFactory);
                }
                else
                {
                    switch (this.simulationType)
                    {
                        case OPEN_DRIVE:
                            this.simulation = new OpenDriveSimulation(this.simulator, OtsWebSocketTransceiver.this.tacticalFactory,
                                    this.simulationString);
                            break;
                        case FOSIM:
                            // TODO parse Fosim string
                            // break;
                        case OTS:
                            // TODO parse OTS string
                            // break;
                        default:
                            throw new SimRuntimeException("Network type " + this.simulationType + " is not supported.");
                    }
                }
            }
            catch (GtuException | OtsGeometryException | NetworkException | JAXBException | SAXException
                    | ParserConfigurationException ex)
            {
                throw new SimRuntimeException(ex);
            }
        }

    }

    /** All the converters that decode into arrays and matrices of Objects, keyed by prefix. */
    private static final Map<Byte, Serializer<?>> OBJECT_DECODERS = new HashMap<>();

    static
    {
        try
        {
            Field field = TypedMessage.class.getDeclaredField("OBJECT_DECODERS");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Byte, Serializer<?>> map = (Map<Byte, Serializer<?>>) field.get(TypedMessage.class);
            map.forEach((b, s) ->
            {
                OBJECT_DECODERS.put(b, s);
                OBJECT_DECODERS.put((byte) (b - 128), s);
            });
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Queued messages.
     * @param message bytes of the message
     * @param log log entry to print when the message is sent
     */
    private record QueuedMessage(byte[] message, String log)
    {
    }

    /**
     * Simulation type, defining how the simulation string containing the network and optionally demand is parsed.
     */
    private enum SimulationType
    {
        /** OpenDRIVE network with optional separate JSON OD matrix. */
        SILAB,

        /** OpenDRIVE network with optional separate JSON OD matrix. */
        OPEN_DRIVE,

        /** FOSIM simulation file. */
        FOSIM,

        /** OTS XML simulation. */
        OTS;
    }

}
