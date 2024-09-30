package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import org.djunits.value.vdouble.scalar.Duration;
import org.opentrafficsim.road.gtu.lane.perception.mental.Mental;

/**
 * Interface for mental modules that implement perception channels.
 * @author wjschakel
 */
public interface ChannelMental extends Mental
{

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
