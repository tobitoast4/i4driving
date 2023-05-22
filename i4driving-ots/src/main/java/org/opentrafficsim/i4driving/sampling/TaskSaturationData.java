package org.opentrafficsim.i4driving.sampling;

import org.opentrafficsim.kpi.sampling.data.ExtendedDataNumber;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;

/**
 * Extended data type in sampler to record task saturation. 
 * @author wjschakel
 */
public class TaskSaturationData extends ExtendedDataNumber<GtuDataRoad>
{

    /**
     * Constructor.
     */
    public TaskSaturationData()
    {
        super("TS", "Task saturation.");
    }

    /** {@inheritDoc} */
    @Override
    public Float getValue(final GtuDataRoad gtu)
    {
        Double ts = gtu.getGtu().getParameters().getParameterOrNull(Fuller.TS);
        if (ts != null)
        {
            return (float) (double) ts;
        }
        return Float.NaN;
    }
    
}
