package org.opentrafficsim.i4driving.sampling;

import org.djunits.value.vfloat.scalar.FloatSpeed;
import org.djutils.immutablecollections.ImmutableList;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataSpeed;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;

/**
 * Speed difference in trajectories.
 * @author wjschakel
 */
public class SpeedDifferenceData extends ExtendedDataSpeed<GtuDataRoad>
{

    /**
     * Constructor.
     */
    public SpeedDifferenceData()
    {
        super("dv", "Speed difference with leader.");
    }

    /** {@inheritDoc} */
    @Override
    public FloatSpeed getValue(final GtuDataRoad gtu)
    {
        LaneBasedGtu g = gtu.getGtu();
        try
        {
            Lane lane = g.getReferencePosition().getLane();
            ImmutableList<LaneBasedGtu> gtus = lane.getGtuList();
            int index = gtus.indexOf(g) + 1;
            if (index < gtus.size())
            {
                LaneBasedGtu leader = gtus.get(index);
                return FloatSpeed.instantiateSI((float) (g.getSpeed().si - leader.getSpeed().si));
            }
        }
        catch (GtuException ge)
        {
            throw new RuntimeException("Exception while obtaining speed difference.", ge);
        }
        return FloatSpeed.POSITIVE_INFINITY;
    }

}
