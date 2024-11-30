package org.opentrafficsim.i4driving.opendrive.bindings;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opentrafficsim.i4driving.opendrive.generated.ETrafficRule;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;

/**
 * LaneKeepingPolicy adapter.
 * @author wjschakel
 */
public class LaneKeepingPolicyAdapter extends XmlAdapter<ETrafficRule, LaneKeepingPolicy>
{

    @Override
    public LaneKeepingPolicy unmarshal(final ETrafficRule v)
    {
        return ETrafficRule.LHT.equals(v) ? LaneKeepingPolicy.KEEPLEFT : LaneKeepingPolicy.KEEPRIGHT;
    }

    @Override
    public ETrafficRule marshal(final LaneKeepingPolicy v)
    {
        return LaneKeepingPolicy.KEEPLEFT.equals(v) ? ETrafficRule.LHT : ETrafficRule.RHT;
    }

}
