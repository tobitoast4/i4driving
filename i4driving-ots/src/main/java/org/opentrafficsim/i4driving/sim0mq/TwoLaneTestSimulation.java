package org.opentrafficsim.i4driving.sim0mq;

import java.util.Arrays;
import java.util.List;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.draw.point.Point2d;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.gtu.GtuCharacteristics;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.core.network.LinkType;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.i4driving.object.LocalDistraction;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlannerFactory;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristics;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.gtu.lane.VehicleModel;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.factory.LaneFactory;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.road.network.lane.LaneType;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.lane.object.detector.SinkDetector;
import org.opentrafficsim.road.od.Category;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Defines a simple two-lane test network to develop OtsTransceiver.
 * @author wjschakel
 * @author Bramin Ramachandra Ravi Kiran
 */
public final class TwoLaneTestSimulation implements Sim0mqSimulation
{

    /** Network. */
    private final RoadNetwork network;

    /** GTU characteristics generator. */
    private final LaneBasedGtuCharacteristicsGeneratorOd charateristicsGeneratorOd;

    /** Parameter factory. */
    private ParameterFactorySim0mq parameterFactory;

    /**
     * Constructor.
     * @param simulator simulator
     * @param tacticalFactory tactical planner factory
     * @throws NetworkException
     * @throws OtsGeometryException
     * @throws GtuException
     */
    public TwoLaneTestSimulation(final OtsSimulatorInterface simulator, final ScenarioTacticalPlannerFactory tacticalFactory)
            throws GtuException, OtsGeometryException, NetworkException
    {
        // Create Road-Network
        this.network = new RoadNetwork("Two-Lane Test Network", simulator);

        // Create Points and Nodes
        Point2d pointA = new Point2d(0.0, 0.0);
        Point2d pointB = new Point2d(500.0, 0.0);

        Node nodeA = new Node(this.network, "A", pointA);
        Node nodeB = new Node(this.network, "B", pointB);

        // Define Link and Lane Type
        LinkType linkType = DefaultsNl.URBAN;
        LaneKeepingPolicy policy = LaneKeepingPolicy.KEEPRIGHT;
        GtuType gtuType = DefaultsNl.VEHICLE;
        Length laneWidth = Length.instantiateSI(3.5);
        LaneType laneType = DefaultsRoadNl.URBAN_ROAD;
        Speed speedLimit = new Speed(50.0, SpeedUnit.KM_PER_HOUR);

        // Create Lanes
        List<Lane> lanesAB = new LaneFactory(this.network, nodeA, nodeB, linkType, simulator, policy, gtuType)
                .leftToRight(2.0, laneWidth, laneType, speedLimit).addLanes(Type.DASHED).getLanes();

        // Define Route
        List<Node> nodes = Arrays.asList(nodeA, nodeB);
        Route route = new Route("Fellow route", DefaultsNl.CAR, nodes);

        // Model
        StreamInterface stream = simulator.getModel().getStream("generation");
        tacticalFactory.setStream(stream);
        // LaneBasedTacticalPlannerFactory<?> tacticalFactory = mixinModel.getTacticalPlanner(stream);
        this.parameterFactory = new ParameterFactorySim0mq();
        this.parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.VGAIN, new Speed(35.0, SpeedUnit.KM_PER_HOUR));

        // GTU characteristics generator
        GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);
        GtuCharacteristics gtucharacteristics = GtuType.defaultCharacteristics(DefaultsNl.CAR, this.network, stream);
        LaneBasedStrategicalRoutePlannerFactory stratFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory, this.parameterFactory);
        this.charateristicsGeneratorOd = new LaneBasedGtuCharacteristicsGeneratorOd()
        {
            @Override
            public LaneBasedGtuCharacteristics draw(final Node origin, final Node destination, final Category category,
                    final StreamInterface randomStream) throws GtuException
            {
                return new LaneBasedGtuCharacteristics(gtucharacteristics, stratFactory, route, nodeA, nodeB,
                        VehicleModel.MINMAX);
            }
        };

        // Destroy GTUs when leaving simulation
        Length pos = Length.instantiateSI(200.0);
        Length length = Length.instantiateSI(30.0);
        for (Lane lane : lanesAB)
        {
            new SinkDetector(lane, lane.getLength().minus(Length.instantiateSI(50.0)), simulator, DefaultsRoadNl.ROAD_USERS);
            new LocalDistraction(lane.getId() + "_distraction", new LanePosition(lane, pos), length, 0.5,
                    LateralDirectionality.RIGHT);
        }

    }

    @Override
    public RoadNetwork getNetwork()
    {
        return this.network;
    }

    @Override
    public LaneBasedGtuCharacteristicsGeneratorOd getGtuCharacteristicsGeneratorOd()
    {
        return this.charateristicsGeneratorOd;
    }

    @Override
    public ParameterFactorySim0mq getParameterFactory()
    {
        return this.parameterFactory;
    }

}
