package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Tailgating;

/**
 * Task demand for social pressure. This is equal to the social pressure from the follower multiplied with the socio speed
 * sensitivity.
 * @author wjschakel
 */
public class ChannelTaskSocio implements ChannelTask
{

    /** Default set that is returned by the supplier. */
    private static final Set<ChannelTask> SET = Set.of(new ChannelTaskSocio());

    /** Standard supplier that supplies a single instance of the socio task. */
    public static final Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return "socio";
    }

    /** {@inheritDoc} */
    @Override
    public Object getChannel()
    {
        return REAR;
    }

    /** {@inheritDoc} */
    @Override
    public double getDemand(final LanePerception perception)
    {
        NeighborsPerception neighbors = Try.assign(() -> perception.getPerceptionCategory(NeighborsPerception.class),
                "NeighborsPerception not present.");
        Iterator<LaneBasedGtu> followers = neighbors.getFollowers(RelativeLane.CURRENT).underlying();
        if (!followers.hasNext())
        {
            return 0.0;
        }
        try
        {
            double socio = perception.getGtu().getParameters().getParameter(LmrsParameters.SOCIO);
            return followers.next().getParameters().getParameter(Tailgating.RHO) * socio;
        }
        catch (ParameterException ex)
        {
            // follower does not provide social pressure, ignore
            return 0.0;
        }
    }

}
