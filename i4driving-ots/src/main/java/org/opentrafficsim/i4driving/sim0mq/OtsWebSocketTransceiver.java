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
import org.djutils.event.EventListener;
import org.djutils.logger.CategoryLogger;
import org.json.JSONArray;
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
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
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
import org.pmw.tinylog.Level;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import javax.naming.NamingException;
import java.awt.*;
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
    /** Port number. */
    @Option(names = "--port", description = "Port number", defaultValue = "5556")
    private int port;

    /** Show GUI. */
    @Option(names = "--no-gui", description = "Whether to show GUI", defaultValue = "false", negatable = true) // false=default
    private boolean showGui = true;

    /** Mixed in model arguments. */
    @Mixin
    private ScenarioTacticalPlannerFactory tacticalFactory = new ScenarioTacticalPlannerFactory();


    /** */
    private static final long serialVersionUID = 20241210L;

    /** GTU characteristics generator. */
    private LaneBasedGtuCharacteristicsGeneratorOd characteristicsGeneratorOd;
    private ParameterFactorySim0mq parameterFactory;
    private GtuSpawnerOd gtuSpawner;
    private OtsAnimator simulator;
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

    private WebSocketClient webSocketClient;
    private String laneChange = "";

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
        CategoryLogger.setAllLogLevel(Level.INFO);
        CategoryLogger.setAllLogMessageFormat("[{date: YYYY-MM-dd HH:mm:ss.SSS}] {level}: {message}");
        new OtsWebSocketTransceiver(args).start();
    }

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

            CoSimModel model = new CoSimModel(this.simulator);
            Duration runtime = Duration.instantiateSI(36000.0);
            this.simulator.initialize(Time.ZERO, Duration.ZERO, runtime, model);
            this.simulator.getReplication().setHistoryManager(
                    new HistoryManagerDevs(this.simulator, Duration.instantiateSI(5.0), Duration.instantiateSI(10.0)));
            this.network = (RoadNetwork) model.getNetwork();
            this.characteristicsGeneratorOd = model.getSim0mqSimulation().getGtuCharacteristicsGeneratorOd();
            this.parameterFactory = model.getSim0mqSimulation().getParameterFactory();
            this.gtuSpawner = new GtuSpawnerOd(this.network, this.characteristicsGeneratorOd);

            this.network.addListener(this, Network.GTU_ADD_EVENT);
            this.network.addListener(this, Network.GTU_REMOVE_EVENT);

            new OtsWebSocketTransceiver.Worker().start();

            boolean showGui = true;
            if (showGui)
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
            JSONObject avData = new JSONObject();
            avData.put("id", "AV");
            avData.put("mode", "ots");
            JSONObject avPosition = new JSONObject();
            avPosition.put("x", 10);
            avPosition.put("y", 7.125);
            avData.put("position", avPosition);
            JSONObject avRotation = new JSONObject();
            avRotation.put("z", 0);
            avData.put("rotation", avRotation);
            avData.put("v", 0);
            generateVehicle(avData);
        }
        catch (NetworkException | RemoteException | DsolException | OtsDrawingException | SimRuntimeException | NamingException
               | OtsGeometryException | InvocationTargetException | GtuException | IllegalAccessException e)
        {

        }
    }

    /** {@inheritDoc} */
    @Override
    public void onEvent(JSONObject data)
    {
        try
        {
            String messageType = data.getString("type");
            JSONObject messageData = Utils.tryGetJSONObject(data, "data");

            if ("OBJECTS".equals(messageType)) {
                JSONArray objects = messageData.getJSONArray("objects");
                for (int i = 0; i < objects.length(); i++) {
                    JSONObject odbObject = objects.getJSONObject(i);
                    String id = odbObject.getString("id");
                    String name = odbObject.getString("name");

                    if (name.startsWith("sign.")) {

                    } else if (name.startsWith("scnx.road.superstructures.objects.post")) {

                    } else if (name.startsWith("Vehicles.")) {
                        if (name.equals("Vehicles.V600.Fiat500.main")) {
                            continue; // TODO make this better
                        }
                        if (this.network != null) {
                            Gtu gtu = this.network.getGTU(id);
                            if (gtu == null) {
                                generateVehicle(odbObject);
                            } else {
                                updateVehicle(odbObject);
                            }
                        }
                    }

                }
            }
//            else if  ("EXTERNAL".equals(messageType))  // update GTU
//            {
//                updateVehicle(messageData);
//            }
//            else if ("VEHICLE".equals(messageType))  // create GTU
//            {
//                generateVehicle(messageData);
//            }
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
//                Object[] payload = message.createObjectArray();
//                this.lastRoutesJson = DefaultGson.GSON.fromJson((String) payload[8], RoutesJson.class);
//            }
//            else if ("ODMATRIX".equals(messageType))
//            {
//                Object[] payload = message.createObjectArray();
//                this.lastOdJson = DefaultGson.GSON.fromJson((String) payload[8], OdMatrixJson.class);
//            }
//            else if ("NETWORK".equals(messageType))
//            {
//                this.lastNetworkMessage = message;
//                setupSimulation();
//            }
            else if ("START".equals(messageType))
            {
                this.simulator.setSpeedFactor(1.0);
                if (this.simulator != null && !this.simulator.isStartingOrRunning())
                {
                    this.simulator.start();
                }
            }
            else if ("PAUSE".equals(messageType))
            {
                try {
                    this.simulator.stop();
                } catch (SimRuntimeException e) {
                    // Simulator already stopped
                }
            }
            else if ("RESET".equals(messageType))
            {
                setupSimulation();
            }
            else if ("TERMINATE".equals(messageType))
            {
                stopSimulation();
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
//        CategoryLogger.always().debug("Ots terminated");
//        System.exit(0);
    }

    private void updateVehicle(JSONObject messageData) {
        String id = messageData.getString("id");
        Length x = new Length(messageData.getJSONObject("position").getDouble("x"), LengthUnit.METER);
        Length y = new Length(messageData.getJSONObject("position").getDouble("y"), LengthUnit.METER);
        Direction direction = new Direction(messageData.getJSONObject("rotation").getDouble("z"), DirectionUnit.EAST_RADIAN);
        Speed speed = new Speed(messageData.getDouble("v"), SpeedUnit.KM_PER_HOUR);
//        Acceleration acceleration = new Acceleration(messageData.getDouble("acceleration"), AccelerationUnit.METER_PER_SECOND_2);
        Acceleration acceleration = new Acceleration(0, AccelerationUnit.METER_PER_SECOND_2);
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

    /**
     * Generates vehicle.
     * @throws InvocationTargetException if a parameter cannot be set
     * @throws IllegalAccessException if a parameter cannot be set
     */
    private void generateVehicle(final JSONObject messageData) throws GtuException,
            OtsGeometryException, NetworkException, RemoteException, IllegalAccessException, InvocationTargetException
    {
        boolean running = this.simulator != null && this.simulator.getSimulatorTime().gt0();
        String id = messageData.getString("id");

        String mode = Utils.tryGetString(messageData, "mode", "external");
        Length initX = new Length(messageData.getJSONObject("position").getDouble("x"), LengthUnit.METER);
        Length initY = new Length(messageData.getJSONObject("position").getDouble("y"), LengthUnit.METER);
        Direction initDirection = new Direction(messageData.getJSONObject("rotation").getDouble("z"), DirectionUnit.EAST_RADIAN);
        OrientedPoint2d position = new OrientedPoint2d(initX.si, initY.si, initDirection.si);
        Speed initSpeed = new Speed(messageData.getDouble("v"), SpeedUnit.KM_PER_HOUR);
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

        Gtu gtu = this.network.getGTU(id);
        if (gtu == null) {  // somehow this is not guaranteed to this point, so we check again
            this.gtuSpawner.spawnGtu(id, gtuType, vehicleLength, vehicleWidth, refToNose, route, initSpeed, position);
        }
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

//    @Override
//    public void notify(final Event event) throws RemoteException
//    {
//        EventType eventType = event.getType();
//        if (eventType.equals(LaneBasedGtu.LANEBASED_MOVE_EVENT))
//        {
//            Object[] payload = (Object[]) event.getContent();
//            String gtuId = (String) payload[0];
//            if (!this.planGtuIds.contains(gtuId))
//            {
//                return;
//            }
//            if (simulator.isStartingOrRunning()) {
//                sendOperationalPlanMessage(payload);
//            }
//        }
//        else if (eventType.equals(Network.GTU_ADD_EVENT))
//        {
//            String gtuId = (String) event.getContent();
//            Gtu gtu = this.network.getGTU(gtuId);
//            if (!gtuId.equals(this.externallyGeneratedGtuId))
//            {
//                this.planGtuIds.add(gtuId);
//                this.externallyGeneratedGtuId = null;
//            }
//            gtu.addListener(this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
//        }
//        else if (eventType.equals(Network.GTU_REMOVE_EVENT))
//        {
//            String gtuId = (String) event.getContent();
//            Gtu gtu = this.network.getGTU(gtuId);
//            gtu.removeListener(this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
//            this.planGtuIds.remove(gtuId);
//            this.externalGtuIds.remove(gtuId);
//            ActiveModeCrossing crossing = this.activeIds.remove(gtuId);
//            if (crossing != null)
//            {
//                crossing.removeArrival(gtuId);
//            }
//            this.commandHandlers.remove(gtuId);
//            if (!this.deleteGtuIds.remove(gtuId))
//            {
//                sendDeleteMessage(gtuId);
//            }
//        }
//        else if (eventType.equals(SimulatorInterface.STOP_EVENT))
//        {
//            if (this.runUntil != null && this.simulator.getSimulatorTime().eq(this.runUntil))
//            {
//                this.runUntil = null;
//            }
//            // if not, stopped for some other reason, perhaps a stop button in the GUI
//        }
//    }

    public void notify(final org.djutils.event.Event event) throws RemoteException
    {
        if (event.getType().equals(LaneBasedGtu.LANEBASED_MOVE_EVENT))
        {
            Object[] payload = (Object[]) event.getContent();
            String laneChangeDirection = (String) payload[5];  // this can either be "RIGHT" / "LEFT" / "NONE"
            if (!laneChangeDirection.equals("") && !laneChangeDirection.equals("NONE")) {
                laneChange = laneChangeDirection;
                CategoryLogger.always().info(laneChange);
            }
        }
    }

    /**
     * Worker thread to send messages.
     */
    protected class Worker extends Thread
    {
        private LaneBasedGtu gtuAV = null;

        @Override
        public void run()
        {
            try
            {
                // Note on synchronicity and possible dead-locks:
                // OTS is single-threaded. All changes during the simulation should be scheduled in the simulator. All messages
                // sent back from a notification from simulation, should be queued for the Worker thread in the queue.
                while (true)
                {
                    if (this.gtuAV == null) {
                        gtuAV = (LaneBasedGtu) OtsWebSocketTransceiver.this.network.getGTU("AV");
                        if (gtuAV != null) {
                            gtuAV.addListener(OtsWebSocketTransceiver.this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
                        }
                    }
                    if (!simulator.isStartingOrRunning() || gtuAV.isDestroyed()) {
                        continue;
                    }

                    OrientedPoint2d position = gtuAV.getLocation();
                    double acceleration = gtuAV.getAcceleration().getSI();
                    double speed = gtuAV.getSpeed().getSI();
                    boolean isBrakingLightsOn = false;
                    try {
                        isBrakingLightsOn = gtuAV.isBrakingLightsOn();
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    String turnIndicatorStatus = gtuAV.getTurnIndicatorStatus().name();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("speed", speed);
                    dataJson.put("acceleration", acceleration);
                    dataJson.put("isBrakingLightsOn", isBrakingLightsOn);
                    dataJson.put("turnIndicatorStatus", turnIndicatorStatus);
                    dataJson.put("laneChange", OtsWebSocketTransceiver.this.laneChange);
                    laneChange = "";  // the request for lane change was send --> reset
                    JSONObject positionJson = new JSONObject();
                    positionJson.put("x", position.getX());
                    positionJson.put("y", position.getY());
                    dataJson.put("position", positionJson);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "PLAN");
                    jsonObject.put("data", dataJson);
                    webSocketClient.sendMessage(jsonObject.toString());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

//    private void sendOperationalPlanMessage(final Object[] payload) throws RemoteException
//    {
//        String gtuId = (String) payload[0];
//        LaneBasedGtu gtu = (LaneBasedGtu) this.network.getGTU(gtuId);
//        OperationalPlan plan = ((ScenarioTacticalPlanner) gtu.getTacticalPlanner()).pullLastIntendedPlan();
//        if (plan == null)
//        {
//            // Do not sent plan upon a move triggered by external control
//            return;
//        }
//
//        String laneChange = "";
//        String laneChangeDirection = (String) payload[5];  // this can either be "RIGHT" / "LEFT" / "NONE"
//        if (!laneChangeDirection.equals("") && !laneChangeDirection.equals("NONE")) {
//            laneChange = laneChangeDirection;
//            CategoryLogger.always().debug("LaneChange:  " + laneChange);
//        }
//
//        OrientedPoint2d position = gtu.getLocation();
//        double gtuspeed = gtu.getSpeed().si;
//        double acceleration = gtu.getAcceleration().getSI();
//        boolean isBrakingLightsOn = false;
//        try {
//            isBrakingLightsOn = gtu.isBrakingLightsOn();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        String turnIndicatorStatus = gtu.getTurnIndicatorStatus().name();
//        JSONObject dataJson = new JSONObject();
//        dataJson.put("speed", gtuspeed);
//        dataJson.put("acceleration", acceleration);
//        dataJson.put("isBrakingLightsOn", isBrakingLightsOn);
//        dataJson.put("turnIndicatorStatus", turnIndicatorStatus);
//        dataJson.put("laneChange", laneChange);
//        JSONObject positionJson = new JSONObject();
//        positionJson.put("x", position.getX());
//        positionJson.put("y", position.getY());
//        dataJson.put("position", positionJson);
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("type", "PLAN");
//        jsonObject.put("data", dataJson);
//        webSocketClient.sendMessage(jsonObject.toString());
//        String log = String.format("[%.3fs] Ots sent PLAN message for GTU %s", this.simulator.getSimulatorTime().si, gtuId);
//        System.out.println(log);
//    }

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
     * Co-simulation model. This intermediates between an OTS model, and the different supported network/OD types.
     */
    private final class CoSimModel extends AbstractOtsModel
    {
        /** */
        private static final long serialVersionUID = 1L;

        /** Simulation. */
        private Sim0mqSimulation simulation;

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
                this.simulation = new SilabSimulation(getSimulator(), OtsWebSocketTransceiver.this.tacticalFactory);
            }
            catch (GtuException | OtsGeometryException | NetworkException ex)
            {
                throw new SimRuntimeException(ex);
            }
        }

    }
}
