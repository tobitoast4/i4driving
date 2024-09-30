package org.opentrafficsim.i4driving.sampling;

import org.opentrafficsim.kpi.sampling.data.ExtendedDataNumber;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;

/**
 * Extended data type in sampler to record task demand of specific task. 
 * @author wjschakel
 */
public class TaskDemandData extends ExtendedDataNumber<GtuDataRoad>
{

    /** Task id. */
    private String taskId;

    /**
     * Constructor.
     * @param taskId task id
     */
    public TaskDemandData(final String taskId)
    {
        super(taskId + "_TD", "Task demand of task " + taskId);
        this.taskId = taskId;
    }

    /** {@inheritDoc} */
    @Override
    public Float getValue(final GtuDataRoad gtu)
    {
        return (float) ((Fuller) gtu.getGtu().getTacticalPlanner().getPerception().getMental()).getTaskDemand(this.taskId);
    }
    
}
