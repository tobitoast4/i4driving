package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Set;
import java.util.function.Function;

import org.opentrafficsim.i4driving.Stateless;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;

/**
 * Task demand to follow curvature. Not yet supported.
 * @author wjschakel
 */
@Stateless
public class ChannelTaskCurve implements ChannelTask
{

    /** Default set that is returned by the supplier. */
    private static final Set<ChannelTask> SET = Set.of(new ChannelTaskCurve());

    /** Standard supplier that supplies a single instance of the curve task. */
    public static final Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return "curve (front)";
    }

    /** {@inheritDoc} */
    @Override
    public Object getChannel()
    {
        return FRONT;
    }

    /** {@inheritDoc} */
    @Override
    public double getDemand(final LanePerception perception)
    {
        throw new UnsupportedOperationException("Curve task demand not yet supported.");
    }

}
