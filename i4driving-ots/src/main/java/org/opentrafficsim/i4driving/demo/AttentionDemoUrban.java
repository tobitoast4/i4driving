package org.opentrafficsim.i4driving.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djunits.unit.FrequencyUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.vector.FrequencyVector;
import org.djunits.value.vdouble.vector.TimeVector;
import org.djutils.cli.CliUtil;
import org.djutils.draw.line.PolyLine2d;
import org.djutils.draw.line.Polygon2d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.Point2d;
import org.opentrafficsim.animation.colorer.FixedColor;
import org.opentrafficsim.animation.gtu.colorer.AccelerationGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SpeedGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SwitchableGtuColorer;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.Bezier;
import org.opentrafficsim.core.geometry.ContinuousBezierCubic;
import org.opentrafficsim.core.geometry.ContinuousLine;
import org.opentrafficsim.core.geometry.ContinuousStraight;
import org.opentrafficsim.core.geometry.Flattener;
import org.opentrafficsim.core.geometry.Flattener.NumSegments;
import org.opentrafficsim.core.geometry.FractionalLengthData;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.geometry.OtsLine2d;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.Link;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.generator.characteristics.DefaultLaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.LaneAccessLaw;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.CrossSectionLink.Priority;
import org.opentrafficsim.road.network.lane.CrossSectionSlice;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LaneGeometryUtil;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.road.network.lane.LaneType;
import org.opentrafficsim.road.network.lane.Shoulder;
import org.opentrafficsim.road.network.lane.Stripe;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.lane.conflict.ConflictBuilder;
import org.opentrafficsim.road.network.lane.conflict.ConflictBuilder.RelativeWidthGenerator;
import org.opentrafficsim.road.network.lane.object.trafficlight.TrafficLight;
import org.opentrafficsim.road.od.Categorization;
import org.opentrafficsim.road.od.Category;
import org.opentrafficsim.road.od.Interpolation;
import org.opentrafficsim.road.od.OdApplier;
import org.opentrafficsim.road.od.OdMatrix;
import org.opentrafficsim.road.od.OdOptions;
import org.opentrafficsim.swing.script.AbstractSimulationScript;
import org.opentrafficsim.trafficcontrol.FixedTimeController;
import org.opentrafficsim.trafficcontrol.FixedTimeController.SignalGroup;

/**
 * Demo of attention in an urban setting.
 * @author wjschakel
 */
public class AttentionDemoUrban extends AbstractSimulationScript
{

    /** */
    private static final long serialVersionUID = 20240926L;

    private static final LaneType SHOULDER = new LaneType("Shoulder");

    private final double linkLength = 100.0;

    private final double intersection = 15.0;

    /**
     * Constructor.
     */
    protected AttentionDemoUrban()
    {
        super("Attention urban", "Demo of attention in an urban setting.");
        GtuColorer colorer = new SwitchableGtuColorer(0, new FixedColor(Color.BLUE, "Blue"),
                new SpeedGtuColorer(new Speed(60.0, SpeedUnit.KM_PER_HOUR)),
                new AccelerationGtuColorer(Acceleration.instantiateSI(-6.0), Acceleration.instantiateSI(2.0)),
                new SplitColorer());
        setGtuColorer(colorer);
    }

    /**
     * Main program.
     * @param args command line arguments. See AbstractSimulationScript for available arguments.
     * @throws Exception when an exception occurs.
     */
    public static void main(String[] args) throws Exception
    {
        AttentionDemoUrban demo = new AttentionDemoUrban();
        CliUtil.execute(demo, args);
        demo.start();
    }

    /** {@inhertiDoc} */
    @Override
    protected RoadNetwork setupSimulation(final OtsSimulatorInterface sim) throws Exception
    {
        RoadNetwork network = new RoadNetwork("urban demo", sim);

        // Eastern this.intersection
        OrientedPoint2d pointNin2 = new OrientedPoint2d(this.linkLength / 2.0 + this.intersection / 3.0,
                this.linkLength + this.intersection / 2.0, Math.PI * 1.5);
        OrientedPoint2d pointNout2 = new OrientedPoint2d(this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                this.linkLength + this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointSin2 = new OrientedPoint2d(this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                -this.linkLength - this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointSout2 = new OrientedPoint2d(this.linkLength / 2.0 + this.intersection / 3.0,
                -this.linkLength - this.intersection / 2.0, Math.PI * 1.5);
        OrientedPoint2d pointPNin2 =
                new OrientedPoint2d(this.linkLength / 2.0 + this.intersection / 3.0, this.intersection / 2.0, Math.PI * 1.5);
        OrientedPoint2d pointPNout2 = new OrientedPoint2d(this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointPSin2 = new OrientedPoint2d(this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                -this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointPSout2 =
                new OrientedPoint2d(this.linkLength / 2.0 + this.intersection / 3.0, -this.intersection / 2.0, Math.PI * 1.5);
        Node nodeNin2 = new Node(network, "Nin2", pointNin2);
        Node nodeNout2 = new Node(network, "Nout2", pointNout2);
        Node nodeSin2 = new Node(network, "Sin2", pointSin2);
        Node nodeSout2 = new Node(network, "Sout2", pointSout2);
        Node nodePNin2 = new Node(network, "PNin2", pointPNin2);
        Node nodePNout2 = new Node(network, "PNout2", pointPNout2);
        Node nodePSin2 = new Node(network, "PSin2", pointPSin2);
        Node nodePSout2 = new Node(network, "PSout2", pointPSout2);

        OrientedPoint2d pointEin =
                new OrientedPoint2d(this.linkLength * 1.5 + this.intersection, this.intersection / 6.0, Math.PI * 1.0);
        OrientedPoint2d pointEout =
                new OrientedPoint2d(this.linkLength * 1.5 + this.intersection, -this.intersection / 6.0, 0.0);
        OrientedPoint2d pointPEin =
                new OrientedPoint2d(this.linkLength * 0.5 + this.intersection, this.intersection / 6.0, Math.PI * 1.0);
        OrientedPoint2d pointPEout =
                new OrientedPoint2d(this.linkLength * 0.5 + this.intersection, -this.intersection / 6.0, 0.0);
        OrientedPoint2d pointCEin = new OrientedPoint2d(this.linkLength * 0.5, this.intersection / 6.0, Math.PI * 1.0);
        OrientedPoint2d pointCEout = new OrientedPoint2d(this.linkLength * 0.5, -this.intersection / 6.0, 0.0);
        Node nodeEin = new Node(network, "Ein", pointEin);
        Node nodeEout = new Node(network, "Eout", pointEout);
        Node nodePEin = new Node(network, "PEin", pointPEin);
        Node nodePEout = new Node(network, "PEout", pointPEout);
        Node nodeCEin = new Node(network, "CEin", pointCEin);
        Node nodeCEout = new Node(network, "CEout", pointCEout);

        // Western this.intersection
        double dx = -this.linkLength - this.intersection;
        OrientedPoint2d pointNin1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + this.intersection / 3.0,
                this.linkLength + this.intersection / 2.0, Math.PI * 1.5);
        OrientedPoint2d pointNout1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                this.linkLength + this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointSin1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                -this.linkLength - this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointSout1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + this.intersection / 3.0,
                -this.linkLength - this.intersection / 2.0, Math.PI * 1.5);
        OrientedPoint2d pointPNin1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + this.intersection / 3.0,
                this.intersection / 2.0, Math.PI * 1.5);
        OrientedPoint2d pointPNout1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointPSin1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + 2.0 * this.intersection / 3.0,
                -this.intersection / 2.0, Math.PI * 0.5);
        OrientedPoint2d pointPSout1 = new OrientedPoint2d(dx + this.linkLength / 2.0 + this.intersection / 3.0,
                -this.intersection / 2.0, Math.PI * 1.5);
        Node nodeNin1 = new Node(network, "Nin1", pointNin1);
        Node nodeNout1 = new Node(network, "Nout1", pointNout1);
        Node nodeSin1 = new Node(network, "Sin1", pointSin1);
        Node nodeSout1 = new Node(network, "Sout1", pointSout1);
        Node nodePNin1 = new Node(network, "PNin1", pointPNin1);
        Node nodePNout1 = new Node(network, "PNout1", pointPNout1);
        Node nodePSin1 = new Node(network, "PSin1", pointPSin1);
        Node nodePSout1 = new Node(network, "PSout1", pointPSout1);

        OrientedPoint2d pointWout =
                new OrientedPoint2d(-this.linkLength * 1.5 - this.intersection, this.intersection / 6.0, Math.PI * 1.0);
        OrientedPoint2d pointWin =
                new OrientedPoint2d(-this.linkLength * 1.5 - this.intersection, -this.intersection / 6.0, 0.0);
        OrientedPoint2d pointPWout =
                new OrientedPoint2d(-this.linkLength * 0.5 - this.intersection, this.intersection / 6.0, Math.PI * 1.0);
        OrientedPoint2d pointPWin =
                new OrientedPoint2d(-this.linkLength * 0.5 - this.intersection, -this.intersection / 6.0, 0.0);
        OrientedPoint2d pointCWout = new OrientedPoint2d(-this.linkLength * 0.5, this.intersection / 6.0, Math.PI * 1.0);
        OrientedPoint2d pointCWin = new OrientedPoint2d(-this.linkLength * 0.5, -this.intersection / 6.0, 0.0);
        Node nodeWout = new Node(network, "Wout", pointWout);
        Node nodeWin = new Node(network, "Win", pointWin);
        Node nodePWin = new Node(network, "PWin", pointPWin);
        Node nodePWout = new Node(network, "PWout", pointPWout);
        Node nodeCWin = new Node(network, "CWin", pointCWin);
        Node nodeCWout = new Node(network, "CWout", pointCWout);

        // Links
        makeLink(network, nodeNin2, nodePNin2);
        makeLink(network, nodePNout2, nodeNout2);
        makeLink(network, nodeEin, nodePEin);
        makeLink(network, nodePEout, nodeEout);
        makeLink(network, nodeSin2, nodePSin2);
        makeLink(network, nodePSout2, nodeSout2);
        makeLink(network, nodePNin2, nodeCEin);
        makeLink(network, nodePNin2, nodePSout2);
        makeLink(network, nodePNin2, nodePEout);
        makeLink(network, nodePEin, nodePNout2);
        makeLink(network, nodePEin, nodeCEin);
        makeLink(network, nodePEin, nodePSout2);
        makeLink(network, nodePSin2, nodePEout);
        makeLink(network, nodePSin2, nodePNout2);
        makeLink(network, nodePSin2, nodeCEin);
        makeLink(network, nodeCEout, nodePSout2);
        makeLink(network, nodeCEout, nodePEout);
        makeLink(network, nodeCEout, nodePNout2);

        makeLink(network, nodeCEin, nodeCWout);
        makeLink(network, nodeCWin, nodeCEout);

        makeLink(network, nodeNin1, nodePNin1);
        makeLink(network, nodePNout1, nodeNout1);
        makeLink(network, nodePWout, nodeWout);
        makeLink(network, nodeWin, nodePWin);
        makeLink(network, nodeSin1, nodePSin1);
        makeLink(network, nodePSout1, nodeSout1);
        makeLink(network, nodePNin1, nodePWout);
        makeLink(network, nodePNin1, nodePSout1);
        makeLink(network, nodePNin1, nodeCWin);
        makeLink(network, nodeCWout, nodePNout1);
        makeLink(network, nodeCWout, nodePWout);
        makeLink(network, nodeCWout, nodePSout1);
        makeLink(network, nodePSin1, nodeCWin);
        makeLink(network, nodePSin1, nodePNout1);
        makeLink(network, nodePSin1, nodePWout);
        makeLink(network, nodePWin, nodePSout1);
        makeLink(network, nodePWin, nodeCWin);
        makeLink(network, nodePWin, nodePNout1);

        List<Node> origins = new ArrayList<>();
        List<Node> destinations = new ArrayList<>();
        for (Node node : network.getNodeMap().values())
        {
            if (node.getId().startsWith("N") || node.getId().startsWith("E") || node.getId().startsWith("S")
                    || node.getId().startsWith("W"))
            {
                if (node.getId().contains("in"))
                {
                    origins.add(node);
                }
                else if (node.getId().contains("out"))
                {
                    destinations.add(node);
                }
            }
        }
        OdMatrix od = new OdMatrix("od", origins, destinations, Categorization.UNCATEGORIZED,
                new TimeVector(new double[] {0.0, 3600.0}), Interpolation.LINEAR);
        for (Node from : origins)
        {
            for (Node to : destinations)
            {
                // Skip U-turns
                if (!from.getId().replace("in", "out").equals(to.getId()))
                {
                    boolean main = (from.getId().startsWith("E") || from.getId().startsWith("W"))
                            && (to.getId().startsWith("E") || to.getId().startsWith("W"));
                    double[] demand = main ? new double[] {200.0, 300.0} : new double[] {20.0, 30.0};
                    od.putDemandVector(from, to, Category.UNCATEGORIZED, new FrequencyVector(demand, FrequencyUnit.PER_HOUR));
                }
            }
        }

        OdOptions options = new OdOptions();
        options.set(OdOptions.GTU_TYPE,
                new DefaultLaneBasedGtuCharacteristicsGeneratorOd.Factory(
                        DefaultLaneBasedGtuCharacteristicsGeneratorOd.defaultLmrs(sim.getModel().getStream("generation")))
                                .create());
        GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);
        OdApplier.applyOd(network, od, options, DefaultsRoadNl.VEHICLES);

        // TODO: When OTS issue #134 is published, we can use ConflictBuilder.DEFAULT_WIDTH_GENERATOR
        ConflictBuilder.buildConflicts(network, sim, new RelativeWidthGenerator(0.7));

        Lane lane = ((CrossSectionLink) network.getLink("Sin2_PSin2")).getLanes().get(0);
        Route route = new Route("route", DefaultsNl.CAR, List.of(nodeSin2, nodePSin2, nodePNout2, nodeNout2));
        network.getLaneChangeInfo(lane, route, DefaultsNl.CAR, Length.instantiateSI(2000.0), LaneAccessLaw.LEGAL);

        addTrafficLights(network);

        return network;
    }

    private void makeLink(final RoadNetwork network, final Node from, final Node to)
            throws IllegalArgumentException, NetworkException
    {
        ContinuousLine line;
        double distance = from.getPoint().distance(to.getPoint());
        Flattener flattener = new NumSegments(64);
        if (from.getHeading().eq(to.getHeading()))
        {
            line = new ContinuousStraight(from.getLocation(), from.getLocation().distance(to.getLocation()));
        }
        else
        {
            double shape = distance < this.intersection / 2.0 ? 1.0 : 0.55;
            Point2d[] designPoints = Bezier.cubicControlPoints(from.getLocation(), to.getLocation(), shape, false);
            line = new ContinuousBezierCubic(designPoints[0], designPoints[1], designPoints[2], designPoints[3]);
        }
        OtsLine2d designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, 0.0), flattener));
        CrossSectionLink link = new CrossSectionLink(network, from.getId() + "_" + to.getId(), from, to, DefaultsNl.URBAN,
                designLine, new FractionalLengthData(0.0, 0.0), LaneKeepingPolicy.KEEPRIGHT);
        if (from.getId().contains("E") || from.getId().contains("W"))
        {
            link.setPriority(Priority.PRIORITY);
        }

        designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, 3.5), flattener));
        List<CrossSectionSlice> slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(3.5), Length.instantiateSI(3.5));
        PolyLine2d left = line.flattenOffset(new FractionalLengthData(0.0, 1.75), flattener);
        PolyLine2d right = line.flattenOffset(new FractionalLengthData(0.0, 5.25), flattener);
        Polygon2d contour = LaneGeometryUtil.getContour(left, right);
        new Shoulder(link, "leftShoulder", designLine, contour, slices, SHOULDER);

        designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, -3.5), flattener));
        slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(-3.5), Length.instantiateSI(3.5));
        left = line.flattenOffset(new FractionalLengthData(0.0, -1.75), flattener);
        right = line.flattenOffset(new FractionalLengthData(0.0, -5.25), flattener);
        contour = LaneGeometryUtil.getContour(left, right);
        new Shoulder(link, "rightShoulder", designLine, contour, slices, SHOULDER);

        designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, 0.0), flattener));
        slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(0.0), Length.instantiateSI(3.5));
        left = line.flattenOffset(new FractionalLengthData(0.0, -1.75), flattener);
        right = line.flattenOffset(new FractionalLengthData(0.0, 1.75), flattener);
        contour = LaneGeometryUtil.getContour(left, right);
        new Lane(link, "lane", designLine, contour, slices, DefaultsRoadNl.URBAN_ROAD,
                Map.of(DefaultsNl.VEHICLE, new Speed(50.0, SpeedUnit.KM_PER_HOUR)));

        if (distance > this.linkLength / 2.0)
        {
            designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, 1.75), flattener));
            slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(0.0), Length.instantiateSI(0.2));
            left = line.flattenOffset(new FractionalLengthData(0.0, 1.85), flattener);
            right = line.flattenOffset(new FractionalLengthData(0.0, 1.65), flattener);
            contour = LaneGeometryUtil.getContour(left, right);
            new Stripe(Type.SOLID, link, designLine, contour, slices);
        }
        if (distance < this.intersection / 2.0 || distance > this.linkLength / 2.0)
        {
            designLine = new OtsLine2d(line.flattenOffset(new FractionalLengthData(0.0, -1.75), flattener));
            slices = LaneGeometryUtil.getSlices(line, Length.instantiateSI(0.0), Length.instantiateSI(0.2));
            left = line.flattenOffset(new FractionalLengthData(0.0, -1.85), flattener);
            right = line.flattenOffset(new FractionalLengthData(0.0, -1.65), flattener);
            contour = LaneGeometryUtil.getContour(left, right);
            new Stripe(Type.SOLID, link, designLine, contour, slices);
        }
    }

    private void addTrafficLights(final RoadNetwork network) throws NetworkException
    {
        Lane north = ((CrossSectionLink) network.getLink("Nin1_PNin1")).getLanes().get(0);
        Lane east = ((CrossSectionLink) network.getLink("CEin_CWout")).getLanes().get(0);
        Lane south = ((CrossSectionLink) network.getLink("Sin1_PSin1")).getLanes().get(0);
        Lane west = ((CrossSectionLink) network.getLink("Win_PWin")).getLanes().get(0);

        Length dx = Length.instantiateSI(2.0);
        new TrafficLight("light", north, north.getLength().minus(dx), network.getSimulator());
        new TrafficLight("light", east, east.getLength().minus(dx), network.getSimulator());
        new TrafficLight("light", south, south.getLength().minus(dx), network.getSimulator());
        new TrafficLight("light", west, west.getLength().minus(dx), network.getSimulator());

        Set<SignalGroup> groups = new LinkedHashSet<>();
        Duration yellow = Duration.instantiateSI(3.0);
        groups.add(new SignalGroup("north", Set.of("Nin1_PNin1.lane.light", "Sin1_PSin1.lane.light"),
                Duration.instantiateSI(0.0), Duration.instantiateSI(17.0), yellow));
        groups.add(new SignalGroup("east", Set.of("CEin_CWout.lane.light"), Duration.instantiateSI(20.0),
                Duration.instantiateSI(32.0), yellow));
        groups.add(new SignalGroup("west", Set.of("Win_PWin.lane.light"), Duration.instantiateSI(55.0),
                Duration.instantiateSI(32.0), yellow));
        new FixedTimeController("controller", network.getSimulator(), network, Duration.instantiateSI(90.0), Duration.ZERO,
                groups);
    }

    // TODO: remove this class when updated SplitColorer is published in OTS
    private static class SplitColorer implements GtuColorer
    {

        /** Left color. */
        static final Color LEFT = Color.GREEN;

        /** Other color. */
        static final Color OTHER = Color.BLUE;

        /** Right color. */
        static final Color RIGHT = Color.RED;

        /** Unknown color. */
        static final Color UNKNOWN = Color.WHITE;

        /** The legend. */
        private static final List<LegendEntry> LEGEND;

        static
        {
            LEGEND = new ArrayList<>(4);
            LEGEND.add(new LegendEntry(LEFT, "Left", "Left"));
            LEGEND.add(new LegendEntry(RIGHT, "Right", "Right"));
            LEGEND.add(new LegendEntry(OTHER, "Other", "Other"));
            LEGEND.add(new LegendEntry(UNKNOWN, "Unknown", "Unknown"));
        }

        /** {@inheritDoc} */
        @Override
        public final Color getColor(final Gtu gtu)
        {
            if (!(gtu instanceof LaneBasedGtu))
            {
                return UNKNOWN;
            }
            LaneBasedGtu laneGtu = (LaneBasedGtu) gtu;
            LanePosition refPos;
            try
            {
                refPos = laneGtu.getReferencePosition();
            }
            catch (GtuException exception)
            {
                return UNKNOWN;
            }
            Link link = refPos.lane().getLink();
            Route route = laneGtu.getStrategicalPlanner().getRoute();
            if (route == null)
            {
                return UNKNOWN;
            }

            // get all links we can go in to
            Set<Link> nextLinks;
            Link preLink;
            do
            {
                try
                {
                    preLink = link;
                    nextLinks = link.getEndNode().nextLinks(gtu.getType(), link);
                    if (!nextLinks.isEmpty())
                    {
                        link = laneGtu.getStrategicalPlanner().nextLink(preLink, gtu.getType());
                    }
                }
                catch (NetworkException exception)
                {
                    return UNKNOWN;
                }
            }
            while (nextLinks.size() == 1);

            // dead end
            if (nextLinks.isEmpty())
            {
                return UNKNOWN;
            }

            // split
            try
            {
                double preAngle = preLink.getDesignLine().getLocationFraction(1.0).getDirZ();
                double angleLeft = 0.0;
                double angleRight = 0.0;
                Link linkLeft = null;
                Link linkRight = null;
                for (Link nextLink : nextLinks)
                {
                    double angle = nextLink.getStartNode().equals(link.getStartNode())
                            ? nextLink.getDesignLine().getLocationFraction(0.0).getDirZ()
                            : nextLink.getDesignLine().getLocationFraction(1.0).getDirZ() + Math.PI;
                    angle -= preAngle; // difference with from
                    while (angle < -Math.PI)
                    {
                        angle += Math.PI * 2;
                    }
                    while (angle > Math.PI)
                    {
                        angle -= Math.PI * 2;
                    }
                    if (angle < angleRight)
                    {
                        angleRight = angle;
                        linkRight = nextLink;
                    }
                    else if (angle > angleLeft)
                    {
                        angleLeft = angle;
                        linkLeft = nextLink;
                    }
                }
                if (link.equals(linkRight))
                {
                    return RIGHT;
                }
                else if (link.equals(linkLeft))
                {
                    return LEFT;
                }
                return OTHER;
            }
            catch (OtsGeometryException exception)
            {
                // should not happen as the fractions are 0.0 and 1.0
                throw new RuntimeException("Angle could not be calculated.", exception);
            }
        }

        /** {@inheritDoc} */
        @Override
        public final List<LegendEntry> getLegend()
        {
            return LEGEND;
        }

        /** {@inheritDoc} */
        @Override
        public final String toString()
        {
            return "Split";
        }

    }

}
