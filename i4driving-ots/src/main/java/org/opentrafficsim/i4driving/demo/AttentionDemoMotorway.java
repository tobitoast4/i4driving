package org.opentrafficsim.i4driving.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.djunits.unit.FrequencyUnit;
import org.djunits.unit.SpeedUnit;
import org.djunits.unit.TimeUnit;
import org.djunits.value.storage.StorageType;
import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.vector.FrequencyVector;
import org.djunits.value.vdouble.vector.TimeVector;
import org.djutils.cli.CliUtil;
import org.djutils.draw.point.Point2d;
import org.opentrafficsim.animation.colorer.IncentiveColorer;
import org.opentrafficsim.animation.colorer.SplitColorer;
import org.opentrafficsim.animation.gtu.colorer.AccelerationGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.animation.gtu.colorer.IdGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SpeedGtuColorer;
import org.opentrafficsim.animation.gtu.colorer.SwitchableGtuColorer;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.LinkType;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactoryByType;
import org.opentrafficsim.core.perception.HistoryManagerDevs;
import org.opentrafficsim.draw.ColorInterpolator;
import org.opentrafficsim.draw.graphs.ContourDataSource;
import org.opentrafficsim.draw.graphs.GraphPath;
import org.opentrafficsim.draw.graphs.GraphPath.Section;
import org.opentrafficsim.i4driving.demo.AttentionDemoUrban.DataTypeAttention;
import org.opentrafficsim.i4driving.demo.plots.ContourPlotExtendedData;
import org.opentrafficsim.i4driving.demo.plots.DistributionPlotExtendedData;
import org.opentrafficsim.i4driving.sampling.TaskSaturationData;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelPerceptionFactory;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTask;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataNumber;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.generator.GeneratorPositions.LaneBias;
import org.opentrafficsim.road.gtu.generator.GeneratorPositions.LaneBiases;
import org.opentrafficsim.road.gtu.generator.characteristics.DefaultLaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionFactory;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.lane.tactical.AbstractLaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.AbstractIdmFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModelFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.Idm;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlus;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.AccelerationIncentive;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.AccelerationTrafficLights;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveCourtesy;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveKeep;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveRoute;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSocioSpeed;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSpeedWithCourtesy;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.Lmrs;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.SocioDesiredSpeed;
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
import org.opentrafficsim.road.network.lane.LaneType;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.road.network.lane.conflict.Conflict;
import org.opentrafficsim.road.network.lane.object.detector.SinkDetector;
import org.opentrafficsim.road.network.lane.object.trafficlight.TrafficLight;
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
import org.opentrafficsim.swing.graphs.SwingContourPlot;
import org.opentrafficsim.swing.graphs.SwingPlot;
import org.opentrafficsim.swing.gui.OtsSimulationApplication;
import org.opentrafficsim.swing.script.AbstractSimulationScript;
import org.opentrafficsim.trafficcontrol.FixedTimeController;
import org.opentrafficsim.trafficcontrol.FixedTimeController.SignalGroup;

import nl.tudelft.simulation.dsol.swing.gui.TablePanel;
import nl.tudelft.simulation.jstats.distributions.DistContinuous;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import picocli.CommandLine.Option;

/**
 * Demo of attention in an Motorway setting.
 * 
 * @author AliNadi,wjschakel
 */
public class AttentionDemoMotorway extends AbstractSimulationScript {

	/** Social interactions. */
	//@Option(names = "--social", description = "Enables social interactions", defaultValue = "true")
	
    /** Front attention data type. */
    private static final DataTypeAttention DATA_ATT_FRONT =
            new DataTypeAttention(ChannelTask.FRONT.toString(), (c) -> ChannelTask.FRONT.equals(c));

    /** Right attention data type. */
    private static final DataTypeAttention DATA_ATT_RIGHT =
    		new DataTypeAttention(ChannelTask.RIGHT.toString(), (c) -> ChannelTask.RIGHT.equals(c));
    
    /** Left attention data type. */
    private static final DataTypeAttention DATA_ATT_LEFT =
    		new DataTypeAttention(ChannelTask.LEFT.toString(), (c) -> ChannelTask.LEFT.equals(c));
    
    /** Rear attention data type. */
    private static final DataTypeAttention DATA_ATT_REAR =
    		new DataTypeAttention(ChannelTask.REAR.toString(), (c) -> ChannelTask.REAR.equals(c));

    /** Task saturation data type. */
    private static final TaskSaturationData DATA_SATURATION = new TaskSaturationData();

    /** Time-to-collision data type. */
    private static final TimeToCollision DATA_TTC = new TimeToCollision();
    
	private boolean social;

	//private RoadSampler sampler;

	protected AttentionDemoMotorway() {
		super("Attention Motorway", "Demo of attention in an Motorway setting.");
	}

	public static void main(String[] args) throws Exception {

		AttentionDemoMotorway demo = new AttentionDemoMotorway();
		CliUtil.execute(demo, args);
		demo.start();
	}
	@Override
	protected RoadNetwork setupSimulation(OtsSimulatorInterface sim) throws Exception {

		RoadNetwork network = new RoadNetwork("Motorway demo", sim);
		sim.getReplication().setHistoryManager(
				new HistoryManagerDevs(sim, Duration.instantiateSI(5.0), Duration.instantiateSI(10.0)));

		Point2d pointA = new Point2d(0.0, 0.0);
		Point2d pointB = new Point2d(500.0, 0.0);
		Point2d pointC = new Point2d(750.0, 0.0);
		Point2d pointD = new Point2d(1250.0, 0.0);
		Point2d pointE = new Point2d(1500.0, 0.0);
		Point2d pointF = new Point2d(2000.0, 0.0);
		Point2d pointG = new Point2d(250.0, -30.0);
		Point2d pointH = new Point2d(1750.0, -30.0);

		Node nodeA = new Node(network, "A", pointA);
		Node nodeB = new Node(network, "B", pointB);
		Node nodeC = new Node(network, "C", pointC);
		Node nodeD = new Node(network, "D", pointD);
		Node nodeE = new Node(network, "E", pointE);
		Node nodeF = new Node(network, "F", pointF);
		Node nodeG = new Node(network, "G", pointG);
		Node nodeH = new Node(network, "H", pointH);

		// Links
		LinkType linkType = DefaultsNl.FREEWAY;

		LaneKeepingPolicy policy = LaneKeepingPolicy.KEEPRIGHT;
		GtuType gtuType = DefaultsNl.VEHICLE;
		Length laneWidth = Length.instantiateSI(3.5);
		LaneType laneType = DefaultsRoadNl.FREEWAY;
		Speed speedLimit = new Speed(100.0, SpeedUnit.KM_PER_HOUR);

		new LaneFactory(network, nodeA, nodeB, linkType, sim, policy, gtuType)
				.leftToRight(2.0, laneWidth, laneType, speedLimit).addLanes(Type.DASHED);

		new LaneFactory(network, nodeG, nodeB, linkType, sim, policy, gtuType)
				.leftToRight(0.0, laneWidth, laneType, speedLimit).addLanes();

		new LaneFactory(network, nodeB, nodeC, linkType, sim, policy, gtuType)
				.leftToRight(2.0, laneWidth, laneType, speedLimit).addLanes(Type.DASHED, Type.BLOCK);
		new LaneFactory(network, nodeC, nodeD, linkType, sim, policy, gtuType)
				.leftToRight(2.0, laneWidth, laneType, speedLimit).addLanes(Type.DASHED);
		new LaneFactory(network, nodeD, nodeE, linkType, sim, policy, gtuType)
				.leftToRight(2.0, laneWidth, laneType, speedLimit).addLanes(Type.DASHED, Type.BLOCK);
		List<Lane> lanesEF = new LaneFactory(network, nodeE, nodeF, linkType, sim, policy, gtuType)
				.leftToRight(2.0, laneWidth, laneType, speedLimit).addLanes(Type.DASHED).getLanes();
		List<Lane> lanesEH = new LaneFactory(network, nodeE, nodeH, linkType, sim, policy, gtuType)
				.leftToRight(0.0, laneWidth, laneType, speedLimit).addLanes().getLanes();

		List<Node> origins = new ArrayList<>();
		origins.add(nodeA);
		origins.add(nodeG);
		List<Node> destinations = new ArrayList<>();
		destinations.add(nodeF);
		destinations.add(nodeH);
		Categorization categorization = new Categorization("Per GTU Type", GtuType.class);
		TimeVector timeVector = new TimeVector(new double[] { 0.0, 20.0, 60.0 }, TimeUnit.BASE_MINUTE,
				StorageType.DENSE);
		OdMatrix od = new OdMatrix("OD", origins, destinations, categorization, timeVector, Interpolation.LINEAR);

		double truckFraction = 0.05;
		Category carCategory = new Category(categorization, DefaultsNl.CAR);
		Category truckCategory = new Category(categorization, DefaultsNl.TRUCK);

		FrequencyVector demandAH = new FrequencyVector(new double[] { 100.0, 500.0, 100.0 }, FrequencyUnit.PER_HOUR,
				StorageType.DENSE);
		od.putDemandVector(nodeA, nodeH, carCategory, demandAH, 1.0 - truckFraction);
		od.putDemandVector(nodeA, nodeH, truckCategory, demandAH, truckFraction);
		FrequencyVector demandGF = new FrequencyVector(new double[] { 100.0, 500.0, 100.0 }, FrequencyUnit.PER_HOUR,
				StorageType.DENSE);
		od.putDemandVector(nodeG, nodeF, carCategory, demandGF, 1.0 - truckFraction);
		od.putDemandVector(nodeG, nodeF, truckCategory, demandGF, truckFraction);
		FrequencyVector demandGH = new FrequencyVector(new double[] { 25.0, 125.0, 25.0 }, FrequencyUnit.PER_HOUR,
				StorageType.DENSE);
		od.putDemandVector(nodeG, nodeH, carCategory, demandGH, 1.0 - truckFraction);
		od.putDemandVector(nodeG, nodeH, truckCategory, demandGH, truckFraction);

		GtuType.registerTemplateSupplier(DefaultsNl.CAR, Defaults.NL);
		GtuType.registerTemplateSupplier(DefaultsNl.TRUCK, Defaults.NL);

		OdOptions odOptions = new OdOptions();
		StreamInterface stream = sim.getModel().getStream("generation");

		Set<MandatoryIncentive> mandatory = new LinkedHashSet<>();
		mandatory.add(new IncentiveRoute());
		Set<VoluntaryIncentive> voluntary = new LinkedHashSet<>();
		voluntary.add(new IncentiveSpeedWithCourtesy());
		voluntary.add(new IncentiveKeep());

		if (this.social) {
			voluntary.add(new IncentiveCourtesy());
			voluntary.add(new IncentiveSocioSpeed());
		}

		Set<AccelerationIncentive> acceleration = new LinkedHashSet<>();
		acceleration.add(new AccelerationTrafficLights());

		//Tailgating tailGating = this.social ? Tailgating.PRESSURE : Tailgating.NONE;

		PerceptionFactory perceptionFactory = new ChannelPerceptionFactory();
		// PerceptionFactory perceptionFactory = new DefaultLmrsPerceptionFactory();

		CarFollowingModelFactory<IdmPlus> cfFactory = new AbstractIdmFactory<>(
				new IdmPlus(Idm.HEADWAY, new SocioDesiredSpeed(Idm.DESIRED_SPEED)), stream);

		// LmrsFactory lmrsFactory = new LmrsFactory(cfFactory, perceptionFactory,
		// Synchronization.PASSIVE,
		// Cooperation.PASSIVE, GapAcceptance.INFORMED, tailGating, mandatory,
		// voluntary, acceleration);
		AbstractLaneBasedTacticalPlannerFactory<Lmrs> lmrsFactory = new AbstractLaneBasedTacticalPlannerFactory<>(
				cfFactory, perceptionFactory) {
			/** {@inheritDoc} */
			@Override
			public Lmrs create(final LaneBasedGtu gtu) throws GtuException {
				Lmrs lmrs = new Lmrs(nextCarFollowingModel(gtu), gtu, getPerceptionFactory().generatePerception(gtu),
						Synchronization.ALIGN_GAP, Cooperation.PASSIVE, GapAcceptance.INFORMED, Tailgating.PRESSURE);
				lmrs.addMandatoryIncentive(new IncentiveRoute());
				lmrs.addAccelerationIncentive(new AccelerationTrafficLights());
				return lmrs;
			}

			/** {@inheritDoc} */
			@Override
			public Parameters getParameters() throws ParameterException {
				ParameterSet parameters = new ParameterSet();
				parameters.setDefaultParameters(LmrsUtil.class);
				parameters.setDefaultParameters(LmrsParameters.class);
				parameters.setDefaultParameters(TrafficLightUtil.class);
				getCarFollowingParameters().setAllIn(parameters);
				getPerceptionFactory().getParameters().setAllIn(parameters);
				parameters.setDefaultParameter(ParameterTypes.VCONG);
				parameters.setDefaultParameter(ParameterTypes.T0);
				parameters.setDefaultParameter(ParameterTypes.LCDUR);
				return parameters;
			}

		};

		ParameterFactoryByType parameterFactory = new ParameterFactoryByType();
		parameterFactory.addParameter(DefaultsNl.TRUCK, ParameterTypes.A, Acceleration.instantiateSI(0.8));
		parameterFactory.addParameter(DefaultsNl.CAR, LmrsParameters.VGAIN, new Speed(35.0, SpeedUnit.KM_PER_HOUR));
		double fractionUnder = 0.8;
		double error = 0.4;
		parameterFactory.addParameter(Estimation.OVER_EST, new DistContinuous(stream) {
			/** */
			private static final long serialVersionUID = 20240930L;

			/** {@inheritDoc} */
			@Override
			public double getProbabilityDensity(final double x) {
				return x == -error ? fractionUnder : (x == error ? error - fractionUnder : error);
			}

			/** {@inheritDoc} */
			@Override
			public double draw() {
				return getStream().nextDouble() <= fractionUnder ? -error : error;
			}
		});
		LaneBasedStrategicalRoutePlannerFactory stratFactory = new LaneBasedStrategicalRoutePlannerFactory(lmrsFactory,
				parameterFactory);

		odOptions.set(OdOptions.GTU_TYPE,
				new DefaultLaneBasedGtuCharacteristicsGeneratorOd.Factory(stratFactory).create());
		odOptions.set(OdOptions.LANE_BIAS, new LaneBiases().addBias(DefaultsNl.CAR, LaneBias.WEAK_LEFT)
				.addBias(DefaultsNl.TRUCK, LaneBias.TRUCK_RIGHT));

		OdApplier.applyOd(network, od, odOptions, DefaultsRoadNl.ROAD_USERS);

		OdApplier.applyOd(network, od, odOptions, DefaultsRoadNl.ROAD_USERS);

		for (Lane lane : lanesEF) {
			new SinkDetector(lane, lane.getLength().minus(Length.instantiateSI(50.0)), sim, DefaultsRoadNl.ROAD_USERS);
		}
		for (Lane lane : lanesEH) {
			new SinkDetector(lane, lane.getLength().minus(Length.instantiateSI(50.0)), sim, DefaultsRoadNl.ROAD_USERS);
		}

		new TrafficLight("Traffic light", lanesEH.get(0), lanesEH.get(0).getLength().minus(Length.instantiateSI(60.0)),
				sim);

		SignalGroup signalGroup = new SignalGroup("Group", Set.of("EH.Lane 1.Traffic light"), Duration.ZERO,
				Duration.instantiateSI(22.0), Duration.instantiateSI(4.0));
		new FixedTimeController("Controller", sim, network, Duration.instantiateSI(60.0), Duration.ZERO,
				Set.of(signalGroup));

		GtuColorer[] colorers = new GtuColorer[] { new IdGtuColorer(),
				new SpeedGtuColorer(new Speed(150.0, SpeedUnit.KM_PER_HOUR)),
				new AccelerationGtuColorer(Acceleration.instantiateSI(-6.0), Acceleration.instantiateSI(2.0)),
				new SplitColorer(), new IncentiveColorer(IncentiveRoute.class),
				new IncentiveColorer(IncentiveSocioSpeed.class), new TaskSaturationChannelColorer() };

		setGtuColorer(new SwitchableGtuColorer(0, colorers));

		return network;
	}
	
	
	
	

	@Override
    protected void addTabs(final OtsSimulatorInterface sim, final OtsSimulationApplication<?> animation)
    {

        RoadSampler sampler = new RoadSampler(Set.of(DATA_ATT_FRONT, DATA_ATT_RIGHT,DATA_ATT_LEFT,DATA_ATT_REAR, DATA_SATURATION, DATA_TTC),
                Collections.emptySet(), getNetwork(), Frequency.instantiateSI(2.0));
        addPlots("Motorway", List.of("A", "B", "C", "D", "E", "F"), 0, sim, animation, sampler);
        //addPlots("Motorway-to-Offramp", List.of("A", "B", "C", "D", "E", "H"), 0, sim, animation, sampler);
        //addPlots("Onramp-to-Motorway", List.of("G", "B", "C", "D","E","F"), 0, sim, animation, sampler);
        //addPlots("Onramp-to-Offramp", List.of("G", "B", "C", "D","E","H"), 0, sim, animation, sampler);

    }
	

    /**
     * Adds plots for a single path, defined by a list of nodes, and lane number on the links.
     * @param tabName tab name
     * @param nodes list of nodes
     * @param laneNum lane number on link
     * @param sim simulator
     * @param animation animation
     * @param sampler sampler
     */
    private void addPlots(final String tabName, final List<String> nodes, final int laneNum, final OtsSimulatorInterface sim,
            final OtsSimulationApplication<?> animation, final RoadSampler sampler)
    {
        GraphPath<LaneDataRoad> graphPath = new GraphPath<>(tabName, getSections(nodes, laneNum));
        GraphPath.initRecording(sampler, graphPath);
        TablePanel charts = new TablePanel(3, 2);
        ContourDataSource source = new ContourDataSource(sampler.getSamplerData(), graphPath);
        ContourPlotExtendedData front =
                new ContourPlotExtendedData("Attention to front", sim, source, DATA_ATT_FRONT, 0.0, 1.0, 0.2);
        charts.setCell(new SwingContourPlot(front).getContentPane(), 0, 0);
        ContourPlotExtendedData Right =
                new ContourPlotExtendedData("Attention to Right", sim, source, DATA_ATT_RIGHT, 0.0, 1.0, 0.2);
        charts.setCell(new SwingContourPlot(Right).getContentPane(), 0, 1);
        ContourPlotExtendedData Left =
                new ContourPlotExtendedData("Attention to Left", sim, source, DATA_ATT_LEFT, 0.0, 1.0, 0.2);
        charts.setCell(new SwingContourPlot(Left).getContentPane(), 1, 0);
        ContourPlotExtendedData Rear =
                new ContourPlotExtendedData("Attention to Rear", sim, source, DATA_ATT_REAR, 0.0, 1.0, 0.2);
        charts.setCell(new SwingContourPlot(Rear).getContentPane(), 1, 1);
        ContourPlotExtendedData saturation =
                new ContourPlotExtendedData("Task saturation", sim, source, DATA_SATURATION, 0.0, 3.0, 0.5);
        charts.setCell(new SwingContourPlot(saturation).getContentPane(), 2, 0);
        DistributionPlotExtendedData ttc = new DistributionPlotExtendedData(sampler.getSamplerData(), graphPath, DATA_TTC,
                "Time-to-collision", "Time-to-collision [s]", sim, 0.0, 0.5, 8.0);
        charts.setCell(new SwingPlot(ttc).getContentPane(), 2, 1);
        animation.getAnimationPanel().getTabbedPane().addTab(animation.getAnimationPanel().getTabbedPane().getTabCount(),
                tabName, charts);
    }

    /**
     * Get list of graph path sections.
     * @param nodes link names
     * @param laneNum lane number
     * @return list of graph path sections
     */
    private List<Section<LaneDataRoad>> getSections(final List<String> nodes, final int laneNum)
    {
        List<Section<LaneDataRoad>> out = new ArrayList<>();
        for (int i = 0; i < nodes.size() - 1; i++)
        {
            String linkId = nodes.get(i) + "_" + nodes.get(i + 1);
            Lane lane = ((CrossSectionLink) getNetwork().getLink(linkId)).getLanes().get(laneNum);
            Speed speedLimit = Speed.ZERO;
            try
            {
                speedLimit = lane.getLowestSpeedLimit();
            }
            catch (NetworkException ex)
            {
                //
            }
            out.add(new Section<>(lane.getLength(), speedLimit, List.of(new LaneDataRoad(lane))));
        }
        return out;
    }

    /**
     * Extended data type for attention.
     */
    public static class DataTypeAttention extends ExtendedDataNumber<GtuDataRoad>
    {
        /** Channel predicate. */
        private final Predicate<Object> predicate;

        /**
         * Constructor.
         * @param id id
         * @param predicate channel predicate
         */
        DataTypeAttention(final String id, final Predicate<Object> predicate)
        {
            super(id, "attention " + id);
            this.predicate = predicate;
        }

        @Override
        public Float getValue(final GtuDataRoad gtu)
        {
            if (gtu.getGtu().getStrategicalPlanner() != null)
            {
                ChannelFuller fuller = (ChannelFuller) gtu.getGtu().getTacticalPlanner().getPerception().getMental();
                float result = 0.0f;
                for (Object channel : fuller.getChannels())
                {
                    if (this.predicate.test(channel))
                    {
                        result += (float) fuller.getAttention(channel);
                    }
                }
                return result;
            }
            return Float.NaN;
        }
    }


	public static class TaskSaturationChannelColorer implements GtuColorer {

		/** Full. */
		static final Color MAX = Color.RED;

		/** Medium. */
		static final Color MID = Color.YELLOW;

		/** Zero. */
		static final Color SUBCRIT = Color.GREEN;

		/** Not available. */
		static final Color NA = Color.WHITE;

		/** Legend. */
		static final List<LegendEntry> LEGEND;

		static {
			LEGEND = new ArrayList<>();
			LEGEND.add(new LegendEntry(SUBCRIT, "sub-critical", "sub-critical task saturation"));
			LEGEND.add(new LegendEntry(MID, "1.5", "1.5 task saturation"));
			LEGEND.add(new LegendEntry(MAX, "3.0", "3.0 or larger"));
			LEGEND.add(new LegendEntry(NA, "N/A", "N/A"));
		}

		/** {@inheritDoc} */
		@Override
		public Color getColor(final Gtu gtu) {
			Double ts = gtu.getParameters().getParameterOrNull(Fuller.TS);
			if (ts == null) {
				return NA;
			}
			if (ts <= 1.0) {
				return SUBCRIT;
			} else if (ts > 3.0) {
				return MAX;
			} else if (ts < 1.5) {
				return ColorInterpolator.interpolateColor(SUBCRIT, MID, (ts - 1.0) / 0.5);
			}
			return ColorInterpolator.interpolateColor(MID, MAX, (ts - 1.5) / 1.5);
		}

		/** {@inheritDoc} */
		@Override
		public List<LegendEntry> getLegend() {
			return LEGEND;
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return "Task saturation";
		}

	}

}
