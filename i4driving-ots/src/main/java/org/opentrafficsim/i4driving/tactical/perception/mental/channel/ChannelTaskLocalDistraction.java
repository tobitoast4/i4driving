package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.opentrafficsim.base.OtsRuntimeException;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.i4driving.Stateless;
import org.opentrafficsim.i4driving.object.LocalDistraction;
import org.opentrafficsim.i4driving.tactical.perception.LocalDistractionPerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;

/**
 * Task demand from local distraction.
 * @author wjschakel
 */
@Stateless
public class ChannelTaskLocalDistraction implements ChannelTask
{

    /** Channel key. */
    private final Object channel;

    /** Side of road in driving direction of distractions to include. */
    private final LateralDirectionality side;

    /** Standard set of left, right and front local distraction task. */
    private static final Set<ChannelTask> SET = Set.of(new ChannelTaskLocalDistraction(LEFT, LateralDirectionality.LEFT),
            new ChannelTaskLocalDistraction(RIGHT, LateralDirectionality.RIGHT),
            new ChannelTaskLocalDistraction(FRONT, LateralDirectionality.NONE));

    /** Standard supplier that supplies instances for left, right and front local distraction task. */
    public static final Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;

    /**
     * Constructor.
     * @param channel channel key
     * @param side side of road in driving direction of distractions to include
     */
    public ChannelTaskLocalDistraction(final Object channel, final LateralDirectionality side)
    {
        this.channel = channel;
        this.side = side;
    }

    @Override
    public String getId()
    {
        return "local_distraction";
    }

    @Override
    public Object getChannel()
    {
        return this.channel;
    }

    @Override
    public double getDemand(final LanePerception perception)
    {
        double td = 0.0;
        try
        {
            Iterator<UnderlyingDistance<LocalDistraction>> distractions = perception
                    .getPerceptionCategory(LocalDistractionPerception.class).getActiveModes().underlyingWithDistance();
            while (distractions.hasNext())
            {
                UnderlyingDistance<LocalDistraction> distraction = distractions.next();
                if (distraction.getDistance().le(distraction.getObject().getRange()) && distraction.getDistance().ge0()
                        && distraction.getObject().getSide().equals(this.side))
                {
                    td = Math.max(td, distraction.getObject().getDistractionLevel());
                }
            }
        }
        catch (OperationalPlanException | ParameterException ex)
        {
            throw new OtsRuntimeException(ex);
        }
        return td;
    }

}
