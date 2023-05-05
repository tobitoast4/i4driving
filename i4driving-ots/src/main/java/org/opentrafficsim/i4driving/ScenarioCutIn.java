package org.opentrafficsim.i4driving;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.djunits.unit.SpeedUnit;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.cli.CliUtil;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.definitions.DefaultsNl;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.geometry.OtsLine3d;
import org.opentrafficsim.core.geometry.OtsPoint3d;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.core.network.LinkWeight;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.parameters.ParameterFactoryDefault;
import org.opentrafficsim.i4driving.messages.Commands;
import org.opentrafficsim.i4driving.messages.DefaultGsonBuilder;
import org.opentrafficsim.i4driving.tactical.CommandsHandler;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.road.definitions.DefaultsRoadNl;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.CategoricalLanePerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.AnticipationTrafficPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.DirectInfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.DirectNeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.AbstractIdm;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlus;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveKeep;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveRoute;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSpeedWithCourtesy;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Cooperation;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.GapAcceptance;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsUtil;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Synchronization;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Tailgating;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.gtu.strategical.RouteGenerator;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.factory.LaneFactory;
import org.opentrafficsim.road.network.lane.Stripe.Type;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;
import org.opentrafficsim.swing.script.AbstractSimulationScript;

import com.google.gson.Gson;

import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * Cut-in scenario with three vehicles on a freeway.
 * @author wjschakel
 */
public class ScenarioCutIn extends AbstractSimulationScript
{

    /** */
    private static final long serialVersionUID = 20230505L;

    /**
     * Constructor.
     */
    protected ScenarioCutIn()
    {
        super("Cut-in scenario", "Cut-in scenario with three vehicles on a freeway");
    }

    /**
     * Main program execution.
     * @param args String... command line arguments.
     */
    public static void main(final String... args)
    {
        Locale.setDefault(Locale.US);
        ScenarioCutIn demo = new ScenarioCutIn();
        CliUtil.execute(demo, args);
        try
        {
            demo.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
        OtsPoint3d pointA = new OtsPoint3d(0.0, 0.0, 0.0);
        OtsPoint3d pointB = new OtsPoint3d(1000.0, 0.0, 0.0);
        Node nodeA = new Node(network, "A", pointA);
        Node nodeB = new Node(network, "B", pointB);
        new LaneFactory(network, nodeA, nodeB, DefaultsNl.FREEWAY, sim, LaneKeepingPolicy.KEEPRIGHT,
                DefaultsNl.VEHICLE, new OtsLine3d(pointA, pointB))
                        .leftToRight(0.0, Length.instantiateSI(3.5), DefaultsRoadNl.FREEWAY,
                                new Speed(130, SpeedUnit.KM_PER_HOUR))
                        .addLanes(Type.DASHED).getLanes();

        // Model
        LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner> tacticalFactory =
                new LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner>()
                {
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
                        return parameters;
                    }

                    /** {@inheritDoc} */
                    @Override
                    public ScenarioTacticalPlanner create(final LaneBasedGtu gtu) throws GtuException
                    {
                        CarFollowingModel idm = new IdmPlus();
                        LanePerception lanePerception = new CategoricalLanePerception(gtu);
                        lanePerception.addPerceptionCategory(new DirectEgoPerception<>(lanePerception));
                        lanePerception.addPerceptionCategory(new AnticipationTrafficPerception(lanePerception));
                        lanePerception.addPerceptionCategory(new DirectInfrastructurePerception(lanePerception));
                        lanePerception
                                .addPerceptionCategory(new DirectNeighborsPerception(lanePerception, HeadwayGtuType.WRAP));
                        ScenarioTacticalPlanner tacticalPlanner = new ScenarioTacticalPlanner(idm, gtu, lanePerception,
                                Synchronization.PASSIVE, Cooperation.PASSIVE, GapAcceptance.INFORMED, Tailgating.NONE);
                        tacticalPlanner.addMandatoryIncentive(new IncentiveRoute());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveSpeedWithCourtesy());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveKeep());
                        return tacticalPlanner;
                    }
                };
        StreamInterface randomStream = sim.getModel().getStream("generation");
        RouteGenerator routeGenerator = RouteGenerator.getDefaultRouteSupplier(randomStream, LinkWeight.LENGTH_NO_CONNECTORS);
        LaneBasedStrategicalRoutePlannerFactory strategicalFactory =
                new LaneBasedStrategicalRoutePlannerFactory(tacticalFactory, new ParameterFactoryDefault(), routeGenerator);

        // Vehicle commands
        Gson gson = DefaultGsonBuilder.get();
        for (int i = 1; i < 4; i++)
        {
            Commands commands = gson.fromJson(Files.readString(Path.of("./src/main/resources/cutinVehicle" + i + ".json")),
                    Commands.COMMANDS);
            new CommandsHandler(network, commands, strategicalFactory);
        }
        
        return network;
    }

}
