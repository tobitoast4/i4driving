package org.opentrafficsim.i4driving.tactical;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.exceptions.Throw;
import org.djutils.exceptions.Try;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.i4driving.messages.Commands;
import org.opentrafficsim.i4driving.messages.Commands.Command;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.RoadNetwork;

/**
 * This class is responsible for handling the commands that should be given to a GTU. 
 * @author wjschakel
 */
public class CommandsHandler
{

    /** Network. */
    private final RoadNetwork network;

    /** Commands. */
    private Commands commands;

    /** GTU. */
    private LaneBasedGtu gtu;

    /**
     * Constructor.
     * @param network RoadNetwork; network.
     * @param commands Commands; commands for a specific GTU.
     */
    public CommandsHandler(final RoadNetwork network, final Commands commands)
    {
        this.network = network;
        this.commands = commands;
        if (commands.getGenerate() != null)
        {
            for (Command command : commands.getCommands())
            {
                Throw.when(command.getTime().lt(commands.getGenerate().getTime()), IllegalArgumentException.class,
                        "Command scheduled before GTU %s is generated.", commands.getGtuId());
            }
            network.getSimulator().scheduleEventAbsTime(commands.getGenerate().getTime(), this, "generateGtu", new Object[0]);
        }
        for (Command command : commands.getCommands())
        {
            network.getSimulator().scheduleEventAbsTime(command.getTime(), this, "executeCommand", new Object[] {command});
        }
    }

    /**
     * Generates the GTU.
     */
    @SuppressWarnings("unused") // scheduled
    private void generateGtu()
    {
        // TODO: implement, location should probably be Link, Lane, Length, not x, y, z.
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
                String value =
                        Try.assign(() -> command.getData("value"), "Field 'value' not found for setParameter command.");
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
