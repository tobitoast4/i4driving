package org.opentrafficsim.i4driving;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.djunits.unit.SpeedUnit;
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
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsLine3d;
import org.opentrafficsim.core.geometry.OtsPoint3d;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.core.network.LinkWeight;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactoryDefault;
import org.opentrafficsim.i4driving.messages.Commands;
import org.opentrafficsim.i4driving.messages.DefaultGsonBuilder;
import org.opentrafficsim.i4driving.tactical.CommandsHandler;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.kpi.sampling.SpaceTimeRegion;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.CategoricalLanePerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.AnticipationTrafficPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.DirectInfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.DirectNeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.AbstractIdm;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlus;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveKeep;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveRoute;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSpeedWithCourtesy;
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
 * Cut-in scenario with three vehicles on a freeway. Command line arguments, with as example the default values, are:
 * <ul>
 * <li>--inputVehicle1="./src/main/resources/cutinVehicle1.json"</li>
 * <li>--inputVehicle2="./src/main/resources/cutinVehicle2.json"</li>
 * <li>--inputVehicle3="./src/main/resources/cutinVehicle3.json"</li>
 * <li>--outputTrajectoriesFile="outputTrajectories.csv"</li>
 * <li>--outputValuesFile="outputValues.csv"</li>
 * <li>--autorun="false" (shows GUI when false)</li>
 * <li>--simulationTime="60s"</li>
 * <li>--seed="1"</li>
 * </ul>
 * @author wjschakel
 */
public class ScenarioCutIn extends AbstractSimulationScript
{

    /** */
    private static final long serialVersionUID = 20230505L;

    /** JSON input file for vehicle 1. */
    @Option(names = {"--inputVehicle1"}, description = "JSON input file for vehicle 1",
            defaultValue = "./src/main/resources/cutinVehicle1.json")
    private String inputVehicle1;

    /** JSON input file for vehicle 1. */
    @Option(names = {"--inputVehicle2"}, description = "JSON input file for vehicle 2",
            defaultValue = "./src/main/resources/cutinVehicle2.json")
    private String inputVehicle2;

    /** JSON input file for vehicle 1. */
    @Option(names = {"--inputVehicle3"}, description = "JSON input file for vehicle 3",
            defaultValue = "./src/main/resources/cutinVehicle3.json")
    private String inputVehicle3;

    /** Trajectory output file. */
    @Option(names = {"--outputTrajectoriesFile"}, description = "Trajectory output file",
            defaultValue = "outputTrajectories.csv")
    private String outputTrajectoriesFile;

    /** Trajectory output file. */
    @Option(names = {"--outputValuesFile"}, description = "Trajectory output file", defaultValue = "outputValues.csv")
    private String outputValuesFile;

    /** Sampler. */
    private RoadSampler sampler;

    /**
     * Constructor.
     */
    protected ScenarioCutIn()
    {
        super("Cut-in scenario", "Cut-in scenario with three vehicles on a freeway");
    }

    /**
     * Main program execution.
     * @param args String... command line arguments.
     */
    public static void main(final String... args)
    {
        Locale.setDefault(Locale.US);
        ScenarioCutIn demo = new ScenarioCutIn();
        try
        {
            CliUtil.changeOptionDefault(demo, "simulationTime", "60s");
            CliUtil.execute(demo, args);
            demo.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        OtsPoint3d pointA = new OtsPoint3d(0.0, 0.0, 0.0);
        OtsPoint3d pointB = new OtsPoint3d(1000.0, 0.0, 0.0);
        Node nodeA = new Node(network, "A", pointA);
        Node nodeB = new Node(network, "B", pointB);
        List<Lane> lanes = new LaneFactory(network, nodeA, nodeB, DefaultsNl.FREEWAY, sim, LaneKeepingPolicy.KEEPRIGHT,
                DefaultsNl.VEHICLE, new OtsLine3d(pointA, pointB))
                        .leftToRight(0.0, Length.instantiateSI(3.5), DefaultsRoadNl.FREEWAY,
                                new Speed(130, SpeedUnit.KM_PER_HOUR))
                        .addLanes(Type.DASHED).getLanes();

        // Model
        LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner> tacticalFactory =
                new LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner>()
                {
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
                        return parameters;
                    }

                    /** {@inheritDoc} */
                    @Override
                    public ScenarioTacticalPlanner create(final LaneBasedGtu gtu) throws GtuException
                    {
                        CarFollowingModel idm = new IdmPlus();
                        LanePerception lanePerception = new CategoricalLanePerception(gtu);
                        lanePerception.addPerceptionCategory(new DirectEgoPerception<>(lanePerception));
                        lanePerception.addPerceptionCategory(new AnticipationTrafficPerception(lanePerception));
                        lanePerception.addPerceptionCategory(new DirectInfrastructurePerception(lanePerception));
                        lanePerception
                                .addPerceptionCategory(new DirectNeighborsPerception(lanePerception, HeadwayGtuType.WRAP));
                        ScenarioTacticalPlanner tacticalPlanner = new ScenarioTacticalPlanner(idm, gtu, lanePerception,
                                Synchronization.PASSIVE, Cooperation.PASSIVE, GapAcceptance.INFORMED, Tailgating.NONE);
                        tacticalPlanner.addMandatoryIncentive(new IncentiveRoute());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveSpeedWithCourtesy());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveKeep());
                        return tacticalPlanner;
                    }
                };
        StreamInterface randomStream = sim.getModel().getStream("generation");
        RouteGenerator routeGenerator = RouteGenerator.getDefaultRouteSupplier(randomStream, LinkWeight.LENGTH_NO_CONNECTORS);
        LaneBasedStrategicalRoutePlannerFactory strategicalFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory, new ParameterFactoryDefault(), routeGenerator);

        // Vehicle commands
        Gson gson = DefaultGsonBuilder.get();
        new CommandsHandler(network, gson.fromJson(Files.readString(Path.of(this.inputVehicle1)), Commands.COMMANDS),
                strategicalFactory);
        new CommandsHandler(network, gson.fromJson(Files.readString(Path.of(this.inputVehicle2)), Commands.COMMANDS),
                strategicalFactory);
        new CommandsHandler(network, gson.fromJson(Files.readString(Path.of(this.inputVehicle3)), Commands.COMMANDS),
                strategicalFactory);

        // Sampler
        this.sampler = RoadSampler.build(network).setFrequency(Frequency.instantiateSI(20.0))
                .registerExtendedDataType(new TimeToCollision()).create();
        for (Lane lane : lanes)
        {
            sampler.registerSpaceTimeRegion(new SpaceTimeRegion<LaneDataRoad>(new LaneDataRoad(lane), Length.ZERO,
                    lane.getLength(), getStartTime(), getStartTime().plus(getSimulationTime())));
        }
        return network;
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
        table.addRow(new String[] {"maximum deceleration", maxDeceleration.neg().toString()});
        table.addRow(new String[] {"minimum time-to-collision", minTtc.toString()});
        Try.execute(() -> CsvData.writeData(this.outputValuesFile, this.outputValuesFile + ".header", table),
                "Could not write values data.");
    }

}
