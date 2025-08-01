package org.opentrafficsim.i4driving.sim0mq;

import com.google.gson.Gson;
import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
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
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlusFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.DefaultLmrsPerceptionFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.LmrsFactory;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalPlanner;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalPlannerFactory;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.Lane;
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

import static org.djunits.unit.LengthUnit.METER;
import static org.djunits.unit.SpeedUnit.KM_PER_HOUR;

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

    /** Ids of GTUs that are externally controlled. */
    private Set<String> externalGtuIds = new LinkedHashSet<>();

    /** Ids of active mode objects, and to which crossing they pertain. */
    private Map<String, ActiveModeCrossing> activeIds = new LinkedHashMap<>();

    /** Command handlers. */
    private Map<String, CommandsHandler> commandHandlers = new LinkedHashMap<>();

    /** GSON builder to parser JSON strings. */
    private Gson gson = DefaultGson.GSON;

    private WebSocketClient webSocketClient;
    private Worker avWorker;
    private String laneChange = "";
    private String avId;

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
        CategoryLogger.setAllLogLevel(Level.DEBUG);
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
            laneChange = "";
            avId = "AV";
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

            if (this.avWorker != null) {  // stop old bg thread if ther is one
                this.avWorker.exit();
            }
            this.avWorker = new OtsWebSocketTransceiver.Worker();
            this.avWorker.start();

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
            avData.put("id", avId);
            avData.put("mode", "ots");
            JSONObject avPosition = new JSONObject();
            avPosition.put("x", 10);
            avPosition.put("y", -7.125);
            avData.put("position", avPosition);
            JSONObject avRotation = new JSONObject();
            avRotation.put("z", 0);
            avData.put("rotation", avRotation);
            avData.put("v", 0);
            generateVehicle(avData);
            CategoryLogger.always().debug("Generate GTU AV");
        }
        catch (NetworkException | RemoteException | DsolException | OtsDrawingException | SimRuntimeException | NamingException
               | OtsGeometryException | InvocationTargetException | GtuException | IllegalAccessException e)
        {
//        catch (RemoteException | DsolException | OtsDrawingException | SimRuntimeException | NamingException e)
//            {

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

            if ("STATUS".equals(messageType))
            {
                String message = Utils.tryGetString(data, "data", "ERROR");
                System.out.println(message);
            }
            else if ("OBJECTS".equals(messageType))
            {
                Set<String> updatedGtuIds = new LinkedHashSet<>();
                JSONArray objects = messageData.getJSONArray("objects");
                for (int i = 0; i < objects.length(); i++) {
                    JSONObject odbObject = objects.getJSONObject(i);
                    String id = odbObject.getString("id");
                    String name = odbObject.getString("name");

                    if (name.startsWith("sign.")) {

                    } else if (name.startsWith("scnx.road.superstructures.objects.post")) {

                    } else if (name.startsWith("Vehicles.")) {
                        if (name.equals("Vehicles.V600.Fiat500.main")) {
                            double x = odbObject.getJSONObject("position").getDouble("x");
                            double y = odbObject.getJSONObject("position").getDouble("y");
                            double direction = odbObject.getJSONObject("rotation").getDouble("z");
                            OrientedPoint2d loc = new OrientedPoint2d(x, y, direction);
                            LaneBasedGtu avGtu = (LaneBasedGtu) this.network.getGTU(avId);
//                            ScenarioTacticalPlanner planner = getTacticalPlanner("AV");
                            System.out.println(avGtu.getLocation().distance(loc));
//                            if (avGtu.getLocation().distance(loc) > 1) {
//                                this.simulator.scheduleEventNow(this, "scheduledDelete", new Object[] {avId});
//                                avId = "AV" + avCount++;
//                                odbObject.put("id", avId);
//                                generateVehicle(odbObject);
//                            }
                        } else if (this.network != null) {
                            Gtu gtu = this.network.getGTU(id);
                            if (gtu == null) {
                                generateVehicle(odbObject);
                                CategoryLogger.always().debug("Generate GTU " + id + " of " + name);
                            } else {
                                updateVehicle(odbObject);
                            }
                        }
                        updatedGtuIds.add(id);
                    }
                }

                Set<String> gtuIdsToRemove = new LinkedHashSet<>();
                for (String id : externalGtuIds) {  // remove GTUs that are not in view anymore
                    if (!updatedGtuIds.contains(id) && !id.equals(avId)) {
                        this.simulator.scheduleEventNow(this, "scheduledDelete", new Object[] {id});
                        gtuIdsToRemove.add(id);
                    }
                }
                for (String id : gtuIdsToRemove) {
                    this.externalGtuIds.remove(id);
                }
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
                this.simulator.scheduleEventNow(this, "scheduledDelete", new Object[] {id});
            }
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
//        if (messageData.getDouble("v") == 0 && !id.equals("AV")) {
//            mode = "active";
//        }
        Speed initSpeed = new Speed(messageData.getDouble("v"), SpeedUnit.KM_PER_HOUR);
//        Acceleration acceleration = new Acceleration(messageData.getDouble("acceleration"), AccelerationUnit.METER_PER_SECOND_2);
        if (mode.toLowerCase().equals("active")) {
            if (running) {
                this.simulator.scheduleEventNow(this, "addActiveModeObject", new Object[] {id, position, initSpeed});
            } else {
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
        Gtu gtu = this.network.getGTU(id);
        if (gtu == null) {  // somehow this is not guaranteed to this point, so we check again
            this.gtuSpawner.spawnGtu(id, gtuType, vehicleLength, vehicleWidth, refToNose, route, initSpeed, position);
        }
        OtsWebSocketTransceiver.this.tacticalFactory.resetMode();
        setParameters.forEach((p) -> this.parameterFactory.clearParameterValue(p));
        scheduledChangeControlMode(id, mode);
    }

    /**
     * Add active mode object. (Objects that cross the streets)
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
        CategoryLogger.always().debug("Destroyed GTU " + id);
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
                if (this.externalGtuIds.remove(id))
                {
                    getTacticalPlanner(id).stopDeadReckoning();
                }
                break;
            }
            case "hybrid":
            {
                if (this.externalGtuIds.add(id))
                {
                    if (getTacticalPlanner(id) != null) {
                        getTacticalPlanner(id).startDeadReckoning();
                    }
                }
                break;
            }
            case "external":
            {
                if (this.externalGtuIds.add(id))
                {
                    if (getTacticalPlanner(id) != null) {
                        getTacticalPlanner(id).startDeadReckoning();
                    }
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
        this.externalGtuIds.clear();
        this.commandHandlers.clear();
        if (this.app != null)
        {
            this.app.dispose();
            this.app = null;
        }
    }

    public void notify(final org.djutils.event.Event event) throws RemoteException
    {
        if (event.getType().equals(LaneBasedGtu.LANEBASED_MOVE_EVENT))
        {
            Object[] payload = (Object[]) event.getContent();
            String laneChangeDirection = (String) payload[5];  // this can either be "RIGHT" / "LEFT" / "NONE"
            if (!laneChangeDirection.equals("") && !laneChangeDirection.equals("NONE")) {
                laneChange = laneChangeDirection;
            }
        }
        if (event.getType().equals(LaneBasedGtu.LANE_EXIT_EVENT))
        {
            System.out.println("LANE_EXIT_EVENT");
        }
        if (event.getType().equals(LaneBasedGtu.LANE_ENTER_EVENT))
        {
            System.out.println("LANE_ENTER_EVENT");
        }
    }

    /**
     * Worker thread to send messages.
     */
    protected class Worker extends Thread
    {
        private boolean exit = false;
        private LaneBasedGtu gtuAV = null;

        public void exit(){
            this.exit = true;
        }

        @Override
        public void run()
        {
            try
            {
                // Note on synchronicity and possible dead-locks:
                // OTS is single-threaded. All changes during the simulation should be scheduled in the simulator. All messages
                // sent back from a notification from simulation, should be queued for the Worker thread in the queue.
                while (!this.exit)
                {
                    if (this.gtuAV == null) {
                        gtuAV = (LaneBasedGtu) OtsWebSocketTransceiver.this.network.getGTU(avId);
                        if (gtuAV != null) {
                            gtuAV.addListener(OtsWebSocketTransceiver.this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
                            gtuAV.addListener(OtsWebSocketTransceiver.this, LaneBasedGtu.LANE_ENTER_EVENT);
                            gtuAV.addListener(OtsWebSocketTransceiver.this, LaneBasedGtu.LANEBASED_MOVE_EVENT);
                        }
                    }
                    if (!simulator.isStartingOrRunning() || gtuAV == null || gtuAV.isDestroyed()) {
                        continue;
                    }

                    OrientedPoint2d position = gtuAV.getLocation();
                    double acceleration = gtuAV.getAcceleration().getSI();
                    double speed = gtuAV.getSpeed().getSI();
                    boolean isBrakingLightsOn = false;
                    isBrakingLightsOn = gtuAV.isBrakingLightsOn();
                    String turnIndicatorStatus = gtuAV.getTurnIndicatorStatus().name();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("speed", speed);
                    dataJson.put("acceleration", acceleration);
                    dataJson.put("isBrakingLightsOn", isBrakingLightsOn);
                    dataJson.put("turnIndicatorStatus", turnIndicatorStatus);
                    dataJson.put("laneChangeDirection", OtsWebSocketTransceiver.this.laneChange);
                    dataJson.put("laneId", this.gtuAV.getReferencePosition().lane().getId());
                    JSONObject positionJson = new JSONObject();
                    positionJson.put("x", position.getX());
                    positionJson.put("y", position.getY());
                    dataJson.put("position", positionJson);
                    JSONObject rotationJson = new JSONObject();
                    rotationJson.put("z", position.getDirZ());
                    dataJson.put("rotation", rotationJson);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "PLAN");
                    jsonObject.put("data", dataJson);
                    webSocketClient.sendMessage(jsonObject.toString());
                    try {
                        Thread.sleep(10);
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
