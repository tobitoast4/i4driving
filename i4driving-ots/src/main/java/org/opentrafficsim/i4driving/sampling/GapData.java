package org.opentrafficsim.i4driving.sampling;

import org.djunits.value.vfloat.scalar.FloatLength;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataLength;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtu;
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
        PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders;
        try
        {
            leaders = gtu.getGtu().getTacticalPlanner().getPerception().getPerceptionCategory(NeighborsPerception.class)
                    .getLeaders(RelativeLane.CURRENT);
        }
        catch (OperationalPlanException ex)
        {
            throw new RuntimeException("Exception while obtaining leaders.", ex);
        }
        if (leaders.isEmpty())
        {
            return FloatLength.POSITIVE_INFINITY;
        }
        return FloatLength.instantiateSI((float) leaders.first().getDistance().si);
    }

}
