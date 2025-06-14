package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import org.djutils.base.Identifiable;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;

/**
 * Interface for tasks that apply within a channel.
 */
public interface ChannelTask extends Identifiable
{

    /** Standard front channel. */
    Object FRONT = "Front";
    
    /** Standard rear channel. */
    Object REAR = "Rear";
    
    /** Standard left channel. */
    Object LEFT = "Left";
    
    /** Standard right channel. */
    Object RIGHT = "Right";
    
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
