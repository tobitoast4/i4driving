package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.function.Supplier;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Anticipation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborTriplet;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtu;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtuPerceived;

/**
 * This class is highly similar to PerceivedHeadwayGtuType, but uses an external perception delay supplier, rather than the Tr
 * parameter.
 * @author wjschakel
 */
public class HeadwayGtuTypeChannel implements HeadwayGtuType
{

    /** Estimation. */
    private final Estimation estimation;

    /** Anticipation. */
    private final Anticipation anticipation;

    /** Perception delay supplier. */
    private final Supplier<Duration> perceptionDelay;

    /** Last update time. */
    private Time updateTime = null;

    /** Historical moment considered at update time. */
    private Time when;

    /** Traveled distance during reaction time at update time. */
    private Length traveledDistance;

    /**
     * Constructor.
     * @param estimation estimation
     * @param anticipation anticipation
     * @param perceptionDelay perception delay supplier.
     */
    public HeadwayGtuTypeChannel(final Estimation estimation, final Anticipation anticipation,
            final Supplier<Duration> perceptionDelay)
    {
        this.estimation = estimation;
        this.anticipation = anticipation;
        this.perceptionDelay = perceptionDelay;
    }

    /** {@inheritDoc} */
    @Override
    public HeadwayGtu createHeadwayGtu(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu, final Length distance,
            final boolean downstream) throws GtuException, ParameterException
    {
        Time now = perceivedGtu.getSimulator().getSimulatorAbsTime();
        Duration tr = this.perceptionDelay.get();
        if (this.updateTime == null || now.si > this.updateTime.si)
        {
            this.updateTime = now;
            Time whenTemp = now.minus(tr);
            if (this.when == null || whenTemp.si > this.when.si)
            {
                // never go backwards in time if the reaction time increases
                this.when = whenTemp;
            }
            this.traveledDistance = perceivingGtu.getOdometer().minus(perceivingGtu.getOdometer(this.when));
        }
        NeighborTriplet triplet = this.estimation.estimate(perceivingGtu, perceivedGtu, distance, downstream, this.when);
        triplet = this.anticipation.anticipate(triplet, tr, this.traveledDistance, downstream);
        return new HeadwayGtuPerceived(perceivedGtu, triplet.headway(), triplet.speed(), triplet.acceleration());
    }

    /** {@inheritDoc} */
    @Override
    public HeadwayGtu createDownstreamGtu(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu,
            final Length distance) throws GtuException, ParameterException
    {
        return createHeadwayGtu(perceivingGtu, perceivedGtu, distance, true);
    }

    /** {@inheritDoc} */
    @Override
    public HeadwayGtu createUpstreamGtu(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu,
            final Length distance) throws GtuException, ParameterException
    {
        return createHeadwayGtu(perceivingGtu, perceivedGtu, distance, false);
    }

    /** {@inheritDoc} */
    @Override
    public HeadwayGtu createParallelGtu(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu,
            final Length overlapFront, final Length overlap, final Length overlapRear) throws GtuException
    {
        return new HeadwayGtuPerceived(perceivedGtu, overlapFront, overlap, overlapRear, perceivedGtu.getSpeed(),
                perceivedGtu.getAcceleration());
    }

}
