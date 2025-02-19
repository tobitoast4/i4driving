package org.opentrafficsim.i4driving.sim0mq;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.draw.point.Point2d;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuCharacteristics;
import org.opentrafficsim.core.gtu.GtuErrorHandler;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.core.network.LinkType;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.core.parameters.ParameterFactoryByType;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.i4driving.tactical.perception.mental.LaneChangeTask;
import org.opentrafficsim.i4driving.tactical.perception.mental.TaskManagerAr;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristics;
import org.opentrafficsim.road.gtu.lane.CollisionException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.VehicleModel;
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
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.factory.LaneFactory;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LaneType;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.lane.object.detector.SinkDetector;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Defines a simple two-lane test network to develop OtsTransceiver.
 * @author wjschakel
 * @author Bramin Ramachandra Ravi Kiran
 */
public final class TwoLaneTestSimulation
{

    /** Apply full Fuller and overwrite all other mental settings. */
    private boolean fullFuller = true;

    /** Apply Fuller. */
    private boolean fuller = true;

    /** Include car-following task demand in Fuller. */
    private boolean carFollowingTask = true;

    /** Include lane change task demand in Fuller. */
    private boolean laneChangeTask = true;

    /** Set lane change task as primary in Fuller. */
    private boolean laneChangeIsPrimary = true;

    /** Apply anticipation reliance in Fuller. */
    private boolean anticipationReliance = true;

    /** Adapt headway in Fuller. */
    private boolean adaptHeadway = true;

    /** Adapt speed in Fuller. */
    private boolean adaptSpeed = true;

    /** Anticipate multiple leaders in car-following. */
    private boolean multiAnticipation = true;

    /** Apply full social interactions and overwrite all other social settings. */
    private boolean fullSocio = true;

    /** Apply social interactions. */
    private boolean socio = true;

    /** Apply tailgating in social interactions. */
    private boolean tailgating = true;

    /** Apply lane change incentive in social interactions. */
    private boolean socioLaneChangeIncentive = true;

    /** Apply desired speed in social interactions. */
    private boolean socioDesiredSpeed = true;

    /** Fraction of drivers with over estimation. */
    private double fractionOverEstimation = 1.0;

    /** Network. */
    private RoadNetwork network;

    /** GTU characteristics. */
    private LaneBasedGtuCharacteristics gtuCharacteristics;

    /**
     * Constructor.
     * @param simulator simulator
     * @throws NetworkException
     * @throws OtsGeometryException
     * @throws GtuException
     */
    public TwoLaneTestSimulation(final OtsSimulatorInterface simulator)
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
        StreamInterface stream = simulator.getModel().getStream("generation");

        // Define GtuCharacteristics
        GtuType gtu = DefaultsNl.CAR;

        GtuType.registerTemplateSupplier(gtu, Defaults.NL);
        GtuCharacteristics gtucharacteristics = GtuType.defaultCharacteristics(gtu, this.network, stream);

        // Define Lmrs
        LaneBasedTacticalPlannerFactory<?> tacticalFactory = getTacticalPlanner(stream);
        ParameterFactoryByType parameterFactory = new ParameterFactoryByType();
        parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.VGAIN, new Speed(35.0, SpeedUnit.KM_PER_HOUR));

        LaneBasedStrategicalRoutePlannerFactory stratFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory, parameterFactory);

        // Define Route
        List<Node> nodes = Arrays.asList(nodeA, nodeB);
        Route route = new Route("Fellow route", gtu, nodes);

        // Define LaneBasedGtuCharacteristics
        this.gtuCharacteristics =
                new LaneBasedGtuCharacteristics(gtucharacteristics, stratFactory, route, nodeA, nodeB, VehicleModel.MINMAX);

        // Destroy GTUs when leaving simulation
        for (Lane lane : lanesAB)
        {
            new SinkDetector(lane, lane.getLength().minus(Length.instantiateSI(50.0)), simulator, DefaultsRoadNl.ROAD_USERS);
        }
    }

    /**
     * Returns the network.
     * @return network
     */
    public RoadNetwork getNetwork()
    {
        return this.network;
    }

    /**
     * Returns the GTU characteristics.
     * @return GTU characteristics
     */
    public LaneBasedGtuCharacteristics getGtuCharacteristics()
    {
        return this.gtuCharacteristics;
    }

    /**
     * Creates tactical planner factory for GTUs with ScenarioTacticalPlanner.
     * @param randomStream stream
     * @return tactical planner factory for GTUs with ScenarioTacticalPlanner
     */
    private LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner> getTacticalPlanner(final StreamInterface randomStream)
    {
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
                                System.out.println(ex.getCause().getMessage());
                                gtu.getSimulator().endReplication();
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
                        if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.fuller)
                        {
                            parameters.setDefaultParameter(Fuller.TC);
                            parameters.setDefaultParameter(Fuller.TS);
                            parameters.setDefaultParameter(Fuller.TS_CRIT);
                            parameters.setDefaultParameter(Fuller.TS_MAX);
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.carFollowingTask)
                            {
                                parameters.setDefaultParameter(CarFollowingTask.HEXP);
                            }
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA_MIN);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA_MAX);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.TR_MAX);
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.adaptHeadway)
                            {
                                parameters.setDefaultParameter(AdaptationHeadway.BETA_T);
                            }
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.adaptSpeed)
                            {
                                parameters.setDefaultParameter(AdaptationSpeed.BETA_V0);
                            }
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.multiAnticipation)
                            {
                                parameters.setDefaultParameter(IdmPlusMulti.NLEADERS);
                            }
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.anticipationReliance)
                            {
                                parameters.setDefaultParameter(TaskManagerAr.ALPHA);
                                parameters.setDefaultParameter(TaskManagerAr.BETA);
                            }
                        }
                        if (TwoLaneTestSimulation.this.fullSocio || TwoLaneTestSimulation.this.socio)
                        {
                            parameters.setDefaultParameter(Tailgating.RHO);
                            parameters.setDefaultParameter(LmrsParameters.SOCIO);
                        }
                        parameters.setParameter(Estimation.OVER_EST,
                                randomStream.nextDouble() <= TwoLaneTestSimulation.this.fractionOverEstimation ? 1.0 : -1.0);
                        return parameters;
                    }

                    /** {@inheritDoc} */
                    @Override
                    public ScenarioTacticalPlanner create(final LaneBasedGtu gtu) throws GtuException
                    {
                        gtu.setErrorHandler(this.errorHandler);

                        DesiredSpeedModel desiredSpeedModel = (TwoLaneTestSimulation.this.fullSocio
                                || (TwoLaneTestSimulation.this.socio && TwoLaneTestSimulation.this.socioDesiredSpeed))
                                        ? new SocioDesiredSpeed(AbstractIdm.DESIRED_SPEED) : AbstractIdm.DESIRED_SPEED;

                        CarFollowingModel idm = TwoLaneTestSimulation.this.multiAnticipation
                                ? new IdmPlusMulti(AbstractIdm.HEADWAY, desiredSpeedModel)
                                : new IdmPlus(AbstractIdm.HEADWAY, desiredSpeedModel);

                        Fuller mental = null;
                        if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.fuller)
                        {
                            Set<Task> tasks = new LinkedHashSet<>();
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.carFollowingTask)
                            {
                                tasks.add(new CarFollowingTask());
                            }
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.laneChangeTask)
                            {
                                tasks.add(new LaneChangeTask());
                            }
                            Set<BehavioralAdaptation> behavioralAdapatations = new LinkedHashSet<>();
                            behavioralAdapatations.add(new AdaptationSituationalAwareness());
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.adaptHeadway)
                            {
                                behavioralAdapatations.add(new AdaptationHeadway());
                            }
                            if (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.adaptSpeed)
                            {
                                behavioralAdapatations.add(new AdaptationSpeed());
                            }
                            String primaryTask =
                                    TwoLaneTestSimulation.this.laneChangeIsPrimary ? "lane-changing" : "car-following";
                            TaskManager taskManager =
                                    (TwoLaneTestSimulation.this.fullFuller || TwoLaneTestSimulation.this.anticipationReliance)
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
                        Tailgating tail = (TwoLaneTestSimulation.this.fullSocio
                                || (TwoLaneTestSimulation.this.socio && TwoLaneTestSimulation.this.tailgating))
                                        ? Tailgating.PRESSURE : Tailgating.NONE;
                        ScenarioTacticalPlanner tacticalPlanner = new ScenarioTacticalPlanner(idm, gtu, lanePerception,
                                Synchronization.PASSIVE, Cooperation.PASSIVE, GapAcceptance.INFORMED, tail);
                        tacticalPlanner.addMandatoryIncentive(new IncentiveRoute());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveSpeedWithCourtesy());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveKeep());
                        if (TwoLaneTestSimulation.this.fullSocio
                                || (TwoLaneTestSimulation.this.socio && TwoLaneTestSimulation.this.socioLaneChangeIncentive))
                        {
                            tacticalPlanner.addVoluntaryIncentive(new IncentiveSocioSpeed());
                        }
                        return tacticalPlanner;
                    }
                };
        return tacticalFactory;
    }

}
