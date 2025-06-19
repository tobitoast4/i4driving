package org.opentrafficsim.i4driving.tactical.perception;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDouble;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborTriplet;

/**
 * Estimation using EST_FACTOR based on task saturation. This either estimates the relative speed, or the absolute speed. For
 * absolute speed, the headway is decreased (upstream) or increased (downstream) by the movement of the perceived vehicle.
 * Additionally, the headway is adjusted by the ego-vehicle movement in the case of relative speed.
 * @author wjschakel
 */
public class SaturationEstimation implements Estimation
{

    /** Erroneous estimation factor on distance and speed or speed difference. */
    public static final ParameterTypeDouble EST_FACTOR = ChannelFuller.EST_FACTOR;

    /** Whether the relative speed, or absolute speed, is erroneously estimated. */
    private final boolean relativeSpeed;

    /**
     * Constructor.
     * @param relativeSpeed whether the relative speed, or absolute speed, is erroneously estimated.
     */
    public SaturationEstimation(final boolean relativeSpeed)
    {
        this.relativeSpeed = relativeSpeed;
    }

    /** {@inheritDoc} */
    @Override
    public NeighborTriplet estimate(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu, final Length distance,
            final boolean downstream, final Time when) throws ParameterException
    {
        double factor = perceivingGtu.getParameters().getParameter(EST_FACTOR);
        Length headway = getDelayedHeadway(perceivingGtu, perceivedGtu, distance, downstream, when).times(factor);
        Speed speed =
                getEgoSpeed(perceivingGtu).plus(getDelayedSpeedDifference(perceivingGtu, perceivedGtu, when).times(factor));
        Acceleration acceleration = perceivedGtu.getAcceleration(when);
        return new NeighborTriplet(headway, Speed.max(speed, Speed.ZERO), acceleration);
    }

    // TODO: the following should be implemented in OTS, where the perceiving object does not have to be a GTU

    /** {@inheritDoc} */
    @Override
    public Length getDelayedHeadway(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu, final Length distance,
            final boolean downstream, final Time when)
    {
        if (this.relativeSpeed)
        {
            return Estimation.super.getDelayedHeadway(perceivingGtu, perceivedGtu, distance, downstream, when);
        }
        double delta = (perceivedGtu.getOdometer().si - perceivedGtu.getOdometer(when).si);
        if (downstream)
        {
            delta = -delta; // leader was closer
        }
        return Length.instantiateSI(distance.si + delta);
    }

    /** {@inheritDoc} */
    @Override
    public Speed getEgoSpeed(final LaneBasedGtu perceivingGtu)
    {
        if (this.relativeSpeed)
        {
            return Estimation.super.getEgoSpeed(perceivingGtu);
        }
        return Speed.ZERO;
    }

    /** {@inheritDoc} */
    @Override
    public Speed getDelayedSpeedDifference(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu, final Time when)
    {
        if (this.relativeSpeed)
        {
            return Estimation.super.getDelayedSpeedDifference(perceivingGtu, perceivedGtu, when);
        }
        return perceivedGtu.getSpeed(when);
    }

}
