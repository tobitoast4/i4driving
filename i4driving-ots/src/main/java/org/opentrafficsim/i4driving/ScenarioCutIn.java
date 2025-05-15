package org.opentrafficsim.i4driving;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
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
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.LinkWeight;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactoryDefault;
import org.opentrafficsim.i4driving.messages.DefaultGson;
import org.opentrafficsim.i4driving.sim0mq.MixinModel;
import org.opentrafficsim.i4driving.tactical.CommandsHandler;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.kpi.sampling.SpaceTimeRegion;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.lane.CollisionDetector;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlannerFactory;
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
import picocli.CommandLine.Mixin;
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

    /** Mixed in model settings. */
    @Mixin
    private MixinModel mixinModel;

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

            Gson gson = DefaultGson.GSON;
            Settings settings;
            try
            {
                settings = gson.fromJson(getReader("settings.json"), DefaultGson.SETTINGS);
                System.out.println("Reading settings from \"settings.json\"");
            }
            catch (RuntimeException exception)
            {
                try
                {
                    settings = gson.fromJson(getReader(demo.settings), DefaultGson.SETTINGS);
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
                this.mixinModel.getTacticalPlanner(randomStream);
        RouteGenerator routeGenerator = RouteGenerator.getDefaultRouteSupplier(randomStream, LinkWeight.LENGTH_NO_CONNECTORS);
        LaneBasedStrategicalRoutePlannerFactory strategicalFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory, new ParameterFactoryDefault(), routeGenerator);

        // Vehicle commands
        Gson gson = DefaultGson.GSON;
        new CommandsHandler(network, gson.fromJson(getReader(this.inputVehicle1), DefaultGson.COMMANDS), strategicalFactory);
        new CommandsHandler(network, gson.fromJson(getReader(this.inputVehicle2), DefaultGson.COMMANDS), strategicalFactory);
        new CommandsHandler(network, gson.fromJson(getReader(this.inputVehicle3), DefaultGson.COMMANDS), strategicalFactory);

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
     * @param file file name.
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
