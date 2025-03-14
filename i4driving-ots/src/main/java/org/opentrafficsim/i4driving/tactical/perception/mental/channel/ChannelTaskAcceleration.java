package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterTypeLength;
import org.opentrafficsim.base.parameters.ParameterTypeSpeed;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.core.gtu.perception.EgoPerception;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.IntersectionPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;

/**
 * Task demand for acceleration from stand-still or low speeds. This mostly functions to increase attention when leaving a
 * queue. This is defined as the maximum of {@code max(0,-dv)/v0 * (1-s/x0)}, where {@code dv} is the approaching speed to a
 * leader, {@code v0} is the desired speed, {@code s} is the distance to the leader and {@code x0} is the look-ahead distance.
 */
public class ChannelTaskAcceleration implements ChannelTask
{

    /** Look-ahead distance. */
    public static final ParameterTypeLength LOOKAHEAD = ParameterTypes.LOOKAHEAD;

    /** Speed threshold below which traffic is considered congested. */
    public static final ParameterTypeSpeed VCONG = ParameterTypes.VCONG;

    /** Default set that is returned by the supplier. */
    private static final Set<ChannelTask> SET = Set.of(new ChannelTaskAcceleration());

    /** Standard supplier that supplies a single instance of the acceleration task. */
    public static final Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return "acceleration";
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
        NeighborsPerception neighbors = Try.assign(() -> perception.getPerceptionCategory(NeighborsPerception.class),
                "NeighborsPerception not present.");
        EgoPerception<?, ?> ego =
                Try.assign(() -> perception.getPerceptionCategory(EgoPerception.class), "EgoPerception not present.");
        Speed v = ego.getSpeed();
        Speed vCong = Try.assign(() -> perception.getGtu().getParameters().getParameter(VCONG), "Parameter VCONG not present");
        Length x0 = Try.assign(() -> perception.getGtu().getParameters().getParameter(LOOKAHEAD),
                "Parameter LOOKAHEAD not present.");
        Iterator<UnderlyingDistance<LaneBasedGtu>> leaders =
                neighbors.getLeaders(RelativeLane.CURRENT).underlyingWithDistance();
        /*
         * We limit this search by a first conflict. Traffic from other directions on the intersection should not let the
         * required level of attention for free acceleration flicker for each passing vehicle.
         */
        Length limit = Length.POSITIVE_INFINITY;
        try
        {
            var conflicts = perception.getPerceptionCategory(IntersectionPerception.class).getConflicts(RelativeLane.CURRENT);
            if (!conflicts.isEmpty())
            {
                limit = conflicts.first().getDistance();
            }
        }
        catch (OperationalPlanException ex)
        {
            // ignore if intersections are no perceived
        }
        double td = 0.0;
        while (leaders.hasNext())
        {
            UnderlyingDistance<LaneBasedGtu> leader = leaders.next();
            if (leader.getDistance().gt(limit))
            {
                break;
            }
            Speed vLead = leader.getObject().getSpeed();
            Length s = leader.getDistance();
            td = Math.max(td, Math.max(Math.min((vLead.si - v.si) / vCong.si, 1.0), 0.0) * (1.0 - s.si / x0.si));
        }
        return td >= 1.0 ? 0.999 : td;
    }

}
