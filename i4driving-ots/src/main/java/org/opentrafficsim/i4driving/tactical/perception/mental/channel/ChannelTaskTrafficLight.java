package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterTypeDuration;
import org.opentrafficsim.core.gtu.perception.EgoPerception;
import org.opentrafficsim.i4driving.Stateless;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.IntersectionPerception;
import org.opentrafficsim.road.network.lane.object.trafficlight.TrafficLight;

/**
 * Task demand for traffic lights. This is defined as {@code exp(-T/h)} where {@code T} is the time headway to the traffic light
 * and {@code h} is the car-following task parameter that scales it.
 * @author wjschakel
 */
@Stateless
public class ChannelTaskTrafficLight implements ChannelTask
{

    /** Car-following task parameter. */
    public static final ParameterTypeDuration HEXP = CarFollowingTask.HEXP;

    /** Default set that is returned by the supplier. */
    private static final Set<ChannelTask> SET = Set.of(new ChannelTaskTrafficLight());

    /** Standard supplier that supplies a single instance of the traffic light task. */
    public static final Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;
    
    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return "traffic-light (front)";
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
        IntersectionPerception intersection = Try.assign(() -> perception.getPerceptionCategory(IntersectionPerception.class),
                "IntersectionPerception not present.");
        Iterator<UnderlyingDistance<TrafficLight>> trafficLights =
                intersection.getTrafficLights(RelativeLane.CURRENT).underlyingWithDistance();
        if (!trafficLights.hasNext())
        {
            return 0.0;
        }
        EgoPerception<?, ?> ego =
                Try.assign(() -> perception.getPerceptionCategory(EgoPerception.class), "EgoPerception not present.");
        Duration headway = trafficLights.next().getDistance().divide(ego.getSpeed());
        Duration h = Try.assign(() -> perception.getGtu().getParameters().getParameter(HEXP), "Parameter h_exp not present.");
        return Math.exp(-headway.si / h.si);
    }

}
