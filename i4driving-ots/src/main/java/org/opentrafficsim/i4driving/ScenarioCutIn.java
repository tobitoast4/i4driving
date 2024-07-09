package org.opentrafficsim.i4driving;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vfloat.scalar.FloatAcceleration;
import org.djunits.value.vfloat.scalar.FloatDuration;
import org.djutils.cli.CliUtil;
import org.djutils.data.Column;
import org.djutils.data.ListTable;
import org.djutils.data.Row;
import org.djutils.data.csv.CsvData;
import org.djutils.draw.point.Point2d;
import org.djutils.exceptions.Try;
import org.opentrafficsim.animation.colorer.FixedColor;
import org.opentrafficsim.animation.colorer.ReactionTimeColorer;
import org.opentrafficsim.animation.colorer.TaskColorer;
import org.opentrafficsim.animation.colorer.TaskSaturationColorer;
import org.opentrafficsim.animation.gtu.colorer.AccelerationGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SpeedGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SwitchableGtuColorer;
import org.opentrafficsim.base.Resource;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuErrorHandler;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.core.network.LinkWeight;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactoryDefault;
import org.opentrafficsim.i4driving.messages.DefaultGsonBuilder;
import org.opentrafficsim.i4driving.tactical.CommandsHandler;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.i4driving.tactical.perception.CarFollowingTask;
import org.opentrafficsim.i4driving.tactical.perception.LaneChangeTask;
import org.opentrafficsim.i4driving.tactical.perception.TaskManagerAr;
import org.opentrafficsim.kpi.sampling.SpaceTimeRegion;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.lane.CollisionDetector;
import org.opentrafficsim.road.gtu.lane.CollisionException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.CategoricalLanePerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.AnticipationTrafficPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.DirectInfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Anticipation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.DirectNeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType.PerceivedHeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationHeadway;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSituationalAwareness;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSpeed;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller.BehavioralAdaptation;
import org.opentrafficsim.road.gtu.lane.perception.mental.Task;
import org.opentrafficsim.road.gtu.lane.perception.mental.TaskManager;
import org.opentrafficsim.road.gtu.lane.perception.mental.TaskManager.SummativeTaskManager;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.AbstractIdm;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.DesiredSpeedModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlus;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlusMulti;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveKeep;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveRoute;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSocioSpeed;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSpeedWithCourtesy;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.SocioDesiredSpeed;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Cooperation;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.GapAcceptance;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsUtil;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Synchronization;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Tailgating;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.gtu.strategical.RouteGenerator;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.factory.LaneFactory;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.sampling.LaneDataRoad;
import org.opentrafficsim.road.network.sampling.RoadSampler;
import org.opentrafficsim.road.network.sampling.data.TimeToCollision;
import org.opentrafficsim.swing.script.AbstractSimulationScript;

import com.google.gson.Gson;

import nl.tudelft.simulation.jstats.streams.StreamInterface;
import picocli.CommandLine.Option;

/**
 * Cut-in scenario with three vehicles on a freeway. For usage and available argument, please refer to the doc folder.
 * @author wjschakel
 */
public class ScenarioCutIn extends AbstractSimulationScript
{

    /** */
    private static final long serialVersionUID = 20230505L;

    /** Settings file; an alternative to setting command line arguments. */
    @Option(names = {"--settings"}, description = "JSON input file for settings", defaultValue = "cutinSettings.json")
    private String settings;

    /** JSON input file for vehicle 1. */
    @Option(names = {"--inputVehicle1"}, description = "JSON input file for vehicle 1", defaultValue = "cutinVehicle1.json")
    private String inputVehicle1;

    /** JSON input file for vehicle 2. */
    @Option(names = {"--inputVehicle2"}, description = "JSON input file for vehicle 2", defaultValue = "cutinVehicle2.json")
    private String inputVehicle2;

    /** JSON input file for vehicle 3. */
    @Option(names = {"--inputVehicle3"}, description = "JSON input file for vehicle 3", defaultValue = "cutinVehicle3.json")
    private String inputVehicle3;

    /** Trajectory output file. */
    @Option(names = {"--outputTrajectoriesFile"}, description = "Trajectory output file",
            defaultValue = "outputTrajectories.csv")
    private String outputTrajectoriesFile;

    /** Trajectory output file. */
    @Option(names = {"--outputValuesFile"}, description = "Trajectory output file", defaultValue = "outputValues.csv")
    private String outputValuesFile;

    /** Apply full Fuller and overwrite all other mental settings. */
    @Option(names = {"--fullFuller"}, description = "Apply full Fuller", negatable = true, defaultValue = "true")
    private boolean fullFuller = true;

    /** Apply Fuller. */
    @Option(names = {"--fuller"}, description = "Apply Fuller", negatable = true, defaultValue = "true")
    private boolean fuller = true;

    /** Include car-following task demand in Fuller. */
    @Option(names = {"--carFollowingTask"}, description = "Include car-following task.", negatable = true,
            defaultValue = "true")
    private boolean carFollowingTask = true;

    /** Include lane change task demand in Fuller. */
    @Option(names = {"--laneChangeTask"}, description = "Include lane change task.", negatable = true, defaultValue = "true")
    private boolean laneChangeTask = true;

    /** Set lane change task as primary in Fuller. */
    @Option(names = {"--laneChangeIsPrimary"}, description = "Set lane change task as primary.", negatable = true,
            defaultValue = "true")
    private boolean laneChangeIsPrimary = true;

    /** Apply anticipation reliance in Fuller. */
    @Option(names = {"--anticipationReliance"}, description = "Apply anticipation reliance in Fuller.", negatable = true,
            defaultValue = "true")
    private boolean anticipationReliance = true;

    /** Adapt headway in Fuller. */
    @Option(names = {"--adaptHeadway"}, description = "Adapt headway in Fuller.", negatable = true, defaultValue = "true")
    private boolean adaptHeadway = true;

    /** Adapt speed in Fuller. */
    @Option(names = {"--adaptSpeed"}, description = "Adapt speed in Fuller.", negatable = true, defaultValue = "true")
    private boolean adaptSpeed = true;

    /** Anticipate multiple leaders in car-following. */
    @Option(names = {"--multiAnticipation"}, description = "Anticipate multiple leaders in car-following.", negatable = true,
            defaultValue = "true")
    private boolean multiAnticipation = true;

    /** Apply full social interactions and overwrite all other social settings. */
    @Option(names = {"--fullSocio"}, description = "Apply full social model.", negatable = true, defaultValue = "true")
    private boolean fullSocio = true;

    /** Apply social interactions. */
    @Option(names = {"--socio"}, description = "Apply social model.", negatable = true, defaultValue = "true")
    private boolean socio = true;

    /** Apply tailgating in social interactions. */
    @Option(names = {"--tailgating"}, description = "Apply tailgating.", negatable = true, defaultValue = "true")
    private boolean tailgating = true;

    /** Apply lane change incentive in social interactions. */
    @Option(names = {"--socioLaneChangeIncentive"}, description = "Apply social lane change incentive.", negatable = true,
            defaultValue = "true")
    private boolean socioLaneChangeIncentive = true;

    /** Apply desired speed in social interactions. */
    @Option(names = {"--socioDesiredSpeed"}, description = "Apply social desired speed.", negatable = true,
            defaultValue = "true")
    private boolean socioDesiredSpeed = true;

    /** Fraction of drivers with over estimation. */
    @Option(names = {"--fractionOverEstimation"}, description = "Fraction of drivers with over estimation.",
            defaultValue = "1.0")
    private double fractionOverEstimation = 1.0;

    /** Sampler. */
    private RoadSampler sampler;

    /** Collision message saved in output. */
    private String collision = "none";

    /**
     * Constructor.
     */
    protected ScenarioCutIn()
    {
        super("Cut-in scenario", "Cut-in scenario with three vehicles on a freeway");
        setGtuColorer(SwitchableGtuColorer.builder().addActiveColorer(new FixedColor(Color.BLUE, "Blue"))
                .addColorer(new TaskColorer("car-following")).addColorer(new TaskColorer("lane-changing"))
                .addColorer(new TaskSaturationColorer()).addColorer(new ReactionTimeColorer(Duration.instantiateSI(1.0)))
                .addColorer(new SpeedGtuColorer(new Speed(150, SpeedUnit.KM_PER_HOUR)))
                .addColorer(new AccelerationGtuColorer(Acceleration.instantiateSI(-6.0), Acceleration.instantiateSI(2)))
                .build());
    }

    /**
     * Main program execution.
     * @param args String... command line arguments.
     * @throws URISyntaxException
     */
    public static void main(final String... args) throws URISyntaxException
    {
        Locale.setDefault(Locale.US);
        ScenarioCutIn demo = new ScenarioCutIn();
        try
        {
            CliUtil.changeOptionDefault(demo, "simulationTime", "60s");
            CliUtil.execute(demo, args);

            Gson gson = DefaultGsonBuilder.get();
            Settings settings;
            try
            {
                settings = gson.fromJson(getReader("settings.json"), DefaultGsonBuilder.SETTINGS);
                System.out.println("Reading settings from \"settings.json\"");
            }
            catch (RuntimeException exception)
            {
                try
                {
                    settings = gson.fromJson(getReader(demo.settings), DefaultGsonBuilder.SETTINGS);
                    System.out.println("Reading settings from \"" + demo.settings + "\"");
                }
                catch (RuntimeException exceptionInner)
                {
                    throw new IOException("Unable to read file " + demo.settings, exceptionInner);
                }
            }
            CliUtil.execute(demo, settings.getArguments());
            demo.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected RoadNetwork setupSimulation(final OtsSimulatorInterface sim) throws Exception
    {
        GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);
        GtuType.registerTemplateSupplier(DefaultsNl.TRUCK, Defaults.NL);

        // Network
        RoadNetwork network = new RoadNetwork("Cut-in scenario network", sim);
        Point2d pointA = new Point2d(0.0, 0.0);
        Point2d pointB = new Point2d(1000.0, 0.0);
        Node nodeA = new Node(network, "A", pointA);
        Node nodeB = new Node(network, "B", pointB);
        List<Lane> lanes =
                new LaneFactory(network, nodeA, nodeB, DefaultsNl.FREEWAY, sim, LaneKeepingPolicy.KEEPRIGHT, DefaultsNl.VEHICLE)
                        .leftToRight(0.0, Length.instantiateSI(3.5), DefaultsRoadNl.FREEWAY,
                                new Speed(130, SpeedUnit.KM_PER_HOUR))
                        .addLanes(Type.DASHED).getLanes();

        // Model
        StreamInterface randomStream = sim.getModel().getStream("generation");
        LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner> tacticalFactory =
                new LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner>()
                {
                    /** GTU error handler for collisions. */
                    private GtuErrorHandler errorHandler = new GtuErrorHandler()
                    {
                        /** {@inheritDoc} */
                        @Override
                        public void handle(final Gtu gtu, final Exception ex) throws Exception
                        {
                            if (ex.getCause() instanceof CollisionException)
                            {
                                ScenarioCutIn.this.collision = ex.getCause().getMessage();
                                System.out.println(ex.getCause().getMessage());
                                gtu.getSimulator().endReplication();
                                onSimulationEnd();
                                System.exit(0);
                            }
                            else
                            {
                                throw ex;
                            }
                        }
                    };

                    /** {@inheritDoc} */
                    @Override
                    public Parameters getParameters() throws ParameterException
                    {
                        ParameterSet parameters = new ParameterSet();
                        parameters.setDefaultParameters(LmrsUtil.class);
                        parameters.setDefaultParameters(LmrsParameters.class);
                        parameters.setDefaultParameters(AbstractIdm.class);
                        parameters.setDefaultParameter(ParameterTypes.PERCEPTION);
                        parameters.setDefaultParameter(ParameterTypes.LOOKBACK);
                        parameters.setDefaultParameter(ParameterTypes.LOOKAHEAD);
                        parameters.setDefaultParameter(ParameterTypes.VCONG);
                        parameters.setDefaultParameter(ParameterTypes.LCDUR);
                        if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.fuller)
                        {
                            parameters.setDefaultParameter(Fuller.TC);
                            parameters.setDefaultParameter(Fuller.TS);
                            parameters.setDefaultParameter(Fuller.TS_CRIT);
                            parameters.setDefaultParameter(Fuller.TS_MAX);
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.carFollowingTask)
                            {
                                parameters.setDefaultParameter(CarFollowingTask.HEXP);
                            }
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA_MIN);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA_MAX);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.TR_MAX);
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.adaptHeadway)
                            {
                                parameters.setDefaultParameter(AdaptationHeadway.BETA_T);
                            }
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.adaptSpeed)
                            {
                                parameters.setDefaultParameter(AdaptationSpeed.BETA_V0);
                            }
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.multiAnticipation)
                            {
                                parameters.setDefaultParameter(IdmPlusMulti.NLEADERS);
                            }
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.anticipationReliance)
                            {
                                parameters.setDefaultParameter(TaskManagerAr.ALPHA);
                                parameters.setDefaultParameter(TaskManagerAr.BETA);
                            }
                        }
                        if (ScenarioCutIn.this.fullSocio || ScenarioCutIn.this.socio)
                        {
                            parameters.setDefaultParameter(Tailgating.RHO);
                            parameters.setDefaultParameter(LmrsParameters.SOCIO);
                        }
                        parameters.setParameter(Estimation.OVER_EST,
                                randomStream.nextDouble() <= ScenarioCutIn.this.fractionOverEstimation ? 1.0 : -1.0);
                        return parameters;
                    }

                    /** {@inheritDoc} */
                    @Override
                    public ScenarioTacticalPlanner create(final LaneBasedGtu gtu) throws GtuException
                    {
                        gtu.setErrorHandler(this.errorHandler);

                        DesiredSpeedModel desiredSpeedModel = (ScenarioCutIn.this.fullSocio
                                || (ScenarioCutIn.this.socio && ScenarioCutIn.this.socioDesiredSpeed))
                                        ? new SocioDesiredSpeed(AbstractIdm.DESIRED_SPEED) : AbstractIdm.DESIRED_SPEED;

                        CarFollowingModel idm =
                                ScenarioCutIn.this.multiAnticipation ? new IdmPlusMulti(AbstractIdm.HEADWAY, desiredSpeedModel)
                                        : new IdmPlus(AbstractIdm.HEADWAY, desiredSpeedModel);

                        Fuller mental = null;
                        if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.fuller)
                        {
                            Set<Task> tasks = new LinkedHashSet<>();
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.carFollowingTask)
                            {
                                tasks.add(new CarFollowingTask());
                            }
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.laneChangeTask)
                            {
                                tasks.add(new LaneChangeTask());
                            }
                            Set<BehavioralAdaptation> behavioralAdapatations = new LinkedHashSet<>();
                            behavioralAdapatations.add(new AdaptationSituationalAwareness());
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.adaptHeadway)
                            {
                                behavioralAdapatations.add(new AdaptationHeadway());
                            }
                            if (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.adaptSpeed)
                            {
                                behavioralAdapatations.add(new AdaptationSpeed());
                            }
                            String primaryTask = ScenarioCutIn.this.laneChangeIsPrimary ? "lane-changing" : "car-following";
                            TaskManager taskManager = (ScenarioCutIn.this.fullFuller || ScenarioCutIn.this.anticipationReliance)
                                    ? new TaskManagerAr(primaryTask) : new SummativeTaskManager();
                            mental = new Fuller(tasks, behavioralAdapatations, taskManager);
                        }

                        LanePerception lanePerception = new CategoricalLanePerception(gtu, mental);
                        lanePerception.addPerceptionCategory(new DirectEgoPerception<>(lanePerception));
                        lanePerception.addPerceptionCategory(new AnticipationTrafficPerception(lanePerception));
                        lanePerception.addPerceptionCategory(new DirectInfrastructurePerception(lanePerception));
                        Estimation estimation = Estimation.FACTOR_ESTIMATION;
                        HeadwayGtuType headwayGtuType = new PerceivedHeadwayGtuType(estimation, Anticipation.CONSTANT_SPEED);
                        lanePerception.addPerceptionCategory(new DirectNeighborsPerception(lanePerception, headwayGtuType));
                        Tailgating tail =
                                (ScenarioCutIn.this.fullSocio || (ScenarioCutIn.this.socio && ScenarioCutIn.this.tailgating))
                                        ? Tailgating.PRESSURE : Tailgating.NONE;
                        ScenarioTacticalPlanner tacticalPlanner = new ScenarioTacticalPlanner(idm, gtu, lanePerception,
                                Synchronization.PASSIVE, Cooperation.PASSIVE, GapAcceptance.INFORMED, tail);
                        tacticalPlanner.addMandatoryIncentive(new IncentiveRoute());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveSpeedWithCourtesy());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveKeep());
                        if (ScenarioCutIn.this.fullSocio
                                || (ScenarioCutIn.this.socio && ScenarioCutIn.this.socioLaneChangeIncentive))
                        {
                            tacticalPlanner.addVoluntaryIncentive(new IncentiveSocioSpeed());
                        }
                        return tacticalPlanner;
                    }
                };
        RouteGenerator routeGenerator = RouteGenerator.getDefaultRouteSupplier(randomStream, LinkWeight.LENGTH_NO_CONNECTORS);
        LaneBasedStrategicalRoutePlannerFactory strategicalFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory, new ParameterFactoryDefault(), routeGenerator);

        // Vehicle commands
        Gson gson = DefaultGsonBuilder.get();
        new CommandsHandler(network, gson.fromJson(getReader(this.inputVehicle1), DefaultGsonBuilder.COMMANDS),
                strategicalFactory);
        new CommandsHandler(network, gson.fromJson(getReader(this.inputVehicle2), DefaultGsonBuilder.COMMANDS),
                strategicalFactory);
        new CommandsHandler(network, gson.fromJson(getReader(this.inputVehicle3), DefaultGsonBuilder.COMMANDS),
                strategicalFactory);

        // Sampler
        this.sampler = RoadSampler.build(network).setFrequency(Frequency.instantiateSI(20.0))
                .registerExtendedDataType(new TimeToCollision()).create();
        for (Lane lane : lanes)
        {
            sampler.registerSpaceTimeRegion(new SpaceTimeRegion<LaneDataRoad>(new LaneDataRoad(lane), Length.ZERO,
                    lane.getLength(), getStartTime(), getStartTime().plus(getSimulationTime())));
        }

        // Collision detection
        new CollisionDetector(network);

        return network;
    }

    /**
     * Returns a reader for GSON to read a file.
     * @param file String; file name.
     * @return Reader for GSON to read a file.
     */
    private final static Reader getReader(final String file)
    {
        File f = new File(file);
        if (f.exists())
        {
            return Try.assign(() -> new BufferedReader(new InputStreamReader(new FileInputStream(f))), "Cannot happen.");
        }
        return new BufferedReader(new InputStreamReader(Resource.getResourceAsStream("/" + file)));
    }

    /** {@inheritDoc} */
    @Override
    protected void onSimulationEnd()
    {
        Try.execute(() -> CsvData.writeData(this.outputTrajectoriesFile, this.outputTrajectoriesFile + ".header",
                this.sampler.getSamplerData()), "Could not write trajectory data.");
        Column<String> column1 = new Column<>("description", "Description column", String.class);
        Column<String> column2 = new Column<>("value", "Value column", String.class);

        ListTable table = new ListTable("output", "Table with scenario output values", Set.of(column1, column2));
        FloatAcceleration maxDeceleration = FloatAcceleration.ZERO;
        FloatDuration minTtc = FloatDuration.POSITIVE_INFINITY;
        for (Row row : this.sampler.getSamplerData())
        {
            if (row.getValue("gtuId").equals("3"))
            {
                maxDeceleration = FloatAcceleration.min(maxDeceleration, (FloatAcceleration) row.getValue("a"));
                FloatDuration ttc = (FloatDuration) row.getValue("timeToCollision");
                if (ttc.gt0())
                {
                    minTtc = FloatDuration.min(minTtc, ttc);
                }
            }
        }
        table.addRow(new String[] {"collision", this.collision});
        table.addRow(new String[] {"maximum deceleration", maxDeceleration.neg().toString()});
        table.addRow(new String[] {"minimum time-to-collision", minTtc.toString()});
        Try.execute(() -> CsvData.writeData(this.outputValuesFile, this.outputValuesFile + ".header", table),
                "Could not write values data.");
    }

}
