package org.opentrafficsim.i4driving.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.cli.CliUtil;
import org.djutils.data.Column;
import org.djutils.data.ListTable;
import org.djutils.draw.line.PolyLine2d;
import org.djutils.draw.line.Polygon2d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.Point2d;
import org.djutils.immutablecollections.ImmutableLinkedHashMap;
import org.djutils.immutablecollections.ImmutableMap;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.ContinuousStraight;
import org.opentrafficsim.core.geometry.Flattener;
import org.opentrafficsim.core.geometry.Flattener.NumSegments;
import org.opentrafficsim.core.geometry.FractionalLengthData;
import org.opentrafficsim.core.geometry.OtsLine2d;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.generator.Injections;
import org.opentrafficsim.road.gtu.generator.LaneBasedGtuGenerator;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlusFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.DefaultLmrsPerceptionFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.LmrsFactory;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.CrossSectionSlice;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LaneGeometryUtil;
import org.opentrafficsim.road.network.lane.Stripe;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.lane.object.detector.SinkDetector;
import org.opentrafficsim.swing.script.AbstractSimulationScript;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * A small demo that generates two vehicles on a two-lane road using injections.
 */
public class InjectionsDemo extends AbstractSimulationScript
{

    /** */
    private static final long serialVersionUID = 20240418L;

    /**
     * Constructor.
     */
    protected InjectionsDemo()
    {
        super("Injections", "Injections demo");
    }

    /**
     * Main program.
     * @param args command line arguments. See AbstractSimulationScript for available arguments.
     * @throws Exception when an exception occurs.
     */
    public static void main(final String[] args) throws Exception
    {
        InjectionsDemo demo = new InjectionsDemo();
        CliUtil.execute(demo, args);
        demo.start();
    }

    /** {@inheritDoc} */
    @Override
    protected RoadNetwork setupSimulation(final OtsSimulatorInterface sim) throws Exception
    {
        // Create a simple straight 2-lane highway
        RoadNetwork network = new RoadNetwork("injections", sim);
        OrientedPoint2d pointA = new OrientedPoint2d(0.0, 0.0, 0.0);
        Point2d pointB = new Point2d(500.0, 0.0);
        Node nodeA = new Node(network, "A", pointA);
        Node nodeB = new Node(network, "B", pointB);

        ContinuousStraight designLine = new ContinuousStraight(pointA, pointA.distance(pointB));

        CrossSectionLink linkAB = new CrossSectionLink(network, "AB", nodeA, nodeB, DefaultsNl.FREEWAY,
                new OtsLine2d(pointA, pointB), FractionalLengthData.of(0.0, 0.0, 1.0, 0.0), LaneKeepingPolicy.KEEPRIGHT);
        Length laneWidth = Length.instantiateSI(3.5);
        Map<GtuType, Speed> speedLimits = Map.of(DefaultsNl.CAR, new Speed(130.0, SpeedUnit.KM_PER_HOUR));
        Flattener flattener = new NumSegments(64);

        List<CrossSectionSlice> slices = LaneGeometryUtil.getSlices(designLine, laneWidth.times(0.5).neg(), laneWidth);
        PolyLine2d center = designLine.flattenOffset(LaneGeometryUtil.getCenterOffsets(designLine, slices), flattener);
        PolyLine2d left = designLine.flattenOffset(LaneGeometryUtil.getLeftEdgeOffsets(designLine, slices), flattener);
        PolyLine2d right = designLine.flattenOffset(LaneGeometryUtil.getRightEdgeOffsets(designLine, slices), flattener);
        Polygon2d contour = LaneGeometryUtil.getContour(left, right);
        Lane lane1 = new Lane(linkAB, "Lane1", new OtsLine2d(center), contour, slices, DefaultsRoadNl.FREEWAY, speedLimits);
        
        slices = LaneGeometryUtil.getSlices(designLine, laneWidth.times(0.5), laneWidth);
        center = designLine.flattenOffset(LaneGeometryUtil.getCenterOffsets(designLine, slices), flattener);
        left = designLine.flattenOffset(LaneGeometryUtil.getLeftEdgeOffsets(designLine, slices), flattener);
        right = designLine.flattenOffset(LaneGeometryUtil.getRightEdgeOffsets(designLine, slices), flattener);
        contour = LaneGeometryUtil.getContour(left, right);
        Lane lane2 = new Lane(linkAB, "Lane2", new OtsLine2d(center), contour, slices, DefaultsRoadNl.FREEWAY, speedLimits);
        
        Length stripeWidth = Length.instantiateSI(0.2);
        slices = LaneGeometryUtil.getSlices(designLine, Length.ZERO, stripeWidth);
        center = designLine.flattenOffset(LaneGeometryUtil.getCenterOffsets(designLine, slices), flattener);
        left = designLine.flattenOffset(LaneGeometryUtil.getLeftEdgeOffsets(designLine, slices), flattener);
        right = designLine.flattenOffset(LaneGeometryUtil.getRightEdgeOffsets(designLine, slices), flattener);
        contour = LaneGeometryUtil.getContour(left, right);
        new Stripe(Type.DASHED, linkAB, new OtsLine2d(center), contour, slices);
        
        slices = LaneGeometryUtil.getSlices(designLine, laneWidth.times(0.97).neg(), stripeWidth);
        center = designLine.flattenOffset(LaneGeometryUtil.getCenterOffsets(designLine, slices), flattener);
        left = designLine.flattenOffset(LaneGeometryUtil.getLeftEdgeOffsets(designLine, slices), flattener);
        right = designLine.flattenOffset(LaneGeometryUtil.getRightEdgeOffsets(designLine, slices), flattener);
        contour = LaneGeometryUtil.getContour(left, right);
        new Stripe(Type.SOLID, linkAB, new OtsLine2d(center), contour, slices);
        
        slices = LaneGeometryUtil.getSlices(designLine, laneWidth.times(0.97), stripeWidth);
        center = designLine.flattenOffset(LaneGeometryUtil.getCenterOffsets(designLine, slices), flattener);
        left = designLine.flattenOffset(LaneGeometryUtil.getLeftEdgeOffsets(designLine, slices), flattener);
        right = designLine.flattenOffset(LaneGeometryUtil.getRightEdgeOffsets(designLine, slices), flattener);
        contour = LaneGeometryUtil.getContour(left, right);
        new Stripe(Type.SOLID, linkAB, new OtsLine2d(center), contour, slices);
        
        new SinkDetector(lane1, lane1.getLength(), sim, DefaultsRoadNl.ROAD_USERS);
        new SinkDetector(lane2, lane2.getLength(), sim, DefaultsRoadNl.ROAD_USERS);

        // Register default template
        GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);

        // Create injections and use it in the GTU generator as: arrival time generator, GTU characteristics generator, position
        // generator, room checker and id generator
        Injections injections = getInjections(network, sim.getModel().getStream("default"));
        new LaneBasedGtuGenerator("generator", injections, injections.asLaneBasedGtuCharacteristicsGenerator(), injections,
                network, sim, injections, injections);

        return network;
    }

    /**
     * This method creates injections for two vehicles. The first arrives at 1s on the slow lane and has a slower maximum speed.
     * The second arrives at 4s on the fast lane and has a higher maximum speed.
     * @param network network.
     * @param stream random number stream.
     * @return injections for two GTUs.
     */
    private static Injections getInjections(final RoadNetwork network, final StreamInterface stream)
    {
        // Table to contain GTU information
        ListTable table = new ListTable("injections", "injections", getColumns());

        // For each GTU data for all columns is supplied as in the order of getColumns()
        table.addRow(new Object[] {Duration.instantiateSI(1.0), "A", Length.instantiateSI(8.0), "Lane1", "AB",
                Speed.instantiateSI(25.0), "NL.CAR", Length.instantiateSI(5.0), Length.instantiateSI(2.5),
                Speed.instantiateSI(25.0)});
        table.addRow(new Object[] {Duration.instantiateSI(4.0), "B", Length.instantiateSI(6.0), "Lane2", "AB",
                Speed.instantiateSI(33.0), "NL.CAR", Length.instantiateSI(4.0), Length.instantiateSI(2.0),
                Speed.instantiateSI(40.0)});

        // Mapping of String to GTU type
        ImmutableMap<String, GtuType> gtuTypes = new ImmutableLinkedHashMap<>(Map.of("NL.CAR", DefaultsNl.CAR));

        // Default model factory
        LmrsFactory tacticalFactory = new LmrsFactory(new IdmPlusFactory(stream), new DefaultLmrsPerceptionFactory());
        LaneBasedStrategicalRoutePlannerFactory strategicalPlannerFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory);

        // Time to collision to accept generation of next GTU (i.e. arrival time is not forced hard)
        Duration timeToCollision = Duration.instantiateSI(5.0);

        // Injections based on the above
        return new Injections(table, network, gtuTypes, strategicalPlannerFactory, stream, timeToCollision);
    }

    /**
     * This method creates a list of table columns for a table that contains information for GTU injections. There are more
     * columns available, see {@code Injections} class and constructor.
     * @return list of columns.
     * @see Injections
     */
    private static List<Column<?>> getColumns()
    {
        List<Column<?>> columns = new ArrayList<>();
        columns.add(new Column<>(Injections.TIME_COLUMN, Injections.TIME_COLUMN, Duration.class, "s"));
        columns.add(new Column<>(Injections.ID_COLUMN, Injections.ID_COLUMN, String.class));
        columns.add(new Column<>(Injections.POSITION_COLUMN, Injections.POSITION_COLUMN, Length.class, "m"));
        columns.add(new Column<>(Injections.LANE_COLUMN, Injections.LANE_COLUMN, String.class));
        columns.add(new Column<>(Injections.LINK_COLUMN, Injections.LINK_COLUMN, String.class));
        columns.add(new Column<>(Injections.SPEED_COLUMN, Injections.SPEED_COLUMN, Speed.class, "m/s"));
        columns.add(new Column<>(Injections.GTU_TYPE_COLUMN, Injections.GTU_TYPE_COLUMN, String.class));
        columns.add(new Column<>(Injections.LENGTH_COLUMN, Injections.LENGTH_COLUMN, Length.class, "m"));
        columns.add(new Column<>(Injections.WIDTH_COLUMN, Injections.WIDTH_COLUMN, Length.class, "m"));
        columns.add(new Column<>(Injections.MAX_SPEED_COLUMN, Injections.MAX_SPEED_COLUMN, Speed.class, "m/s"));
        // more columns available, see Injections class and constructor
        return columns;
    }

}
