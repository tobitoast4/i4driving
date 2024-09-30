package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import org.djutils.base.Identifiable;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;

public interface ChannelTask extends Identifiable
{

    /** Standard front channel. */
    final Object FRONT = "Front";
    
    /** Standard rear channel. */
    final Object REAR = "Rear";
    
    /** Standard left channel. */
    final Object LEFT = "Left";
    
    /** Standard right channel. */
    final Object RIGHT = "Right";
    
    /**
     * Return the channel this task pertains to.
     * @return channel this task pertains to.
     */
    Object getChannel();

    /**
     * Returns the level of task demand.
     * @param perception perception.
     * @return level of task demand.
     */
    double getDemand(LanePerception perception);

}
