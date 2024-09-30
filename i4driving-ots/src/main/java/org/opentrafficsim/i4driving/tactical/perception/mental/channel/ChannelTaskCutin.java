package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.djunits.value.vdouble.scalar.Length;
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDouble;
import org.opentrafficsim.base.parameters.ParameterTypeLength;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;

/**
 * Task demand for potential cut-in or cooperation. This is defined as the maximum value of {@code d*(1-s/x0)} where {@code d}
 * is lane change desire towards the ego lane of any leader in either the left or right adjacent lane, {@code s} is the distance
 * to the leader with lane change desire and {@code x0} is the look-ahead distance.
 * @author wjschakel
 */
public class ChannelTaskCutin implements ChannelTask
{

    /** Look-ahead distance. */
    public static final ParameterTypeLength LOOKAHEAD = ParameterTypes.LOOKAHEAD;

    /** Standard set of left and right cut-in task. */
    private final static Set<ChannelTask> SET = Set.of(new ChannelTaskCutin(true), new ChannelTaskCutin(false));

    /** Standard supplier that supplies instances for left and right cut-in task. */
    public final static Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;

    /** Whether this task instance regards the left side. */
    private final boolean left;

    /**
     * Constructor.
     * @param left whether this task instance regards the left side.
     */
    private ChannelTaskCutin(final boolean left)
    {
        this.left = left;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.left ? "cut-in (left)" : "cut-in (right)";
    }

    /** {@inheritDoc} */
    @Override
    public Object getChannel()
    {
        return this.left ? LEFT : RIGHT;
    }

    /** {@inheritDoc} */
    @Override
    public double getDemand(final LanePerception perception)
    {
        NeighborsPerception neighbors = Try.assign(() -> perception.getPerceptionCategory(NeighborsPerception.class),
                "NeighborsPerception not present.");
        Iterator<UnderlyingDistance<LaneBasedGtu>> leaders =
                neighbors.getLeaders(this.left ? RelativeLane.LEFT : RelativeLane.RIGHT).underlyingWithDistance();
        ParameterTypeDouble param = this.left ? LmrsParameters.DRIGHT : LmrsParameters.DLEFT;
        Length x0 = Try.assign(() -> perception.getGtu().getParameters().getParameter(LOOKAHEAD), "Parameter x0 not present.");
        double dMax = 0.0;
        while (leaders.hasNext())
        {
            UnderlyingDistance<LaneBasedGtu> leader = leaders.next();
            double d;
            try
            {
                d = leader.getObject().getParameters().getParameter(param) * (1.0 - leader.getDistance().si / x0.si);
                dMax = Math.max(dMax, d);
            }
            catch (ParameterException ex)
            {
                // leader does not provide lane change desire, ignore
            }
        }
        return dMax;
    }

}
