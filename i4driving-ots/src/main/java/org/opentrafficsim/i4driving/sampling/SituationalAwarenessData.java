package org.opentrafficsim.i4driving.sampling;

import org.opentrafficsim.kpi.sampling.data.ExtendedDataNumber;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSituationalAwareness;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;

/**
 * Extended data type in sampler to record situational awareness. 
 * @author wjschakel
 */
public class SituationalAwarenessData extends ExtendedDataNumber<GtuDataRoad>
{

    /**
     * Constructor.
     */
    public SituationalAwarenessData()
    {
        super("SA", "Situational awareness.");
    }

    /** {@inheritDoc} */
    @Override
    public Float getValue(final GtuDataRoad gtu)
    {
        Double ts = gtu.getGtu().getParameters().getParameterOrNull(AdaptationSituationalAwareness.SA);
        if (ts != null)
        {
            return (float) (double) ts;
        }
        return Float.NaN;
    }
    
}
