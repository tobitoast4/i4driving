package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Iterator;

import org.djunits.value.vdouble.scalar.Duration;
import org.opentrafficsim.base.OtsRuntimeException;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDuration;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.i4driving.object.ActiveModeCrossing;
import org.opentrafficsim.i4driving.object.ActiveModeCrossing.ActiveModeArrival;
import org.opentrafficsim.i4driving.tactical.perception.ActiveModePerception;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;

/**
 * Task to recognize crossing active mode objects. This looks at downstream locations and always applies to the front area.
 * @author wjschakel
 */
public class ChannelTaskActiveModeCrossing implements ChannelTask
{

    /** Car-following task parameter. */
    public static final ParameterTypeDuration HEXP = CarFollowingTask.HEXP;
    
    @Override
    public String getId()
    {
        return "active_mode";
    }

    @Override
    public Object getChannel()
    {
        return FRONT;
    }

    @Override
    public double getDemand(final LanePerception perception)
    {
        // TODO this now follows the same logic as conflicts based on maximum time of ego and conflicting
        try
        {
            Iterator<UnderlyingDistance<ActiveModeCrossing>> crossings =
                    perception.getPerceptionCategory(ActiveModePerception.class).getActiveModes().underlyingWithDistance();
            if (crossings.hasNext())
            {
                UnderlyingDistance<ActiveModeCrossing> crossing = crossings.next();
                ActiveModeArrival arrival = crossing.getObject().getArrivals().first();
                Duration hActive = arrival.distance().divide(arrival.speed());
                Duration hSelf = crossing.getDistance().divide(perception.getGtu().getSpeed());
                Duration hExp = perception.getGtu().getParameters().getParameter(HEXP);
                return Math.exp(-Duration.max(hActive, hSelf).si / hExp.si);
            }
        }
        catch (OperationalPlanException | ParameterException ex)
        {
            throw new OtsRuntimeException(ex);
        }
        return 0.0;
    }

}
