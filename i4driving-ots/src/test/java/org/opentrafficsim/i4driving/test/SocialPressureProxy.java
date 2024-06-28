package org.opentrafficsim.i4driving.test;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.djunits.unit.FrequencyUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djunits.value.vdouble.vector.FrequencyVector;
import org.djunits.value.vdouble.vector.TimeVector;
import org.djunits.value.vdouble.vector.data.DoubleVectorData;
import org.djunits.value.vdouble.vector.data.DoubleVectorDataDense;
import org.djunits.value.vfloat.scalar.FloatLength;
import org.djunits.value.vfloat.scalar.FloatSpeed;
import org.djutils.cli.CliUtil;
import org.djutils.data.csv.CsvData;
import org.djutils.data.serialization.TextSerializationException;
import org.djutils.event.Event;
import org.djutils.event.EventListener;
import org.opentrafficsim.base.WeightedMeanAndSum;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.AbstractOtsModel;
import org.opentrafficsim.core.dsol.OtsAnimator;
import org.opentrafficsim.core.dsol.OtsSimulator;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.egtf.Converter;
import org.opentrafficsim.core.egtf.Quantity;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.geometry.OtsPoint3d;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactoryByType;
import org.opentrafficsim.core.units.distributions.ContinuousDistSpeed;
import org.opentrafficsim.draw.core.BoundsPaintScale;
import org.opentrafficsim.draw.core.OtsDrawingException;
import org.opentrafficsim.draw.graphs.AbstractContourPlot;
import org.opentrafficsim.draw.graphs.ContourDataSource;
import org.opentrafficsim.draw.graphs.ContourDataSource.ContourDataType;
import org.opentrafficsim.draw.graphs.GraphPath;
import org.opentrafficsim.draw.graphs.GraphType;
import org.opentrafficsim.draw.graphs.GraphUtil;
import org.opentrafficsim.draw.graphs.road.GraphLaneUtil;
import org.opentrafficsim.kpi.sampling.SamplingException;
import org.opentrafficsim.kpi.sampling.SpaceTimeRegion;
import org.opentrafficsim.kpi.sampling.Trajectory;
import org.opentrafficsim.kpi.sampling.TrajectoryGroup;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataLength;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataNumber;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataSpeed;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataType;
import org.opentrafficsim.kpi.sampling.meta.FilterDataGtuType;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.colorer.LmrsSwitchableColorer;
import org.opentrafficsim.road.gtu.generator.GeneratorPositions.LaneBias;
import org.opentrafficsim.road.gtu.generator.GeneratorPositions.LaneBiases;
import org.opentrafficsim.road.gtu.generator.MarkovCorrelation;
import org.opentrafficsim.road.gtu.generator.characteristics.DefaultLaneBasedGtuCharacteristicsGeneratorOd.Factory;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.InfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtu;
import org.opentrafficsim.road.gtu.lane.tactical.AbstractLaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.AbstractIdmFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModelFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.Idm;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlus;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.DefaultLmrsPerceptionFactory;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveKeep;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveRoute;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSocioSpeed;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSpeedWithCourtesy;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveStayRight;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.Lmrs;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.SocioDesiredSpeed;
import org.opentrafficsim.road.gtu.lane.tactical.util.ConflictUtil;
import org.opentrafficsim.road.gtu.lane.tactical.util.TrafficLightUtil;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Cooperation;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.GapAcceptance;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsUtil;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.MandatoryIncentive;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Synchronization;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Tailgating;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.VoluntaryIncentive;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.factory.LaneFactory;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.Stripe;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.lane.object.detector.SinkDetector;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;
import org.opentrafficsim.road.network.sampling.LaneDataRoad;
import org.opentrafficsim.road.network.sampling.RoadSampler;
import org.opentrafficsim.road.network.speed.SpeedLimitInfo;
import org.opentrafficsim.road.od.Categorization;
import org.opentrafficsim.road.od.Category;
import org.opentrafficsim.road.od.Interpolation;
import org.opentrafficsim.road.od.OdApplier;
import org.opentrafficsim.road.od.OdMatrix;
import org.opentrafficsim.road.od.OdOptions;
import org.opentrafficsim.swing.graphs.SwingContourPlot;
import org.opentrafficsim.swing.gui.OtsAnimationPanel;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.jstats.distributions.DistLogNormal;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.language.DSOLException;
import picocli.CommandLine.Option;

/**
 * This class simulates a simple lane drop stretch of highway with social interactions. As a proxy for social pressure it also
 * computes {@code (a - a')/b}, where {@code a} is the car-following acceleration, {@code a'} is the car-following acceleration
 * if the leader was in our shoes (i.e. at our gap and speed difference to the leader), and {@code b} is a normalization
 * acceleration that needs to be tuned.
 * @author wjschakel
 */
public class SocialPressureProxy
{

    /** Extended data for sampler regarding rho. */
    private static final ExtendedDataRho RHO = new ExtendedDataRho();

    /** Extended data for sampler regarding rho proxy. */
    private static ExtendedDataRhoProxy RHO_PROXY;

    /** Whether to show GUI. */
    @Option(names = "--gui", description = "GUI", defaultValue = "true")
    private boolean gui;

    /** Output directory. */
    @Option(names = "--outputDir", description = "Output directory", defaultValue = "../prototyping/socialpressure/")
    private String outputDir;

    /** Whether to use the multi-lane lanedrop network, or just a single lane. */
    @Option(names = "--multiLane", description = "Multi-lane", defaultValue = "false")
    private boolean multiLane;

    /** Whether to apply a distance discounting on the social pressure proxy. */
    @Option(names = "--discounted", description = "Space discounted social pressure proxy.", defaultValue = "true")
    private boolean discounted;

    /** Whether to apply a higher speed limit in the first section. */
    @Option(names = "--speedDrop", description = "Apply a 130-100km/h speed drop, or all 100km/h.", defaultValue = "false")
    private boolean speedDrop;

    // The following two parameters were calibrated on a single lane without speed drop

    /** Desired headway used in proxy car-following. */
    @Option(names = "--proxyHeadway", description = "Desired headway used in proxy car-following.")
    private Duration proxyHeadway = Duration.instantiateSI(2.146);

    /** Deceleration used to scale proxy car-following. */
    @Option(names = "--proxyDeceleration", description = "Deceleration used to scale proxy car-following.")
    private Acceleration proxyDeceleration = Acceleration.instantiateSI(6.309);

    /**
     * Main method.
     * @param args String[]; command line arguments (ignored);
     */
    public static void main(final String[] args)
    {
        try
        {
            SocialPressureProxy spp = new SocialPressureProxy();
            CliUtil.execute(spp, args);
            RHO_PROXY = new ExtendedDataRhoProxy(spp.discounted, spp.proxyHeadway, spp.proxyDeceleration);

            if (spp.gui)
            {
                OtsAnimator simulator = new OtsAnimator("SocialPressureProxy");
                SocialPressureProxyModel otsModel = spp.getModel(simulator);
                simulator.initialize(Time.ZERO, Duration.ZERO, Duration.instantiateSI(3600.0), otsModel);
                OtsAnimationPanel animationPanel =
                        new OtsAnimationPanel(otsModel.getNetwork().getExtent(), new Dimension(800, 600), simulator, otsModel,
                                new LmrsSwitchableColorer(DefaultsNl.GTU_TYPE_COLORS.toMap()), otsModel.getNetwork());
                OtsSimulationApplication<SocialPressureProxyModel> app =
                        new OtsSimulationApplication<>(otsModel, animationPanel);
                app.setExitOnClose(true);
                animationPanel.enableSimulationControlButtons();

                // graphs
                GraphPath<LaneDataRoad> left = GraphLaneUtil.createPath(spp.multiLane ? "Left" : "Lane",
                        ((CrossSectionLink) otsModel.getNetwork().getLink("AB")).getLanes().get(0));
                ContourDataSource sourceLeft = new ContourDataSource(otsModel.getSampler().getSamplerData(), left);
                SwingContourPlot rhoPlotLeft =
                        new SwingContourPlot(new ContourPlotRho(spp.multiLane ? "Rho left" : "Rho", simulator, sourceLeft));
                SwingContourPlot rhoPlotLeft2 = new SwingContourPlot(
                        new ContourPlotRhoProxy(spp.multiLane ? "Rho' left" : "Rho'", simulator, sourceLeft));
                TablePanel charts = new TablePanel(spp.multiLane ? 3 : 1, 2);
                charts.setCell(rhoPlotLeft.getContentPane(), 0);
                charts.setCell(rhoPlotLeft2.getContentPane(), spp.multiLane ? 3 : 1);
                if (spp.multiLane)
                {
                    GraphPath<LaneDataRoad> middle = GraphLaneUtil.createPath("Middle",
                            ((CrossSectionLink) otsModel.getNetwork().getLink("AB")).getLanes().get(1));
                    GraphPath<LaneDataRoad> right = GraphLaneUtil.createPath("Right",
                            ((CrossSectionLink) otsModel.getNetwork().getLink("AB")).getLanes().get(2));
                    ContourDataSource sourceMiddle = new ContourDataSource(otsModel.getSampler().getSamplerData(), middle);
                    ContourDataSource sourceRight = new ContourDataSource(otsModel.getSampler().getSamplerData(), right);
                    SwingContourPlot rhoPlotMiddle =
                            new SwingContourPlot(new ContourPlotRho("Rho middle", simulator, sourceMiddle));
                    SwingContourPlot rhoPlotRight =
                            new SwingContourPlot(new ContourPlotRho("Rho right", simulator, sourceRight));
                    SwingContourPlot rhoPlotMiddle2 =
                            new SwingContourPlot(new ContourPlotRhoProxy("Rho' middle", simulator, sourceMiddle));
                    SwingContourPlot rhoPlotRight2 =
                            new SwingContourPlot(new ContourPlotRhoProxy("Rho' right", simulator, sourceRight));
                    charts.setCell(rhoPlotMiddle.getContentPane(), 1);
                    charts.setCell(rhoPlotRight.getContentPane(), 2);
                    charts.setCell(rhoPlotMiddle2.getContentPane(), 4);
                    charts.setCell(rhoPlotRight2.getContentPane(), 5);
                }
                animationPanel.getTabbedPane().addTab("rho statistics", charts);
            }
            else
            {
                OtsSimulatorInterface simulator = new OtsSimulator("SocialPressureProxy");
                SocialPressureProxyModel otsModel = spp.getModel(simulator);
                simulator.initialize(Time.ZERO, Duration.ZERO, Duration.instantiateSI(3600.0), otsModel);
                double tReport = 60.0;
                Time t = simulator.getSimulatorAbsTime();
                while (t.si < 3600.0)
                {
                    simulator.step();
                    t = simulator.getSimulatorAbsTime();
                    if (t.si >= tReport)
                    {
                        System.out.println("Simulation time is " + t);
                        tReport += 60.0;
                    }
                }
                simulator.endReplication();
                System.exit(0);
            }
        }
        catch (SimRuntimeException | NamingException | RemoteException | OtsDrawingException | IndexOutOfBoundsException
                | DSOLException | NetworkException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Returns the model.
     * @param simulator OtsSimulatorInterface; simulator.
     * @return SocialPressureProxyModel; model.
     */
    private SocialPressureProxyModel getModel(final OtsSimulatorInterface simulator)
    {
        return new SocialPressureProxyModel(simulator, this.outputDir);
    }

    /**
     * Social pressure proxy model class.
     * @author wjschakel
     */
    public class SocialPressureProxyModel extends AbstractOtsModel implements EventListener
    {

        /** */
        private static final long serialVersionUID = 20240427L;

        /** Output directory. */
        private final String outputDir;

        /** The network. */
        private RoadNetwork network;

        /** The sampler. */
        private RoadSampler sampler;

        /**
         * Constructor.
         * @param simulator OtsSimulatorInterface; simulator.
         * @param outputDir String; output directory.
         */
        public SocialPressureProxyModel(final OtsSimulatorInterface simulator, final String outputDir)
        {
            super(simulator);
            this.outputDir = outputDir;
        }

        /**
         * Returns the network.
         */
        @Override
        public RoadNetwork getNetwork()
        {
            return this.network;
        }

        /**
         * Returns the sampler.
         */
        public RoadSampler getSampler()
        {
            return this.sampler;
        }

        /** {@inheritDoc} */
        @Override
        public void constructModel() throws SimRuntimeException
        {
            try
            {

                // Network
                this.network = new RoadNetwork("SocialPressureProxy", getSimulator());

                // Nodes
                OtsPoint3d pointA = new OtsPoint3d(0.0, 0.0, 0.0);
                OtsPoint3d pointB = new OtsPoint3d(1500.0, 0.0, 0.0);
                OtsPoint3d pointC = new OtsPoint3d(3000.0, 0.0, 0.0);
                OtsPoint3d pointD = new OtsPoint3d(5100.0, 0.0, 0.0); // TODO: remove extra 100m when later OTS version is used
                Node nodeA = new Node(this.network, "A", pointA);
                Node nodeB = new Node(this.network, "B", pointB);
                Node nodeC = new Node(this.network, "C", pointC);
                Node nodeD = new Node(this.network, "D", pointD);

                // Lanes (and links)
                Length laneWidth = Length.instantiateSI(3.5);
                Speed speedLimit2 = new Speed(100.0, SpeedUnit.KM_PER_HOUR);
                Speed speedLimit1 = speedDrop ? new Speed(130.0, SpeedUnit.KM_PER_HOUR) : speedLimit2;
                double dLat1 = multiLane ? 3.0 : 1.0;
                double dLat2 = multiLane ? 2.0 : 1.0;
                Stripe.Type[] upSections =
                        multiLane ? new Stripe.Type[] {Stripe.Type.DASHED, Stripe.Type.DASHED} : new Stripe.Type[0];
                Stripe.Type[] downSections = multiLane ? new Stripe.Type[] {Stripe.Type.DASHED} : new Stripe.Type[0];
                List<Lane> lanesAB = new LaneFactory(this.network, nodeA, nodeB, DefaultsNl.FREEWAY, getSimulator(),
                        LaneKeepingPolicy.KEEPRIGHT, DefaultsNl.VEHICLE)
                                .leftToRight(dLat1, laneWidth, DefaultsRoadNl.FREEWAY, speedLimit1).addLanes(upSections)
                                .addShoulder(laneWidth, LateralDirectionality.NONE).getLanes();
                List<Lane> lanesBC = new LaneFactory(this.network, nodeB, nodeC, DefaultsNl.FREEWAY, getSimulator(),
                        LaneKeepingPolicy.KEEPRIGHT, DefaultsNl.VEHICLE)
                                .leftToRight(dLat1, laneWidth, DefaultsRoadNl.FREEWAY, speedLimit2).addLanes(upSections)
                                .addShoulder(laneWidth, LateralDirectionality.NONE).getLanes();
                List<Lane> lanesCD = new LaneFactory(this.network, nodeC, nodeD, DefaultsNl.FREEWAY, getSimulator(),
                        LaneKeepingPolicy.KEEPRIGHT, DefaultsNl.VEHICLE)
                                .leftToRight(dLat2, laneWidth, DefaultsRoadNl.FREEWAY, speedLimit2).addLanes(downSections)
                                .addShoulder(laneWidth, LateralDirectionality.NONE).getLanes();

                // Demand
                DoubleVectorData vectorData = new DoubleVectorDataDense(new double[] {0.0, 1800.0, 3600.0});
                TimeVector globalTime = new TimeVector(vectorData, TimeUnit.DEFAULT);
                List<Node> origins = new ArrayList<>();
                origins.add(nodeA);
                List<Node> destinations = new ArrayList<>();
                destinations.add(nodeD);
                Categorization categorization = new Categorization("GtuType", GtuType.class);
                OdMatrix od = new OdMatrix("OD", origins, destinations, categorization, globalTime, Interpolation.LINEAR);
                vectorData =
                        new DoubleVectorDataDense(multiLane ? new double[] {3000.0 / 3600.0, 4000.0 / 3600.0, 2000.0 / 3600.0}
                                : new double[] {1500.0 / 3600.0, 2000.0 / 3600.0, 1000.0 / 3600.0});
                FrequencyVector demand = new FrequencyVector(vectorData, FrequencyUnit.SI);
                od.putDemandVector(nodeA, nodeD, new Category(categorization, DefaultsNl.CAR), demand, 0.9);
                od.putDemandVector(nodeA, nodeD, new Category(categorization, DefaultsNl.TRUCK), demand, 0.1);

                // Model factories
                StreamInterface stream = getSimulator().getModel().getStream("default");
                // parameters
                ParameterFactoryByType parameterFactory = new ParameterFactoryByType();
                parameterFactory.addParameter(Tailgating.RHO, 0.0);
                parameterFactory.addParameter(ParameterTypes.TMAX, Duration.instantiateSI(1.6));
                parameterFactory.addParameter(DefaultsNl.TRUCK, LmrsParameters.SOCIO, 1.0);
                parameterFactory.addParameter(DefaultsNl.TRUCK, LmrsParameters.VGAIN, new Speed(50.0, SpeedUnit.KM_PER_HOUR));
                parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.SOCIO, new DistTriangular(stream, 0.0, 0.25, 1.0));
                parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.VGAIN, // mu =~ 3.3789, sigma = 0.4, mode = 25.0
                        new ContinuousDistSpeed(new DistLogNormal(stream, Math.log(25.0) + 0.4 * 0.4, 0.4),
                                SpeedUnit.KM_PER_HOUR));
                parameterFactory.addParameter(DefaultsNl.TRUCK, ParameterTypes.A, Acceleration.instantiateSI(0.8));
                // IDM+ with dynamic desired speed for socio
                CarFollowingModelFactory<IdmPlus> idm =
                        new AbstractIdmFactory<>(new IdmPlus(Idm.HEADWAY, new SocioDesiredSpeed(Idm.DESIRED_SPEED)), stream);
                // incentives including socio-speed
                Set<MandatoryIncentive> mandatoryIncentives = new LinkedHashSet<>();
                mandatoryIncentives.add(new IncentiveRoute());
                Set<VoluntaryIncentive> voluntaryIncentives = new LinkedHashSet<>();
                voluntaryIncentives.add(new IncentiveSpeedWithCourtesy());
                voluntaryIncentives.add(new IncentiveKeep());
                voluntaryIncentives.add(new IncentiveSocioSpeed());
                VoluntaryIncentive stayRight = new IncentiveStayRight(); // only for trucks
                // LMRS with tailgating
                LaneBasedTacticalPlannerFactory<Lmrs> tacticalFatory =
                        new AbstractLaneBasedTacticalPlannerFactory<>(idm, new DefaultLmrsPerceptionFactory())
                        {
                            /** {@inheritDoc} */
                            @Override
                            public Lmrs create(final LaneBasedGtu gtu) throws GtuException
                            {
                                Lmrs lmrs = new Lmrs(nextCarFollowingModel(gtu), gtu,
                                        getPerceptionFactory().generatePerception(gtu), Synchronization.PASSIVE,
                                        Cooperation.PASSIVE, GapAcceptance.INFORMED, Tailgating.PRESSURE);
                                mandatoryIncentives.forEach(incentive -> lmrs.addMandatoryIncentive(incentive));
                                voluntaryIncentives.forEach(incentive -> lmrs.addVoluntaryIncentive(incentive));
                                if (gtu.getType().isOfType(DefaultsNl.TRUCK))
                                {
                                    voluntaryIncentives.add(stayRight); // incentive to stay away from the 3rd lane
                                }
                                return lmrs;
                            }

                            /** {@inheritDoc} */
                            @Override
                            public Parameters getParameters() throws ParameterException
                            {
                                ParameterSet parameters = new ParameterSet();
                                parameters.setDefaultParameters(LmrsUtil.class);
                                parameters.setDefaultParameters(LmrsParameters.class);
                                parameters.setDefaultParameters(ConflictUtil.class);
                                parameters.setDefaultParameters(TrafficLightUtil.class);
                                getCarFollowingParameters().setAllIn(parameters);
                                getPerceptionFactory().getParameters().setAllIn(parameters);
                                parameters.setDefaultParameter(ParameterTypes.VCONG);
                                parameters.setDefaultParameter(ParameterTypes.T0);
                                parameters.setDefaultParameter(ParameterTypes.LCDUR);
                                return parameters;
                            }
                        };

                LaneBasedStrategicalRoutePlannerFactory strategicalFactory =
                        new LaneBasedStrategicalRoutePlannerFactory(tacticalFatory, parameterFactory);
                LaneBasedGtuCharacteristicsGeneratorOd factory = new Factory(strategicalFactory).create();

                // Vehicle templates
                GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);
                GtuType.registerTemplateSupplier(DefaultsNl.TRUCK, Defaults.NL);

                // Sink sensors TODO: remove when later OTS version is used
                for (Lane lane : lanesCD)
                {
                    new SinkDetector(lane, lane.getLength().minus(Length.instantiateSI(100.0)), getSimulator(),
                            DefaultsRoadNl.VEHICLES);
                }

                // Vehicle generators
                OdOptions options = new OdOptions();
                options.set(OdOptions.GTU_TYPE, factory);
                options.set(OdOptions.INSTANT_LC, true);
                LaneBiases biases = new LaneBiases();
                biases.addBias(DefaultsNl.CAR, LaneBias.WEAK_LEFT);
                biases.addBias(DefaultsNl.TRUCK, LaneBias.TRUCK_RIGHT);
                options.set(OdOptions.LANE_BIAS, biases);
                options.set(OdOptions.NO_LC_DIST, Length.instantiateSI(100.0));
                MarkovCorrelation<GtuType, Frequency> markov = new MarkovCorrelation<>();
                markov.addState(DefaultsNl.TRUCK, 0.6);
                options.set(OdOptions.MARKOV, markov);
                OdApplier.applyOd(this.network, od, options, DefaultsRoadNl.VEHICLES);

                // Sampler
                Set<ExtendedDataType<?, ?, ?, GtuDataRoad>> extendedData = new LinkedHashSet<>();
                extendedData.add(RHO);
                extendedData.add(RHO_PROXY);
                extendedData.add(new ExtendedDataHeadway());
                extendedData.add(new ExtendedDataDesiredSpeedLeader());
                extendedData.add(new ExtendedDataSpeedLeader());
                this.sampler = new RoadSampler(extendedData, Set.of(new FilterDataGtuType()), this.network,
                        Frequency.instantiateSI(2.0));
                registerLanes(this.sampler, lanesAB);
                registerLanes(this.sampler, lanesBC);
                registerLanes(this.sampler, lanesCD);
                this.getSimulator().addListener(this, Replication.END_REPLICATION_EVENT); // to save data

            }
            catch (NetworkException | OtsGeometryException | ParameterException | RemoteException ex)
            {

            }

        }

        /**
         * Register lanes in the sampler for sampling.
         * @param sampler RoadSampler; sampler.
         * @param lanes List&lt;Lane&gt;; lanes.
         */
        private void registerLanes(final RoadSampler sampler, final List<Lane> lanes)
        {
            for (Lane lane : lanes)
            {
                sampler.registerSpaceTimeRegion(new SpaceTimeRegion<LaneDataRoad>(new LaneDataRoad(lane), Length.ZERO,
                        lane.getLength(), Time.ZERO, Time.instantiateSI(3600.0)));
            }
        }

        /** {@inheritDoc} */
        @Override
        public void notify(final Event event) throws RemoteException
        {
            // save sampler data
            try
            {
                CsvData.writeData(this.outputDir + "rho.csv", this.outputDir + "rho.csv.meta", this.sampler.getSamplerData());
                System.out.println("Sampler data saved.");
            }
            catch (IOException | TextSerializationException e)
            {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Data type for rho in the sampler.
     * @author wjschakel
     */
    private static class ExtendedDataRho extends ExtendedDataNumber<GtuDataRoad>
    {
        /**
         * Constructor.
         */
        public ExtendedDataRho()
        {
            super("rho", "rho");
        }

        /** {@inheritDoc} */
        @Override
        public Float getValue(final GtuDataRoad gtu)
        {
            try
            {
                PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders = gtu.getGtu().getTacticalPlanner().getPerception()
                        .getPerceptionCategory(NeighborsPerception.class).getLeaders(RelativeLane.CURRENT);
                if (leaders.isEmpty())
                {
                    return Float.NaN;
                }
                Parameters params = gtu.getGtu().getParameters();
                if (params.contains(Tailgating.RHO))
                {
                    return params.getParameter(Tailgating.RHO).floatValue();
                }
                return Float.NaN;
            }
            catch (ParameterException | OperationalPlanException ex)
            {
                System.out.println("Unable to obtain rho");
                return Float.NaN;
            }
        }
    }

    /**
     * Data type for rho proxy in the sampler.
     * @author wjschakel
     */
    private static class ExtendedDataRhoProxy extends ExtendedDataNumber<GtuDataRoad>
    {
        /** Whether to apply a distance discounting on the social pressure proxy. */
        private final boolean discounted;

        /** Desired headway used in proxy car-following. */
        private final Duration proxyHeadway;

        /** Deceleration used to scale proxy car-following. */
        private final Acceleration proxyDeceleration;

        /**
         * Constructor.
         * @param discounted boolean; whether to apply a distance discounting on the social pressure proxy.
         * @param proxyHeadway Duration; desired headway used in proxy car-following.
         * @param proxyDeceleration Acceleration; deceleration used to scale proxy car-following.
         */
        public ExtendedDataRhoProxy(final boolean discounted, final Duration proxyHeadway, final Acceleration proxyDeceleration)
        {
            super("rho'", "rho proxy");
            this.discounted = discounted;
            this.proxyHeadway = proxyHeadway;
            this.proxyDeceleration = proxyDeceleration;
        }

        /** {@inheritDoc} */
        @Override
        public Float getValue(final GtuDataRoad gtu)
        {
            try
            {
                PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders = gtu.getGtu().getTacticalPlanner().getPerception()
                        .getPerceptionCategory(NeighborsPerception.class).getLeaders(RelativeLane.CURRENT);
                if (leaders.isEmpty())
                {
                    return 0.0f;
                }

                Acceleration egoAcceleration = gtu.getGtu().getAcceleration();
                Speed egoSpeed = gtu.getGtu().getSpeed();
                SpeedLimitInfo sli =
                        gtu.getGtu().getTacticalPlanner().getPerception().getPerceptionCategory(InfrastructurePerception.class)
                                .getSpeedLimitProspect(RelativeLane.CURRENT).getSpeedLimitInfo(Length.ZERO);

                // get leader GTU's acceleration in our case
                UnderlyingDistance<LaneBasedGtu> leader = leaders.underlyingWithDistance().next();
                Parameters leaderParameters = leader.getObject().getParameters();

                // set regular following headway to compare, calculate acceleration, reset headway
                leaderParameters.setParameterResettable(ParameterTypes.T, this.proxyHeadway);
                Acceleration leaderAcceleration = leader.getObject().getTacticalPlanner().getCarFollowingModel()
                        .followingAcceleration(leaderParameters, egoSpeed, sli, leaders);
                leaderParameters.resetParameter(ParameterTypes.T);

                // scaling
                double x0 = gtu.getGtu().getParameters().getParameter(ParameterTypes.LOOKAHEAD).si;
                double discount = this.discounted ? (1 - leader.getDistance().si / x0) : 1.0;
                return (float) (Math.max(0.0,
                        Math.min((egoAcceleration.si - leaderAcceleration.si) / this.proxyDeceleration.si, 1.0)) * discount);

            }
            catch (ParameterException | OperationalPlanException ex)
            {
                System.out.println("Unable to obtain rho'");
                return Float.NaN;
            }
        }
    }

    /**
     * Contour plot for rho.
     * @author wjschakel
     */
    private static class ContourPlotRho extends AbstractContourPlot<Double>
    {
        /** Quantity for the EGTF regarding rho. */
        private static final Quantity<Double, double[][]> QUANTITY_RHO = new Quantity<>("rho", new Converter<double[][]>()
        {
            /** {@inheritDoc} */
            @Override
            public double[][] convert(final double[][] filteredData)
            {
                return filteredData;
            }
        });

        /** Contour data type for rho. */
        private static final ContourDataType<Double, WeightedMeanAndSum<Double, Double>> CONTOUR_DATA_TYPE_RHO =
                new ContourDataType<Double, WeightedMeanAndSum<Double, Double>>()
                {
                    /** {@inheritDoc} */
                    @Override
                    public WeightedMeanAndSum<Double, Double> identity()
                    {
                        return new WeightedMeanAndSum<>();
                    }

                    /** {@inheritDoc} */
                    @Override
                    public WeightedMeanAndSum<Double, Double> processSeries(
                            final WeightedMeanAndSum<Double, Double> intermediate, final List<TrajectoryGroup<?>> trajectories,
                            final List<Length> xFrom, final List<Length> xTo, final Time tFrom, final Time tTo)
                    {
                        for (int i = 0; i < trajectories.size(); i++)
                        {
                            TrajectoryGroup<?> trajectoryGroup = trajectories.get(i);
                            for (Trajectory<?> trajectory : trajectoryGroup.getTrajectories())
                            {
                                if (GraphUtil.considerTrajectory(trajectory, tFrom, tTo))
                                {
                                    trajectory = trajectory.subSet(xFrom.get(i), xTo.get(i), tFrom, tTo);

                                    try
                                    {
                                        float[] out = trajectory.getExtendedData(RHO);
                                        for (float f : out)
                                        {
                                            if (!Float.isNaN(f))
                                            {
                                                intermediate.add((double) f, 1.0);
                                            }
                                        }
                                    }
                                    catch (SamplingException ex)
                                    {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }
                        }
                        return intermediate;
                    }

                    /** {@inheritDoc} */
                    @Override
                    public Double finalize(final WeightedMeanAndSum<Double, Double> intermediate)
                    {
                        return intermediate.getMean();
                    }

                    /** {@inheritDoc} */
                    @Override
                    public Quantity<Double, ?> getQuantity()
                    {
                        return QUANTITY_RHO;
                    }
                };

        /**
         * Constructor. In case this plot is created live, the sampler of the sample data in the data source needs to have the
         * extended data type {@code ReferenceSpeed.INSTANCE} registered.
         * @param caption String; caption
         * @param simulator OtsSimulatorInterface; simulator
         * @param dataPool ContourDataSource; data pool
         */
        public ContourPlotRho(final String caption, final OtsSimulatorInterface simulator, final ContourDataSource dataPool)
        {
            super(caption, simulator, dataPool, createPaintScale(), 0.2, "%.2f", "rho %.2f");
        }

        /**
         * Creates a paint scale from red, via yellow to green.
         * @return ContinuousColorPaintScale; paint scale
         */
        private static BoundsPaintScale createPaintScale()
        {
            double[] boundaries = {0.0, 0.5, 1.0};
            Color[] colorValues = BoundsPaintScale.GREEN_RED;
            return new BoundsPaintScale(boundaries, colorValues);
        }

        /** {@inheritDoc} */
        @Override
        public GraphType getGraphType()
        {
            return GraphType.OTHER;
        }

        /** {@inheritDoc} */
        @Override
        protected double scale(final double si)
        {
            return si;
        }

        /** {@inheritDoc} */
        @Override
        protected double getValue(final int item, final double cellLength, final double cellSpan)
        {
            return getDataPool().get(item, getContourDataType());
        }

        /** {@inheritDoc} */
        @Override
        protected ContourDataType<Double, WeightedMeanAndSum<Double, Double>> getContourDataType()
        {
            return CONTOUR_DATA_TYPE_RHO;
        }
    }

    /**
     * Contour plot for rho proxy.
     * @author wjschakel
     */
    private static class ContourPlotRhoProxy extends AbstractContourPlot<Double>
    {
        /** Quantity for the EGTF regarding rho'. */
        private static final Quantity<Double, double[][]> QUANTITY_RHO2 = new Quantity<>("rho2", new Converter<double[][]>()
        {
            /** {@inheritDoc} */
            @Override
            public double[][] convert(final double[][] filteredData)
            {
                return filteredData;
            }
        });

        /** Contour data type for rho'. */
        private static final ContourDataType<Double, WeightedMeanAndSum<Double, Double>> CONTOUR_DATA_TYPE_RHO2 =
                new ContourDataType<Double, WeightedMeanAndSum<Double, Double>>()
                {
                    /** {@inheritDoc} */
                    @Override
                    public WeightedMeanAndSum<Double, Double> identity()
                    {
                        return new WeightedMeanAndSum<>();
                    }

                    /** {@inheritDoc} */
                    @Override
                    public WeightedMeanAndSum<Double, Double> processSeries(
                            final WeightedMeanAndSum<Double, Double> intermediate, final List<TrajectoryGroup<?>> trajectories,
                            final List<Length> xFrom, final List<Length> xTo, final Time tFrom, final Time tTo)
                    {
                        for (int i = 0; i < trajectories.size(); i++)
                        {
                            TrajectoryGroup<?> trajectoryGroup = trajectories.get(i);
                            for (Trajectory<?> trajectory : trajectoryGroup.getTrajectories())
                            {
                                if (GraphUtil.considerTrajectory(trajectory, tFrom, tTo))
                                {
                                    trajectory = trajectory.subSet(xFrom.get(i), xTo.get(i), tFrom, tTo);

                                    try
                                    {
                                        float[] out = trajectory.getExtendedData(RHO_PROXY);
                                        for (float f : out)
                                        {
                                            intermediate.add((double) f, 1.0);
                                        }
                                    }
                                    catch (SamplingException ex)
                                    {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }
                        }
                        return intermediate;
                    }

                    /** {@inheritDoc} */
                    @Override
                    public Double finalize(final WeightedMeanAndSum<Double, Double> intermediate)
                    {
                        return intermediate.getMean();
                    }

                    /** {@inheritDoc} */
                    @Override
                    public Quantity<Double, ?> getQuantity()
                    {
                        return QUANTITY_RHO2;
                    }
                };

        /**
         * Constructor. In case this plot is created live, the sampler of the sample data in the data source needs to have the
         * extended data type {@code ReferenceSpeed.INSTANCE} registered.
         * @param caption String; caption
         * @param simulator OtsSimulatorInterface; simulator
         * @param dataPool ContourDataSource; data pool
         */
        public ContourPlotRhoProxy(final String caption, final OtsSimulatorInterface simulator,
                final ContourDataSource dataPool)
        {
            super(caption, simulator, dataPool, createPaintScale(), 0.2, "%.2f", "rho' %.2f");
        }

        /**
         * Creates a paint scale from red, via yellow to green.
         * @return ContinuousColorPaintScale; paint scale
         */
        private static BoundsPaintScale createPaintScale()
        {
            double[] boundaries = {0.0, 0.5, 1.0};
            Color[] colorValues = BoundsPaintScale.GREEN_RED;
            return new BoundsPaintScale(boundaries, colorValues);
        }

        /** {@inheritDoc} */
        @Override
        public GraphType getGraphType()
        {
            return GraphType.OTHER;
        }

        /** {@inheritDoc} */
        @Override
        protected double scale(final double si)
        {
            return si;
        }

        /** {@inheritDoc} */
        @Override
        protected double getValue(final int item, final double cellLength, final double cellSpan)
        {
            return getDataPool().get(item, getContourDataType());
        }

        /** {@inheritDoc} */
        @Override
        protected ContourDataType<Double, WeightedMeanAndSum<Double, Double>> getContourDataType()
        {
            return CONTOUR_DATA_TYPE_RHO2;
        }
    }

    /**
     * Data type for distance headway in the sampler.
     * @author wjschakel
     */
    private static class ExtendedDataHeadway extends ExtendedDataLength<GtuDataRoad>
    {
        /**
         * Constructor.
         */
        public ExtendedDataHeadway()
        {
            super("s", "distance headway to leader");
        }

        /** {@inheritDoc} */
        @Override
        public FloatLength getValue(final GtuDataRoad gtu)
        {
            try
            {
                PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders = gtu.getGtu().getTacticalPlanner().getPerception()
                        .getPerceptionCategory(NeighborsPerception.class).getLeaders(RelativeLane.CURRENT);
                if (leaders.isEmpty())
                {
                    return FloatLength.NaN;
                }
                UnderlyingDistance<LaneBasedGtu> leader = leaders.underlyingWithDistance().next();
                return FloatLength.instantiateSI((float) leader.getDistance().si);
            }
            catch (OperationalPlanException e)
            {
                System.out.println("Unable to obtain s'");
                return FloatLength.NaN;
            }
        }
    }

    /**
     * Data type for speed of leader in the sampler.
     * @author wjschakel
     */
    private static class ExtendedDataSpeedLeader extends ExtendedDataSpeed<GtuDataRoad>
    {
        /**
         * Constructor.
         */
        public ExtendedDataSpeedLeader()
        {
            super("vLead", "speed of the leader");
        }

        /** {@inheritDoc} */
        @Override
        public FloatSpeed getValue(final GtuDataRoad gtu)
        {
            try
            {
                PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders = gtu.getGtu().getTacticalPlanner().getPerception()
                        .getPerceptionCategory(NeighborsPerception.class).getLeaders(RelativeLane.CURRENT);
                if (leaders.isEmpty())
                {
                    return FloatSpeed.NaN;
                }
                LaneBasedGtu leader = leaders.underlying().next();
                return FloatSpeed.instantiateSI((float) leader.getSpeed().si);
            }
            catch (OperationalPlanException e)
            {
                System.out.println("Unable to obtain v of leader'");
                return FloatSpeed.NaN;
            }
        }
    }

    /**
     * Data type for desired speed of leader in the sampler.
     * @author wjschakel
     */
    private static class ExtendedDataDesiredSpeedLeader extends ExtendedDataSpeed<GtuDataRoad>
    {
        /**
         * Constructor.
         */
        public ExtendedDataDesiredSpeedLeader()
        {
            super("v0lead", "desired speed of the leader");
        }

        /** {@inheritDoc} */
        @Override
        public FloatSpeed getValue(final GtuDataRoad gtu)
        {
            try
            {
                PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders = gtu.getGtu().getTacticalPlanner().getPerception()
                        .getPerceptionCategory(NeighborsPerception.class).getLeaders(RelativeLane.CURRENT);
                if (leaders.isEmpty())
                {
                    return FloatSpeed.NaN;
                }
                LaneBasedGtu leader = leaders.underlying().next();
                double v0 = Math.min(leader.getMaximumSpeed().si, leader.getParameters().getParameter(ParameterTypes.FSPEED)
                        * leader.getReferencePosition().getLane().getSpeedLimit(leader.getType()).si);
                return FloatSpeed.instantiateSI((float) v0);
            }
            catch (OperationalPlanException | ParameterException | NetworkException | GtuException e)
            {
                System.out.println("Unable to obtain v0 of leader'");
                return FloatSpeed.NaN;
            }
        }
    }

}
