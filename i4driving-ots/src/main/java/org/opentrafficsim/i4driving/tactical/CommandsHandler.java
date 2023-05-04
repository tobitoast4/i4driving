package org.opentrafficsim.i4driving.tactical;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Set;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;
import org.djutils.exceptions.Try;
import org.djutils.immutablecollections.ImmutableMap.ImmutableEntry;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterType;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.gtu.GtuCharacteristics;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.core.network.LinkWeight;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.i4driving.messages.Commands;
import org.opentrafficsim.i4driving.messages.Commands.Command;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.CategoricalLanePerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.DirectInfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.DirectNeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlanner;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlus;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Cooperation;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.GapAcceptance;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Synchronization;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Tailgating;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlanner;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.gtu.strategical.RouteGenerator;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LanePosition;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * This class is responsible for handling the commands that should be given to a GTU.
 * @author wjschakel
 */
public class CommandsHandler
{

    /** Network. */
    private final RoadNetwork network;

    /** Commands. */
    private final Commands commands;

    /** Strategical factory. */
    private final LaneBasedStrategicalRoutePlannerFactory strategicalFactory;

    /** GTU. */
    private LaneBasedGtu gtu;

    /**
     * Constructor.
     * @param network RoadNetwork; network.
     * @param commands Commands; commands for a specific GTU.
     * @param strategicalFactory LaneBasedStrategicalRoutePlannerFactory; strategical planner, may be {@code null} if no
     *            generation info is provided.
     */
    public CommandsHandler(final RoadNetwork network, final Commands commands,
            final LaneBasedStrategicalRoutePlannerFactory strategicalFactory)
    {
        this.network = network;
        this.commands = commands;
        this.strategicalFactory = strategicalFactory;
        if (commands.getGenerationInfo() != null)
        {
            for (Command command : commands.getCommands())
            {
                Throw.when(command.getTime().lt(commands.getGenerationInfo().getTime()), IllegalArgumentException.class,
                        "Command scheduled before GTU %s is generated.", commands.getGtuId());
            }
            network.getSimulator().scheduleEventAbsTime(commands.getGenerationInfo().getTime(), this, "generateGtu",
                    new Object[0]);
            Throw.whenNull(strategicalFactory, "Strategical factory may not be null when generation info is provided.");
        }
        for (Command command : commands.getCommands())
        {
            network.getSimulator().scheduleEventAbsTime(command.getTime(), this, "executeCommand", new Object[] {command});
        }
    }

    /**
     * Generates the GTU.
     * @throws GtuException
     * @throws OtsGeometryException
     * @throws NetworkException
     * @throws SimRuntimeException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ParameterException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @SuppressWarnings({"unused", "unchecked"}) // scheduled
    private void generateGtu()
            throws GtuException, SimRuntimeException, NetworkException, OtsGeometryException, ClassNotFoundException,
            NoSuchFieldException, SecurityException, ParameterException, IllegalArgumentException, IllegalAccessException
    {
        // GTU type and characteristics
        GtuType gtuType = Defaults.getByName(GtuType.class, "NL." + this.commands.getGenerationInfo().getGtuType());
        StreamInterface randomStream = this.network.getSimulator().getModel().getStream("generation");
        GtuCharacteristics gtuCharacteristics = GtuType.defaultCharacteristics(gtuType, this.network, randomStream);
        LaneBasedGtu gtu = new LaneBasedGtu(this.commands.getGtuId(), gtuType, gtuCharacteristics.getLength(), gtuCharacteristics.getWidth(),
                gtuCharacteristics.getMaximumSpeed(), gtuCharacteristics.getFront(), this.network);
        gtu.setMaximumAcceleration(gtuCharacteristics.getMaximumAcceleration());
        gtu.setMaximumDeceleration(gtuCharacteristics.getMaximumDeceleration());
        gtu.setNoLaneChangeDistance(Length.instantiateSI(1.0));

        // position
        String linkId = this.commands.getGenerationInfo().getInitialPosition().getLink();
        String laneId = this.commands.getGenerationInfo().getInitialPosition().getLane();
        Length x = this.commands.getGenerationInfo().getInitialPosition().getX();
        CrossSectionLink link = (CrossSectionLink) this.network.getLink(linkId);
        Lane lane = null;
        for (Lane laneIter : link.getLanes())
        {
            if (laneIter.getId().equals(laneId))
            {
                lane = laneIter;
                break;
            }
        }
        Throw.when(lane == null, NoSuchElementException.class, "Lane %s is not present in link %s.", laneId, linkId);
        Set<LanePosition> position = Set.of(new LanePosition(lane, x));

        // parameters
        for (ImmutableEntry<String, String> paramEntry : this.commands.getGenerationInfo().getParameters().entrySet())
        {
            int dot = paramEntry.getKey().lastIndexOf(".");
            String paramClass = paramEntry.getKey().substring(0, dot);
            String paramField = paramEntry.getKey().substring(dot + 1);
            Class<?> clazz = Class.forName(paramClass);
            Field field = clazz.getDeclaredField(paramField);
            ParameterType<?> parameterType = (ParameterType<?>) field.get(null);
            if (parameterType.getClass().equals(Acceleration.class))
            {
                gtu.getParameters().setParameter((ParameterType<Acceleration>) parameterType,
                        Acceleration.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getClass().equals(Duration.class))
            {
                gtu.getParameters().setParameter((ParameterType<Duration>) parameterType,
                        Duration.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getClass().equals(Length.class))
            {
                gtu.getParameters().setParameter((ParameterType<Length>) parameterType, Length.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getClass().equals(Speed.class))
            {
                gtu.getParameters().setParameter((ParameterType<Speed>) parameterType, Speed.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getClass().equals(Time.class))
            {
                gtu.getParameters().setParameter((ParameterType<Time>) parameterType, Time.valueOf(paramEntry.getValue()));
            }
        }

        // model and initialization
        Node destination = this.network.getNode(this.commands.getGenerationInfo().getDestination());
        LaneBasedStrategicalRoutePlanner strategicalPlanner = this.strategicalFactory.create(gtu, null, null, destination);
        gtu.init(strategicalPlanner, position, this.commands.getGenerationInfo().getInitialSpeed());

    }

    /**
     * Executes a command.
     * @param command Command; command.
     */
    @SuppressWarnings("unused") // scheduled
    private void executeCommand(final Command command)
    {
        switch (command.getType())
        {
            case SET_PARAMETER:
                String parameter =
                        Try.assign(() -> command.getData("parameter"), "Field 'parameter' not found for setParameter command.");
                String value = Try.assign(() -> command.getData("value"), "Field 'value' not found for setParameter command.");
                Try.execute(() -> ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).setParameter(parameter, value),
                        "Parameter value %s for parameter %s is not valid.", value, parameter);
                break;
            case SET_DESIRED_SPEED:
                Speed speed = Speed.valueOf(
                        Try.assign(() -> command.getData("speed"), "Field 'speed' not found for setDesiredSpeed command."));
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).setDesiredSpeed(speed);
                break;
            case RESET_DESIRED_SPEED:
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).resetDesiredSpeed();
                break;
            case SET_ACCELERATION:
                Acceleration acceleration = Acceleration.valueOf(Try.assign(() -> command.getData("acceleration"),
                        "Field 'acceleration' not found for setAcceleration command."));
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).setAcceleration(acceleration);
                break;
            case RESET_ACCELERATION:
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).resetAcceleration();
                break;
            case DISABLE_LANE_CHANGES:
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).disableLaneChanges();
                break;
            case ENABLE_LANE_CHANGES:
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).enableLaneChanges();
                break;
            case CHANGE_LANE:
                LateralDirectionality laneChangeDirection = LateralDirectionality.valueOf(
                        Try.assign(() -> command.getData("direction"), "Field 'direction' not found for changeLane command."));
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).changeLane(laneChangeDirection);
                break;
            case SET_INDICATOR:
                LateralDirectionality indicator = LateralDirectionality.valueOf(Try.assign(() -> command.getData("direction"),
                        "Field 'direction' not found for setIndicator command."));
                Duration duration =
                        Duration.valueOf(Try.assign(() -> command.getData("duration"), "Field 'duration' not found."));
                ((ScenarioTacticalPlanner) getGtu().getTacticalPlanner()).setIndicator(indicator, duration);
                break;
        }
    }

    /**
     * Retrieves the GTU from the network and remembers it for later use.
     * @return LaneBasedGtu; GTU.
     */
    private LaneBasedGtu getGtu()
    {
        if (this.gtu == null)
        {
            this.gtu = (LaneBasedGtu) this.network.getGTU(this.commands.getGtuId());
        }
        Throw.when(this.gtu == null, IllegalStateException.class, "GTU %s could not be found to give command.",
                this.commands.getGtuId());
        return this.gtu;
    }

}
