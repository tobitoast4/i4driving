package org.opentrafficsim.i4driving.test;

import java.awt.Dimension;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;

import org.djunits.unit.DirectionUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Direction;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.locationtech.jts.geom.Coordinate;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.distributions.ConstantGenerator;
import org.opentrafficsim.core.distributions.Generator;
import org.opentrafficsim.core.distributions.ProbabilityException;
import org.opentrafficsim.core.dsol.AbstractOtsModel;
import org.opentrafficsim.core.dsol.OtsAnimator;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.geometry.OtsLine3d;
import org.opentrafficsim.core.geometry.OtsPoint3d;
import org.opentrafficsim.core.gtu.GtuErrorHandler;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.network.LinkType;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.FixedRouteGenerator;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.draw.core.OtsDrawingException;
import org.opentrafficsim.i4driving.test.ConflictTest.ConflictModel;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristics;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuTemplate;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlusFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.DefaultLmrsPerceptionFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.LmrsFactory;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.lane.conflict.ConflictBuilder;
import org.opentrafficsim.road.network.lane.object.detector.SinkDetector;
import org.opentrafficsim.swing.gui.OtsAnimationPanel;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.DSOLException;

/**
 * Test of conflict approach on simple junction.
 * <p>
 * Copyright (c) 2013-2023 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * </p>
 * @author <a href="https://dittlab.tudelft.nl">Wouter Schakel</a>
 */
public class ConflictTest extends OtsSimulationApplication<ConflictModel>
{
    /** */
    private static final long serialVersionUID = 20161211L;

    /**
     * Create a T-Junction demo.
     * @param title String; the title of the Frame
     * @param panel OtsAnimationPanel; the tabbed panel to display
     * @param model TJunctionModel; the model
     * @throws OtsDrawingException on animation error
     */
    public ConflictTest(final String title, final OtsAnimationPanel panel, final ConflictModel model) throws OtsDrawingException
    {
        super(model, panel);
    }

    /**
     * Main program.
     * @param args String[]; the command line arguments (not used)
     */
    public static void main(final String[] args)
    {
        demo(true);
    }

    /**
     * Start the demo.
     * @param exitOnClose boolean; when running stand-alone: true; when running as part of a demo: false
     */
    public static void demo(final boolean exitOnClose)
    {
        try
        {
            OtsAnimator simulator = new OtsAnimator("ConflictTest");
            final ConflictModel junctionModel = new ConflictModel(simulator);
            simulator.initialize(Time.ZERO, Duration.ZERO, Duration.instantiateSI(3600.0), junctionModel);
            OtsAnimationPanel animationPanel = new OtsAnimationPanel(junctionModel.getNetwork().getExtent(),
                    new Dimension(800, 600), simulator, junctionModel, DEFAULT_COLORER, junctionModel.getNetwork());
            ConflictTest app = new ConflictTest("T-Junction demo", animationPanel, junctionModel);
            app.setExitOnClose(exitOnClose);
            animationPanel.enableSimulationControlButtons();
        }
        catch (SimRuntimeException | NamingException | RemoteException | OtsDrawingException | DSOLException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * The simulation model.
     */
    public static class ConflictModel extends AbstractOtsModel
    {
        /** */
        private static final long serialVersionUID = 20161211L;

        /** The network. */
        private RoadNetwork network;

        /**
         * @param simulator OtsSimulatorInterface; the simulator for this model
         */
        public ConflictModel(final OtsSimulatorInterface simulator)
        {
            super(simulator);
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            try
            {
                //URL xmlURL = URLResource.getResource("/ConflictTest.xml");
                //this.network = new RoadNetwork("ConflictTest", getSimulator());
                //XmlNetworkLaneParser.build(xmlURL, this.network, true);
                
                this.network = createDummyNetwork(getSimulator());

                create_single_gtu("3", this.network, getSimulator(), "A", "B2", "A-B", "B-B2", "Lane", "Lane", Length.instantiateSI(5));
                //create_single_gtu("2", this.network, getSimulator(), "A", "B", "A-B", "A-B", "Lane", "Lane", Length.instantiateSI(10));
                //create_single_gtu("1", this.network, getSimulator(), "A", "B", "A-B", "A-B", "Lane", "Lane", Length.instantiateSI(15));
                
                // create a sink 10m before the end of the lane
                Lane lane_end = (Lane) ((CrossSectionLink) this.network.getLink("B-B2")).getCrossSectionElement("Lane");
                new SinkDetector(lane_end, lane_end.getLength().minus(Length.instantiateSI(10)), getSimulator(), DefaultsRoadNl.ROAD_USERS);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        
        
        private RoadNetwork createDummyNetwork(OtsSimulatorInterface sim) throws NetworkException, OtsGeometryException {

            RoadNetwork net = new RoadNetwork("CR-Scenario Network", sim);

            Node a = new Node(net, "A", new OtsPoint3d(100, 0), new Direction(180, DirectionUnit.EAST_DEGREE));
            Node b = new Node(net, "B", new OtsPoint3d(-12, 0),new Direction(180, DirectionUnit.EAST_DEGREE));
            Node b2 = new Node(net, "B2", new OtsPoint3d(-100, 0),new Direction(180, DirectionUnit.EAST_DEGREE));
            Node c = new Node(net, "C", new OtsPoint3d(0, 10), new Direction(270, DirectionUnit.EAST_DEGREE));
            Node d = new Node(net, "D", new OtsPoint3d(0, -10),new Direction(270, DirectionUnit.EAST_DEGREE));
            Node e = new Node(net, "E", new OtsPoint3d(3.5, -10), new Direction(90, DirectionUnit.EAST_DEGREE));
            Node f = new Node(net, "F", new OtsPoint3d(3.5, 10), new Direction(90, DirectionUnit.EAST_DEGREE));

            LinkType link_type = DefaultsNl.ROAD;
            LaneKeepingPolicy policy = LaneKeepingPolicy.KEEPRIGHT;

            OtsLine3d line_ab = new OtsLine3d(new Coordinate[]{new Coordinate(100, 0), new Coordinate(-12, 0)});
            CrossSectionLink ab = new CrossSectionLink(net, "A-B", a, b, link_type, line_ab, policy);
            OtsLine3d line_cd = new OtsLine3d(new Coordinate[]{new Coordinate(0, 10), new Coordinate(0, -10)});
            CrossSectionLink cd = new CrossSectionLink(net, "C-D", c, d, link_type, line_cd, policy);
            OtsLine3d line_ef = new OtsLine3d(new Coordinate[]{new Coordinate(3.5, -10), new Coordinate(3.5, 10)});
            CrossSectionLink ef = new CrossSectionLink(net, "E-F", e, f, link_type, line_ef, policy);
            OtsLine3d line_cb = new OtsLine3d(new Coordinate[]{new Coordinate(0, 10), new Coordinate(-0.5, 6),
                    new Coordinate(-2.2, 2.2), new Coordinate(-6, 0.5), new Coordinate(-12, 0)});
            CrossSectionLink cb = new CrossSectionLink(net, "C-B", c, b, link_type, line_cb, policy);
            OtsLine3d line_bb2 = new OtsLine3d(new Coordinate[]{new Coordinate(-12, 0), new Coordinate(-100, 0)});
            CrossSectionLink bb2 = new CrossSectionLink(net, "B-B2", b, b2, link_type, line_bb2, policy);


            double speed = 44.0;

            Lane l1 = new Lane(ab, "Lane", Length.ZERO, Length.instantiateSI(3.5), DefaultsRoadNl.URBAN_ROAD, new HashMap());
            l1.setSpeedLimit(DefaultsNl.ROAD_USER, Speed.instantiateSI(speed));

            Lane l2 = new Lane(cd, "Lane", Length.ZERO, Length.instantiateSI(3.5), DefaultsRoadNl.URBAN_ROAD, new HashMap());
            l2.setSpeedLimit(DefaultsNl.ROAD_USER, Speed.instantiateSI(speed));

            Lane l3 = new Lane(ef, "Lane", Length.ZERO, Length.instantiateSI(3.5), DefaultsRoadNl.URBAN_ROAD, new HashMap());
            l3.setSpeedLimit(DefaultsNl.ROAD_USER, Speed.instantiateSI(speed));

            Lane l4 = new Lane(cb, "Lane", Length.ZERO, Length.instantiateSI(3.5), DefaultsRoadNl.URBAN_ROAD, new HashMap());
            l4.setSpeedLimit(DefaultsNl.ROAD_USER, Speed.instantiateSI(speed));

            Lane l5 = new Lane(bb2, "Lane", Length.ZERO, Length.instantiateSI(3.5), DefaultsRoadNl.URBAN_ROAD, new HashMap());
            l5.setSpeedLimit(DefaultsNl.ROAD_USER, Speed.instantiateSI(speed));

            ConflictBuilder.buildConflicts(net, sim, new ConflictBuilder.RelativeWidthGenerator(0.5));

            return net;
        }
        

        private void create_single_gtu(String id, RoadNetwork net, OtsSimulatorInterface sim, String node_start_id,
                String node_end_id, String link_start_id, String link_end_id, String lane_start_id, String lane_end_id, Length position)
                throws NetworkException, ProbabilityException, ParameterException, GtuException, SimRuntimeException,
                OtsGeometryException
        {
            StreamInterface stream = new MersenneTwister(12345);
            Node node_start = net.getNode(node_start_id);
            Node node_end = net.getNode(node_end_id);

            Lane lane_start = (Lane) ((CrossSectionLink) net.getLink(link_start_id)).getCrossSectionElement(lane_start_id);

            // Route route = net.getShortestRouteBetween(DefaultsNl.CAR, node_start, node_end);
            Route route = new Route("A-B", DefaultsNl.CAR).addNode(node_start).addNode(net.getNode("B")).addNode(node_end);
            Generator<Route> route_generator = new FixedRouteGenerator(route);

            IdmPlusFactory idm_plus_factory = new IdmPlusFactory(stream);
            LmrsFactory tactical_factory = new LmrsFactory(idm_plus_factory, new DefaultLmrsPerceptionFactory());
            LaneBasedStrategicalRoutePlannerFactory strategical_factory =
                    new LaneBasedStrategicalRoutePlannerFactory(tactical_factory);

            // specify properties of the GTU
            Generator<Length> length_generator = new ConstantGenerator<>(new Length(4.5, LengthUnit.METER));
            Generator<Length> width_generator = new ConstantGenerator<>(new Length(2, LengthUnit.METER));
            Generator<Speed> maximum_speed_generator = new ConstantGenerator<>(new Speed(50, SpeedUnit.KM_PER_HOUR));

            Set<LanePosition> positions = new HashSet<>();

            // generator is created 5m after the start node
            positions.add(new LanePosition(lane_start, position));

            LaneBasedGtuTemplate template_gtu_type = new LaneBasedGtuTemplate(DefaultsNl.CAR, length_generator, width_generator,
                    maximum_speed_generator, strategical_factory, route_generator);
            LaneBasedGtuCharacteristics characteristics_gtu_type = template_gtu_type.draw();

            LaneBasedGtu gtu = new LaneBasedGtu(id, characteristics_gtu_type.getGtuType(), characteristics_gtu_type.getLength(),
                    characteristics_gtu_type.getWidth(), characteristics_gtu_type.getMaximumSpeed(),
                    characteristics_gtu_type.getFront(), net);
            gtu.setMaximumAcceleration(characteristics_gtu_type.getMaximumAcceleration());
            gtu.setMaximumDeceleration(characteristics_gtu_type.getMaximumDeceleration());
            gtu.setVehicleModel(characteristics_gtu_type.getVehicleModel());
            gtu.setNoLaneChangeDistance(null);
            gtu.setInstantaneousLaneChange(false);
            gtu.setErrorHandler(GtuErrorHandler.THROW);
            gtu.init(
                    characteristics_gtu_type.getStrategicalPlannerFactory().create(gtu, characteristics_gtu_type.getRoute(),
                            characteristics_gtu_type.getOrigin(), characteristics_gtu_type.getDestination()),
                    positions, new Speed(20, SpeedUnit.KM_PER_HOUR));
        }

        /** {@inheritDoc} */
        @Override
        public RoadNetwork getNetwork()
        {
            return this.network;
        }

    }
}
