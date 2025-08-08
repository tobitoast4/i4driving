package org.opentrafficsim.i4driving.summerschool;

import java.awt.Color;
import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
import org.djunits.value.vdouble.scalar.Angle;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djunits.value.vdouble.scalar.base.DoubleScalar;
import org.djutils.cli.CliUtil;
import org.djutils.draw.line.PolyLine2d;
import org.djutils.draw.line.Polygon2d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.event.Event;
import org.djutils.exceptions.Try;
import org.opentrafficsim.animation.colorer.FixedColor;
import org.opentrafficsim.animation.colorer.SocialPressureColorer;
import org.opentrafficsim.animation.gtu.colorer.AccelerationGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SpeedGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SwitchableGtuColorer;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.ContinuousArc;
import org.opentrafficsim.core.geometry.ContinuousLine;
import org.opentrafficsim.core.geometry.Flattener;
import org.opentrafficsim.core.geometry.Flattener.NumSegments;
import org.opentrafficsim.core.geometry.FractionalLengthData;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.geometry.OtsLine2d;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.Network;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactory;
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
import org.opentrafficsim.i4driving.sampling.TaskSaturationData;
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
import org.opentrafficsim.road.gtu.generator.GtuSpawner;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristics;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.VehicleModel;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSituationalAwareness;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalPlannerFactory;
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
import org.opentrafficsim.swing.graphs.OtsPlotScheduler;
import org.opentrafficsim.swing.graphs.SwingContourPlot;
import org.opentrafficsim.swing.graphs.SwingPlot;
import org.opentrafficsim.swing.graphs.SwingTrajectoryPlot;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;
import org.opentrafficsim.swing.script.AbstractSimulationScript;

import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import picocli.CommandLine.Mixin;

/**
 * I4Driving Summer School scenario with social interactions on ring road.
 * @author wjschakel
 */
public class ScenarioRing extends AbstractSimulationScript
{

    /** */
    private static final long serialVersionUID = 20250626L;

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

    /** Pointer to network set before superclass knows the network. This is needed to listen to lane changes. */
    private RoadNetwork net;

    /** Number of lane changes. */
    private int numberOfLaneChanges;

    /**
     * Constructor.
     */
    protected ScenarioRing()
    {
        super("i4D-distr", "i4Driving Summer School scenario with social interactions on ring road");
        GtuColorer colorer = new SwitchableGtuColorer(0, new FixedColor(Color.BLUE, "Blue"),
                new SpeedGtuColorer(new Speed(150.0, SpeedUnit.KM_PER_HOUR)),
                new AccelerationGtuColorer(Acceleration.instantiateSI(-6.0), Acceleration.instantiateSI(2.0)),
                new TaskSaturationChannelColorer(), new AttentionColorer(), new SocialPressureColorer());
        setGtuColorer(colorer);
    }

    @Override
    protected RoadNetwork setupSimulation(final OtsSimulatorInterface sim) throws Exception
    {
        RoadNetwork network = new RoadNetwork("i4Driving ring", sim);
        new StopCollisionDetector(network);
        sim.getReplication()
                .setHistoryManager(new HistoryManagerDevs(sim, Duration.instantiateSI(5.0), Duration.instantiateSI(10.0)));

        // Nodes
        double radius = 150.0;
        OrientedPoint2d pointA = new OrientedPoint2d(radius, 0.0, Math.PI / 2.0);
        OrientedPoint2d pointB = new OrientedPoint2d(-radius, 0.0, -Math.PI / 2.0);
        Node nodeA = new Node(network, "A", pointA);
        Node nodeB = new Node(network, "B", pointB);

        // Link, lane and lane markings
        Collection<Lane> lanes = new LinkedHashSet<>(makeLink(network, nodeA, nodeB));
        lanes.addAll(makeLink(network, nodeB, nodeA));

        // Model components
        // - available car-following models: IDM, IDM_PLUS, M_IDM
        this.tacticalFactory.setCarFollowing(CarFollowing.M_IDM);
        // - available Fuller implementations: NONE, SUMMATIVE, ANTICIPATION_RELIANCE, ATTENTION_MATRIX
        this.tacticalFactory.setFullerImplementation(FullerImplementation.ATTENTION_MATRIX);
        this.tacticalFactory.setTemporalAnticipation(true);
        this.tacticalFactory.setFractionOverEstimation(0.6); // [0 ... 1]
        // social interactions
        this.tacticalFactory.setTailgating(true);
        this.tacticalFactory.setSocioSpeed(true);
        this.tacticalFactory.setSocioLaneChange(true);
        // active tasks
        this.tacticalFactory.setCarFollowingTask(true);
        this.tacticalFactory.setLaneChangingTask(true);
        // behavioral adaptations
        this.tacticalFactory.setSpeedAdaptation(true);
        this.tacticalFactory.setHeadwayAdaptation(true);
        // components not applicable in this scenario, leave false
        this.tacticalFactory.setConflictsTask(false);
        this.tacticalFactory.setLocalDistraction(false);
        this.tacticalFactory.setFreeAccelerationTask(false);
        this.tacticalFactory.setTrafficLightsTask(false);
        this.tacticalFactory.setSignalTask(false);
        this.tacticalFactory.setCooperationTask(false);
        this.tacticalFactory.setActiveMode(false);
        this.tacticalFactory.setUpdateTimeAdaptation(false);
        this.tacticalFactory.setNumberOfLeaders(1); // leave 1; with large gaps between leaders otherwise is not collision free

        // Parameters
        ParameterFactoryByType parameterFactory = new ParameterFactoryByType();
        parameterFactory.addParameter(Fuller.TC, 1.0);
        // maximum perception delay > 0.32s (ATTENTION_MATRIX)
        parameterFactory.addParameter(ChannelFuller.TAU_MAX, Duration.instantiateSI(1.19));
        // maximum reaction time (SUMMATIVE and ANTICIPATION_RELIANCE)
        parameterFactory.addParameter(AdaptationSituationalAwareness.TR_MAX, Duration.instantiateSI(2.0));
        parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.VGAIN, new Speed(25.0, SpeedUnit.KM_PER_HOUR));
        parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.SOCIO, 0.5);
        parameterFactory.addParameter(DefaultsNl.TRUCK, LmrsParameters.VGAIN, new Speed(50.0, SpeedUnit.KM_PER_HOUR));
        parameterFactory.addParameter(DefaultsNl.TRUCK, LmrsParameters.SOCIO, 1.0);

        // Pre-placed vehicles
        int nVehiclesPerLane = 6;
        double fTruck = 0.1;
        StreamInterface stream = sim.getModel().getStream("generation");
        prePlaceVehicles(network, lanes, nVehiclesPerLane, fTruck, stream, parameterFactory);

        return network;
    }

    /**
     * Pre-places vehicles on the ring.
     * @param network network
     * @param lanes list of all lanes
     * @param nVehicles number of vehicles per lane
     * @param fTruck truck fraction over whole ring
     * @param stream random number stream
     * @param parameterFactory parameter factory
     * @throws GtuException
     * @throws OtsGeometryException
     * @throws NetworkException
     */
    private void prePlaceVehicles(final RoadNetwork network, final Collection<Lane> lanes, final int nVehicles,
            final double fTruck, final StreamInterface stream, final ParameterFactory parameterFactory)
            throws GtuException, OtsGeometryException, NetworkException
    {
        this.net = network;
        network.addListener(this, Network.GTU_ADD_EVENT);

        GtuSpawner spawner = new GtuSpawner();
        spawner.setStream(stream);
        spawner.setInstantaneousLaneChanges(false);
        this.tacticalFactory.setStream(stream);
        GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);
        GtuType.registerTemplateSupplier(DefaultsNl.TRUCK, Defaults.NL);
        LaneBasedStrategicalPlannerFactory<?> laneBasedStrategicalPlannerFactory =
                new LaneBasedStrategicalRoutePlannerFactory(this.tacticalFactory, parameterFactory);
        int n = 1;
        for (Lane lane : lanes)
        {
            double fTruckLane = fTruck < 0.5 ? (lane.getId().equals("lane1") ? 0.0 : 2 * fTruck)
                    : (lane.getId().equals("lane1") ? 2 * (fTruck - .5) : 1.0);
            for (int i = 0; i < nVehicles; i++)
            {
                GtuType type = stream.nextDouble() < fTruckLane ? DefaultsNl.TRUCK : DefaultsNl.CAR;
                LaneBasedGtuCharacteristics characteristics =
                        new LaneBasedGtuCharacteristics(GtuType.defaultCharacteristics(type, network, stream),
                                laneBasedStrategicalPlannerFactory, null, null, null, VehicleModel.MINMAX);
                spawner.spawnGtu("" + n++, characteristics, network, new Speed(80.0, SpeedUnit.KM_PER_HOUR), null,
                        new LanePosition(lane, lane.getLength().times((i / (double) nVehicles))));
            }
        }
    }

    /**
     * Create link with two lanes.
     * @param network network
     * @param nodeFrom from node
     * @param nodeTo to node
     * @return lanes
     * @throws NetworkException
     */
    private Collection<Lane> makeLink(final RoadNetwork network, final Node nodeFrom, final Node nodeTo) throws NetworkException
    {
        double radius = nodeFrom.getPoint().distance(nodeTo.getPoint()) / 2.0;

        // Link
        ContinuousLine line = new ContinuousArc(nodeFrom.getLocation(), radius, true, Angle.instantiateSI(Math.PI));
        Flattener flattener = new NumSegments(128);
        OtsLine2d designLine = new OtsLine2d(line.flatten(flattener));
        CrossSectionLink link = new CrossSectionLink(network, nodeFrom.getId() + nodeTo.getId(), nodeFrom, nodeTo,
                DefaultsNl.FREEWAY, designLine, new FractionalLengthData(0.0, 0.0), LaneKeepingPolicy.KEEPRIGHT);

        // Lanes
        List<CrossSectionSlice> slices =
                LaneGeometryUtil.getSlices(line, Length.instantiateSI(1.75), Length.instantiateSI(3.5));
        PolyLine2d left = line.flattenOffset(new FractionalLengthData(0.0, 3.5), flattener);
        PolyLine2d center = line.flattenOffset(new FractionalLengthData(0.0, 1.75), flattener);
        PolyLine2d right = line.flattenOffset(new FractionalLengthData(0.0, 0.0), flattener);
        Polygon2d contour = LaneGeometryUtil.getContour(left, right);
        Lane lane1 = new Lane(link, "lane1", new OtsLine2d(center), contour, slices, DefaultsRoadNl.FREEWAY,
                Map.of(DefaultsNl.VEHICLE, new Speed(130.0, SpeedUnit.KM_PER_HOUR)));

        slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(-1.75), Length.instantiateSI(3.5));
        left = line.flattenOffset(new FractionalLengthData(0.0, 0.0), flattener);
        center = line.flattenOffset(new FractionalLengthData(0.0, -1.75), flattener);
        right = line.flattenOffset(new FractionalLengthData(0.0, -3.5), flattener);
        contour = LaneGeometryUtil.getContour(left, right);
        Lane lane2 = new Lane(link, "lane2", new OtsLine2d(center), contour, slices, DefaultsRoadNl.FREEWAY,
                Map.of(DefaultsNl.VEHICLE, new Speed(130.0, SpeedUnit.KM_PER_HOUR)));

        // Lane markings
        double[] offset = new double[] {3.5, 0.0, -3.5};
        Type[] type = new Type[] {Type.SOLID, Type.DASHED, Type.SOLID};
        for (int i = 0; i < offset.length; i++)
        {
            designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, offset[i]), flattener));
            slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(offset[i]), Length.instantiateSI(0.2));
            left = line.flattenOffset(new FractionalLengthData(0.0, offset[i] + 0.1), flattener);
            right = line.flattenOffset(new FractionalLengthData(0.0, offset[i] - 0.1), flattener);
            contour = LaneGeometryUtil.getContour(left, right);
            new Stripe(type[i], link, designLine, contour, slices);
        }

        return Set.of(lane1, lane2);
    }

    @Override
    protected void addTabs(final OtsSimulatorInterface sim, final OtsSimulationApplication<?> animation)
    {
        RoadSampler sampler = new RoadSampler(Set.of(DATA_SATURATION, DATA_TTC), Collections.emptySet(), getNetwork(),
                Frequency.instantiateSI(2.0));

        List<Section<LaneDataRoad>> sections = new ArrayList<>();
        Collection<LaneDataRoad> laneDatas = new LinkedHashSet<>();
        for (String linkId : new String[] {"AB", "BA"})
        {
            CrossSectionLink link = (CrossSectionLink) getNetwork().getLink(linkId);
            Speed speedLimit = Try.assign(() -> link.getLanes().get(0).getSpeedLimit(DefaultsNl.VEHICLE),
                    "Unable to derive speed from lane.");
            LaneDataRoad laneData1 = new LaneDataRoad(link.getLanes().get(0));
            LaneDataRoad laneData2 = new LaneDataRoad(link.getLanes().get(1));
            sections.add(new Section<LaneDataRoad>(link.getLength(), speedLimit, List.of(laneData1, laneData2)));
            laneDatas.add(laneData1);
            laneDatas.add(laneData2);
        }
        GraphPath<LaneDataRoad> graphPath = new GraphPath<>(List.of("Left", "Right"), sections);
        GraphPath.initRecording(sampler, graphPath);
        ContourDataSource source = new ContourDataSource(sampler.getSamplerData(), graphPath);

        TablePanel charts = new TablePanel(2, 2);
        animation.getAnimationPanel().getTabbedPane().addTab(animation.getAnimationPanel().getTabbedPane().getTabCount(),
                "plots", charts);

        // KPI's table
        JPanel panel = new JPanel();
        DefaultTableModel tableModel = new DefaultTableModel(new String[] {"KPI", "Value", "Unit"}, 6);
        this.table = new JTable(tableModel);
        this.table.setPreferredSize(new Dimension(400, 95));
        this.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer());
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        this.table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setPreferredSize(new Dimension(400, 118));
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
        for (LaneDataRoad laneData : laneDatas)
        {
            this.query.addSpaceTimeRegion(laneData, Length.ZERO, laneData.getLength(), getStartTime(),
                    Time.ZERO.plus(getSimulationTime()));
        }
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
        this.table.getModel().setValueAt("# of lane changes", i, 0);
        this.table.getModel().setValueAt("" + this.numberOfLaneChanges, i, 1);
        this.table.getModel().setValueAt("-", i, 2);
    }

    @Override
    protected void onSimulationEnd()
    {
        updateKpis();
    }

    @Override
    public void notify(final Event event) throws RemoteException
    {
        if (event.getType().equals(RoadNetwork.GTU_ADD_EVENT))
        {
            Gtu gtu = this.net.getGTU((String) event.getContent());
            gtu.addListener(this, LaneBasedGtu.LANE_CHANGE_EVENT);
        }
        else if (event.getType().equals(LaneBasedGtu.LANE_CHANGE_EVENT))
        {
            this.numberOfLaneChanges++;
        }
        else
        {
            super.notify(event);
        }
    }

    /**
     * Main program.
     * @param args command line arguments. See AbstractSimulationScript for available arguments.
     * @throws Exception when an exception occurs.
     */
    public static void main(final String[] args) throws Exception
    {
        ScenarioRing demo = new ScenarioRing();
        CliUtil.changeOptionDefault(demo, "simulationTime", "1800s");
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
