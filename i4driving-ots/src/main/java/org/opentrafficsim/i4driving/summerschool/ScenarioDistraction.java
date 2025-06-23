package org.opentrafficsim.i4driving.summerschool;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.FrequencyUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.LinearDensityUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.unit.Unit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djunits.value.vdouble.scalar.base.DoubleScalar;
import org.djunits.value.vdouble.vector.FrequencyVector;
import org.djunits.value.vdouble.vector.TimeVector;
import org.djutils.cli.CliUtil;
import org.djutils.draw.line.PolyLine2d;
import org.djutils.draw.line.Polygon2d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Try;
import org.opentrafficsim.animation.colorer.FixedColor;
import org.opentrafficsim.animation.gtu.colorer.AccelerationGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SpeedGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SwitchableGtuColorer;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.ContinuousLine;
import org.opentrafficsim.core.geometry.ContinuousStraight;
import org.opentrafficsim.core.geometry.FractionalLengthData;
import org.opentrafficsim.core.geometry.OtsLine2d;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactoryByType;
import org.opentrafficsim.core.perception.HistoryManagerDevs;
import org.opentrafficsim.draw.graphs.ContourDataSource;
import org.opentrafficsim.draw.graphs.GraphPath;
import org.opentrafficsim.draw.graphs.GraphPath.Section;
import org.opentrafficsim.draw.graphs.TrajectoryPlot;
import org.opentrafficsim.i4driving.demo.AttentionColorer;
import org.opentrafficsim.i4driving.demo.StopCollisionDetector;
import org.opentrafficsim.i4driving.demo.TaskSaturationChannelColorer;
import org.opentrafficsim.i4driving.demo.plots.ContourPlotExtendedData;
import org.opentrafficsim.i4driving.demo.plots.DistributionPlotExtendedData;
import org.opentrafficsim.i4driving.object.LocalDistraction;
import org.opentrafficsim.i4driving.sampling.TaskSaturationData;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlannerFactory;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlannerFactory.CarFollowing;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlannerFactory.FullerImplementation;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;
import org.opentrafficsim.kpi.sampling.Query;
import org.opentrafficsim.kpi.sampling.TrajectoryGroup;
import org.opentrafficsim.kpi.sampling.indicator.AbstractIndicator;
import org.opentrafficsim.kpi.sampling.indicator.MeanDensity;
import org.opentrafficsim.kpi.sampling.indicator.MeanIntensity;
import org.opentrafficsim.kpi.sampling.indicator.MeanSpeed;
import org.opentrafficsim.kpi.sampling.indicator.TotalTravelDistance;
import org.opentrafficsim.kpi.sampling.indicator.TotalTravelTime;
import org.opentrafficsim.kpi.sampling.meta.FilterDataSet;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.generator.characteristics.DefaultLaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.gtu.generator.headway.ArrivalsHeadwayGenerator.HeadwayDistribution;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSituationalAwareness;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.CrossSectionSlice;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LaneGeometryUtil;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.road.network.lane.Stripe;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;
import org.opentrafficsim.road.network.sampling.LaneDataRoad;
import org.opentrafficsim.road.network.sampling.RoadSampler;
import org.opentrafficsim.road.network.sampling.data.TimeToCollision;
import org.opentrafficsim.road.od.Categorization;
import org.opentrafficsim.road.od.Category;
import org.opentrafficsim.road.od.Interpolation;
import org.opentrafficsim.road.od.OdApplier;
import org.opentrafficsim.road.od.OdMatrix;
import org.opentrafficsim.road.od.OdOptions;
import org.opentrafficsim.swing.graphs.OtsPlotScheduler;
import org.opentrafficsim.swing.graphs.SwingContourPlot;
import org.opentrafficsim.swing.graphs.SwingPlot;
import org.opentrafficsim.swing.graphs.SwingTrajectoryPlot;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;
import org.opentrafficsim.swing.script.AbstractSimulationScript;

import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import picocli.CommandLine.Mixin;

/**
 * I4Driving Summer School scenario with distraction.
 * @author wjschakel
 */
public class ScenarioDistraction extends AbstractSimulationScript
{

    /** */
    private static final long serialVersionUID = 20250623L;

    /** Seed. */
    private static final long SEED = 1;

    /** Task saturation data type. */
    private static final TaskSaturationData DATA_SATURATION = new TaskSaturationData();

    /** Time-to-collision data type. */
    private static final TimeToCollision DATA_TTC = new TimeToCollision();

    /** KPI's. */
    private List<Kpi<?, ?>> kpis = new ArrayList<>();

    /** Tactical planner factory. */
    @Mixin
    private final ScenarioTacticalPlannerFactory tacticalFactory = new ScenarioTacticalPlannerFactory();

    /** Data query. */
    private Query<GtuDataRoad, LaneDataRoad> query;

    /** Table. */
    private JTable table;

    /**
     * Constructor.
     */
    protected ScenarioDistraction()
    {
        super("i4D-distr", "i4Driving Summer School scenario with distraction");
        GtuColorer colorer = new SwitchableGtuColorer(0, new FixedColor(Color.BLUE, "Blue"),
                new SpeedGtuColorer(new Speed(130.0, SpeedUnit.KM_PER_HOUR)),
                new AccelerationGtuColorer(Acceleration.instantiateSI(-6.0), Acceleration.instantiateSI(2.0)),
                new TaskSaturationChannelColorer(), new AttentionColorer());
        setGtuColorer(colorer);
    }

    @Override
    protected RoadNetwork setupSimulation(final OtsSimulatorInterface sim) throws Exception
    {
        RoadNetwork network = new RoadNetwork("i4Driving distraction", sim);
        new StopCollisionDetector(network);
        sim.getReplication()
                .setHistoryManager(new HistoryManagerDevs(sim, Duration.instantiateSI(5.0), Duration.instantiateSI(10.0)));

        // Nodes
        OrientedPoint2d pointA = new OrientedPoint2d(0.0, 0.0, 0.0);
        OrientedPoint2d pointB = new OrientedPoint2d(1000.0, 0.0, 0.0);
        Node nodeA = new Node(network, "A", pointA);
        Node nodeB = new Node(network, "B", pointB);

        // Link
        ContinuousLine line = new ContinuousStraight(pointA, pointA.distance(pointB));
        OtsLine2d designLine = new OtsLine2d(line.flatten(null));
        CrossSectionLink link = new CrossSectionLink(network, "AB", nodeA, nodeB, DefaultsNl.HIGHWAY, designLine,
                new FractionalLengthData(0.0, 0.0), LaneKeepingPolicy.KEEPRIGHT);

        // Lane
        List<CrossSectionSlice> slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(3.5), Length.instantiateSI(3.5));
        PolyLine2d left = line.flattenOffset(new FractionalLengthData(0.0, 1.75), null);
        PolyLine2d right = line.flattenOffset(new FractionalLengthData(0.0, -1.75), null);
        Polygon2d contour = LaneGeometryUtil.getContour(left, right);
        Lane lane = new Lane(link, "lane", designLine, contour, slices, DefaultsRoadNl.HIGHWAY,
                Map.of(DefaultsNl.VEHICLE, new Speed(100.0, SpeedUnit.KM_PER_HOUR)));

        // Continuous lane markings
        for (double offset : new double[] {1.75, -1.75})
        {
            designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, offset), null));
            slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(0.0), Length.instantiateSI(0.2));
            left = line.flattenOffset(new FractionalLengthData(0.0, offset + 0.1), null);
            right = line.flattenOffset(new FractionalLengthData(0.0, offset - 0.1), null);
            contour = LaneGeometryUtil.getContour(left, right);
            new Stripe(Type.SOLID, link, designLine, contour, slices);
        }

        // Distraction
        Length location = Length.instantiateSI(700.0);
        Length length = Length.instantiateSI(200.0);
        double taskDemand = 0.8; // < 1.0
        boolean distractionToSide = true; // otherwise to front, meaning drivers do not have to look at two different areas
        new LocalDistraction("distraction", new LanePosition(lane, location), length, taskDemand,
                distractionToSide ? LateralDirectionality.LEFT : LateralDirectionality.NONE);

        // Model components
        // - available car-following models: IDM, IDM_PLUS, M_IDM
        this.tacticalFactory.setCarFollowing(CarFollowing.M_IDM);
        // - available Fuller implementations: NONE, SUMMATIVE, ANTICIPATION_RELIANCE, ATTENTION_MATRIX
        this.tacticalFactory.setFullerImplementation(FullerImplementation.ATTENTION_MATRIX);
        this.tacticalFactory.setNumberOfLeaders(3); // {1, 2, 3}
        this.tacticalFactory.setTemporalAnticipation(true);
        this.tacticalFactory.setFractionOverEstimation(0.5); // [0 ... 1]
        // social interactions
        this.tacticalFactory.setTailgating(false);
        this.tacticalFactory.setSocioSpeed(false);
        // active tasks
        this.tacticalFactory.setLocalDistraction(true);
        this.tacticalFactory.setCarFollowingTask(true);
        // behavioral adaptations
        this.tacticalFactory.setSpeedAdaptation(true);
        this.tacticalFactory.setHeadwayAdaptation(true);
        // components not applicable in this scenario, leave false
        this.tacticalFactory.setFreeAccelerationTask(false);
        this.tacticalFactory.setLaneChangingTask(false);
        this.tacticalFactory.setTrafficLightsTask(false);
        this.tacticalFactory.setSignalTask(false);
        this.tacticalFactory.setCooperationTask(false);
        this.tacticalFactory.setConflictsTask(false);
        this.tacticalFactory.setSocioLaneChange(false);
        this.tacticalFactory.setActiveMode(false);
        this.tacticalFactory.setUpdateTimeAdaptation(false);

        // Parameters
        ParameterFactoryByType parameterFactory = new ParameterFactoryByType();
        parameterFactory.addParameter(Fuller.TC, 1.0);
        // maximum perception delay > 0.32s (ATTENTION_MATRIX)
        parameterFactory.addParameter(ChannelFuller.TAU_MAX, Duration.instantiateSI(1.19));
        // maximum reaction time (SUMMATIVE and ANTICIPATION_RELIANCE)
        parameterFactory.addParameter(AdaptationSituationalAwareness.TR_MAX, Duration.instantiateSI(2.0));

        // Vehicle generation
        this.tacticalFactory.setStream(sim.getModel().getStream("generation"));
        OdOptions options = new OdOptions();
        options.set(OdOptions.GTU_TYPE, new DefaultLaneBasedGtuCharacteristicsGeneratorOd.Factory(
                new LaneBasedStrategicalRoutePlannerFactory(this.tacticalFactory, parameterFactory)).create());
        options.set(OdOptions.HEADWAY_DIST, HeadwayDistribution.CONSTANT);
        GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);

        // OD
        List<Node> origins = new ArrayList<>();
        origins.add(nodeA);
        List<Node> destinations = new ArrayList<>();
        destinations.add(nodeB);
        OdMatrix od = new OdMatrix("od", origins, destinations, Categorization.UNCATEGORIZED,
                new TimeVector(new double[] {0.0, 120.0, 600.0}), Interpolation.LINEAR);
        double[] demand = new double[] {1800.0, 2200.0, 1200.0};
        od.putDemandVector(nodeA, nodeB, Category.UNCATEGORIZED, new FrequencyVector(demand, FrequencyUnit.PER_HOUR));
        OdApplier.applyOd(network, od, options, DefaultsRoadNl.VEHICLES);

        // Events
        getSimulator().scheduleEventAbs(Duration.instantiateSI(20.0), this, "setAcceleration",
                new Object[] {"1", Acceleration.instantiateSI(-2.0)});
        getSimulator().scheduleEventAbs(Duration.instantiateSI(35.0), this, "resetAcceleration", new Object[] {"1"});

        return network;
    }

    /**
     * Set acceleration on GTU.
     * @param gtuId GTU
     * @param acceleration acceleration
     */
    @SuppressWarnings("unused") // scheduled
    private void setAcceleration(final String gtuId, final Acceleration acceleration)
    {
        ((ScenarioTacticalPlanner) getNetwork().getGTU(gtuId).getTacticalPlanner()).setAcceleration(acceleration);
    }

    /**
     * Reset acceleration on GTU.
     * @param gtuId GTU
     */
    @SuppressWarnings("unused") // scheduled
    private void resetAcceleration(final String gtuId)
    {
        ((ScenarioTacticalPlanner) getNetwork().getGTU(gtuId).getTacticalPlanner()).resetAcceleration();
    }

    @Override
    protected void addTabs(final OtsSimulatorInterface sim, final OtsSimulationApplication<?> animation)
    {
        RoadSampler sampler = new RoadSampler(Set.of(DATA_SATURATION, DATA_TTC), Collections.emptySet(), getNetwork(),
                Frequency.instantiateSI(2.0));
        List<Section<LaneDataRoad>> sections = new ArrayList<>();
        Lane lane = ((CrossSectionLink) getNetwork().getLink("AB")).getLanes().get(0);
        Speed speedLimit = Try.assign(() -> lane.getSpeedLimit(DefaultsNl.VEHICLE), "Unable to derive speed from lane.");
        LaneDataRoad laneData = new LaneDataRoad(lane);
        sections.add(new Section<LaneDataRoad>(lane.getLength(), speedLimit, List.of(laneData)));
        GraphPath<LaneDataRoad> graphPath = new GraphPath<>("GraphPath", sections);
        GraphPath.initRecording(sampler, graphPath);
        ContourDataSource source = new ContourDataSource(sampler.getSamplerData(), graphPath);

        TablePanel charts = new TablePanel(2, 2);
        animation.getAnimationPanel().getTabbedPane().addTab(animation.getAnimationPanel().getTabbedPane().getTabCount(),
                "plots", charts);

        // KPI's table
        JPanel panel = new JPanel();
        DefaultTableModel tableModel = new DefaultTableModel(new String[] {"KPI", "Value", "Unit"}, 5);
        this.table = new JTable(tableModel);
        this.table.setPreferredSize(new Dimension(400, 80));
        this.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        this.table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setPreferredSize(new Dimension(400, 103));
        panel.add(scrollPane);
        charts.setCell(panel, 0, 0);

        // Trajectories
        TrajectoryPlot trajectories = new TrajectoryPlot("Trajectories", Duration.instantiateSI(5.3), new OtsPlotScheduler(sim),
                sampler.getSamplerData(), graphPath);
        SwingTrajectoryPlot swing = new SwingTrajectoryPlot(trajectories);
        charts.setCell(swing.getContentPane(), 1, 0);

        // Task saturation
        ContourPlotExtendedData saturation =
                new ContourPlotExtendedData("Task saturation", sim, source, DATA_SATURATION, 0.0, 2.0, 0.2);
        charts.setCell(new SwingContourPlot(saturation).getContentPane(), 0, 1);

        // Time to collision
        DistributionPlotExtendedData ttc = new DistributionPlotExtendedData(sampler.getSamplerData(), graphPath, DATA_TTC,
                "Time-to-collision", "Time-to-collision [s]", sim, 0.0, 0.5, 8.0);
        charts.setCell(new SwingPlot(ttc).getContentPane(), 1, 1);

        // Setup query
        this.query = new Query<GtuDataRoad, LaneDataRoad>(sampler, "Query", "Query", new FilterDataSet(),
                Frequency.instantiateSI(1.0));
        this.query.addSpaceTimeRegion(laneData, Length.ZERO, laneData.getLength(), getStartTime(),
                Time.ZERO.plus(getSimulationTime()));
        TotalTravelDistance performance = new TotalTravelDistance();
        TotalTravelTime totalTime = new TotalTravelTime();
        MeanSpeed meanSpeed = new MeanSpeed(performance, totalTime);
        MeanDensity meanDensity = new MeanDensity(totalTime);
        MeanIntensity meanIntensity = new MeanIntensity(performance);
        this.kpis.add(new Kpi<>(performance, "Performance", LengthUnit.KILOMETER));
        this.kpis.add(new Kpi<>(totalTime, "Total travel time", DurationUnit.HOUR));
        this.kpis.add(new Kpi<>(meanSpeed, "Mean speed", SpeedUnit.KM_PER_HOUR));
        this.kpis.add(new Kpi<>(meanDensity, "Mean density", LinearDensityUnit.PER_KILOMETER));
        this.kpis.add(new Kpi<>(meanIntensity, "Mean flow", FrequencyUnit.PER_HOUR));
        updateKpis();
    }

    /**
     * Update KPI's in table.
     */
    private void updateKpis()
    {
        getSimulator().scheduleEventRel(Duration.instantiateSI(30.0), this, "updateKpis", new Object[] {});
        Time time = getSimulator().getSimulatorAbsTime();
        List<TrajectoryGroup<GtuDataRoad>> trajectoryGroups = this.query.getTrajectoryGroups(time);
        int i = 0;
        for (Kpi<?, ?> kpi : this.kpis)
        {
            double value = kpi.unit().getScale().fromStandardUnit(kpi.kpi.getValue(this.query, time, trajectoryGroups).si);
            this.table.getModel().setValueAt(kpi.name(), i, 0);
            this.table.getModel().setValueAt(String.format("%.2f", value), i, 1);
            this.table.getModel().setValueAt(kpi.unit().getId(), i, 2);
            i++;
        }
    }

    @Override
    protected void onSimulationEnd()
    {
        updateKpis();
    }

    /**
     * Main program.
     * @param args command line arguments. See AbstractSimulationScript for available arguments.
     * @throws Exception when an exception occurs.
     */
    public static void main(final String[] args) throws Exception
    {
        ScenarioDistraction demo = new ScenarioDistraction();
        CliUtil.changeOptionDefault(demo, "simulationTime", "600s");
        CliUtil.changeOptionDefault(demo, "seed", Long.toString(SEED));
        CliUtil.execute(demo, args);
        demo.start();
    }

    /**
     * KPI information.
     * @param kpi KPI
     * @param name name in table
     * @param unit unit
     * @param <U> unit type
     * @param <S> scalar type
     */
    private record Kpi<U extends Unit<U>, S extends DoubleScalar<U, S>>(AbstractIndicator<S> kpi, String name, U unit)
    {
    }

}
