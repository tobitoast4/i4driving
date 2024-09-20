package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterTypeDuration;
import org.opentrafficsim.core.gtu.perception.EgoPerception;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;

/**
 * Task demand for car-following. This is defined as {@code exp(-T/h)} where {@code T} is the time headway to the leader and
 * {@code h} is the car-following task parameter that scales it.
 * @author wjschakel
 */
public class ChannelTaskCarFollowing implements ChannelTask
{

    /** Car-following task parameter. */
    public static final ParameterTypeDuration HEXP = CarFollowingTask.HEXP;

    /** Default set that is returned by the supplier. */
    private static final Set<ChannelTask> SET = Set.of(new ChannelTaskCarFollowing());

    /** Standard supplier that supplies a single instance of the car-following task. */
    public static final Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return "car-following (front)";
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
        Iterator<UnderlyingDistance<LaneBasedGtu>> leader = neighbors.getLeaders(RelativeLane.CURRENT).underlyingWithDistance();
        if (!leader.hasNext())
        {
            return 0.0;
        }
        EgoPerception<?, ?> ego =
                Try.assign(() -> perception.getPerceptionCategory(EgoPerception.class), "EgoPerception not present.");
        Duration headway = leader.next().getDistance().divide(ego.getSpeed());
        Duration h = Try.assign(() -> perception.getGtu().getParameters().getParameter(HEXP), "Parameter h_exp not present.");
        return Math.exp(-headway.si / h.si);
    }

}
