package org.opentrafficsim.i4driving.sampling;

import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vfloat.scalar.FloatLength;
import org.djutils.immutablecollections.ImmutableList;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataLength;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;

/**
 * Gap data in trajectories.
 * @author wjschakel
 */
public class GapData extends ExtendedDataLength<GtuDataRoad>
{

    /**
     * Constructor.
     */
    public GapData()
    {
        super("gap", "Distance gap to leader.");
    }

    /** {@inheritDoc} */
    @Override
    public FloatLength getValue(final GtuDataRoad gtu)
    {
        LaneBasedGtu g = gtu.getGtu();
        try
        {
            Lane lane = g.getReferencePosition().lane();
            Length front = g.position(lane, g.getFront());
            ImmutableList<LaneBasedGtu> gtus = lane.getGtuList();
            int index = gtus.indexOf(g) + 1;
            if (index < gtus.size())
            {
                LaneBasedGtu leader = gtus.get(index);
                Length rear = leader.position(lane, leader.getRear());
                return FloatLength.instantiateSI((float) (rear.si - front.si));
            }
        }
        catch (GtuException ge)
        {
            throw new RuntimeException("Exception while obtaining gap.", ge);
        }
        return FloatLength.POSITIVE_INFINITY;
    }

}
