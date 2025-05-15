package org.opentrafficsim.i4driving.tactical;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.exceptions.Throw;
import org.djutils.exceptions.Try;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.i4driving.messages.Commands;
import org.opentrafficsim.i4driving.messages.Commands.Command;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;

/**
 * This class is responsible for handling the commands that should be given to a GTU. One handler should be generated per GTU
 * that should receive commands.
 * @author wjschakel
 */
public class CommandsHandler extends ScenarioGtuSpawner
{

    /** Simulator. */
    private final OtsSimulatorInterface simulator;

    /**
     * Constructor using commands.
     * @param network network.
     * @param commands commands for a specific GTU.
     * @param strategicalFactory strategical planner, may be {@code null} if no generation info is provided in the
     *            {@code Commands}.
     */
    public CommandsHandler(final RoadNetwork network, final Commands commands,
            final LaneBasedStrategicalRoutePlannerFactory strategicalFactory)
    {
        super(network, commands.getGtuId(), commands.getGenerationInfo(), strategicalFactory);
        this.simulator = network.getSimulator();
        if (commands.getGenerationInfo() != null)
        {
            for (Command command : commands.getCommands())
            {
                Throw.when(command.time().lt(commands.getGenerationInfo().getTime()), IllegalArgumentException.class,
                        "Command scheduled before GTU %s is generated.", commands.getGtuId());
            }
        }
        for (Command command : commands.getCommands())
        {
            scheduleCommand(command);
        }
    }

    /**
     * Schedules the command. If the time is in the past or now (except at time=0), the command is executed immediately.
     * @param command command
     */
    public void scheduleCommand(final Command command)
    {
        if (command.time().le(this.simulator.getSimulatorAbsTime()) && !command.time().eq0())
        {
            executeCommand(command);
        }
        else
        {
            this.simulator.scheduleEventAbsTime(command.time(), this, "executeCommand", new Object[] {command});
        }
    }

    /**
     * Executes a command immediately.
     * @param command command
     */
    public void executeCommand(final Command command)
    {
        switch (command.type())
        {
            case SET_PARAMETER:
                String parameter =
                        Try.assign(() -> command.getData("parameter"), "Field 'parameter' not found for setParameter command.");
                String value = Try.assign(() -> command.getData("value"), "Field 'value' not found for setParameter command.");
                Try.execute(() -> getTacticalPlanner(getGtu()).setParameter(parameter, value),
                        "Parameter value %s for parameter %s is not valid.", value, parameter);
                break;
            case SET_DESIRED_SPEED:
                Speed speed = Speed.valueOf(
                        Try.assign(() -> command.getData("speed"), "Field 'speed' not found for setDesiredSpeed command."));
                getTacticalPlanner(getGtu()).setDesiredSpeed(speed);
                break;
            case RESET_DESIRED_SPEED:
                getTacticalPlanner(getGtu()).resetDesiredSpeed();
                break;
            case SET_ACCELERATION:
                Acceleration acceleration = Acceleration.valueOf(Try.assign(() -> command.getData("acceleration"),
                        "Field 'acceleration' not found for setAcceleration command."));
                getTacticalPlanner(getGtu()).setAcceleration(acceleration);
                break;
            case RESET_ACCELERATION:
                getTacticalPlanner(getGtu()).resetAcceleration();
                break;
            case DISABLE_LANE_CHANGES:
                getTacticalPlanner(getGtu()).disableLaneChanges();
                break;
            case ENABLE_LANE_CHANGES:
                getTacticalPlanner(getGtu()).enableLaneChanges();
                break;
            case CHANGE_LANE:
                LateralDirectionality laneChangeDirection = LateralDirectionality.valueOf(
                        Try.assign(() -> command.getData("direction"), "Field 'direction' not found for changeLane command."));
                getTacticalPlanner(getGtu()).changeLane(laneChangeDirection);
                break;
            case SET_INDICATOR:
                LateralDirectionality indicator = LateralDirectionality.valueOf(Try.assign(() -> command.getData("direction"),
                        "Field 'direction' not found for setIndicator command."));
                Duration duration =
                        Duration.valueOf(Try.assign(() -> command.getData("duration"), "Field 'duration' not found."));
                getTacticalPlanner(getGtu()).setIndicator(indicator, duration);
                break;
            default:
                throw new RuntimeException("Unknown command type " + command.type());
        }
    }

    /**
     * Returns the scenario based tactical planner of the GTU.
     * @param gtu GTU
     * @return scenario based tactical planner of the GTU
     */
    private ScenarioTacticalPlanner getTacticalPlanner(final Gtu gtu)
    {
        return (ScenarioTacticalPlanner) gtu.getTacticalPlanner();
    }

    @Override
    public String toString()
    {
        return "CommandsHandler [gtuId=" + getGtuId() + "]";
    }

}
