package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.opentrafficsim.base.parameters.ParameterTypeLength;
import org.opentrafficsim.base.parameters.constraint.NumericConstraint;
import org.opentrafficsim.road.gtu.lane.perception.mental.Mental;

/**
 * Interface for mental modules that implement perception channels.
 * @author wjschakel
 */
public interface ChannelMental extends Mental
{

    /** Distance discount. */
    ParameterTypeLength X0_D = new ParameterTypeLength("x0_d", "Distance discount range",
            Length.instantiateSI(126.77), NumericConstraint.POSITIVEZERO);

    /**
     * Returns the perception delay belonging to a perception channel.
     * @param obj object that is a channel key, or that is mapped to a channel key.
     * @return perception delay belonging to a perception channel.
     */
    Duration getPerceptionDelay(Object obj);

    /**
     * Returns the level of attention of a perception channel.
     * @param obj object that is a channel key, or that is mapped to a channel key.
     * @return level of attention of a perception channel.
     */
    double getAttention(Object obj);

    /**
     * Maps an object to a channel key.
     * @param obj object.
     * @param channel channel key.
     */
    void mapToChannel(Object obj, Object channel);

}
