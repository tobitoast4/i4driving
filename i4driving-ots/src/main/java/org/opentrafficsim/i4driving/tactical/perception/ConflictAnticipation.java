package org.opentrafficsim.i4driving.tactical.perception;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Anticipation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborTriplet;

/**
 * Anticipation for vehicles around conflicts, i.e. the ego traveled distance is 0.0.
 * @author wjschakel
 */
public class ConflictAnticipation implements Anticipation
{

    /** {@inheritDoc} */
    @Override
    public NeighborTriplet anticipate(final NeighborTriplet neighborTriplet, final Duration duration,
            final Length traveledDistance, final boolean downstream)
    {
        return new NeighborTriplet(neighborTriplet.headway().plus(neighborTriplet.speed().times(duration)),
                neighborTriplet.speed(), neighborTriplet.acceleration());
    }

    /** {@inheritDoc} */
    @Override
    public Length egoAnticipation(final Speed speed, final Acceleration acceleration, final Duration duration)
    {
        return Length.ZERO;
    }

}