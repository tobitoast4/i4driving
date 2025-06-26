package org.opentrafficsim.i4driving.demo;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.perception.EgoPerception;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ConflictUtilTmp;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ConflictUtilTmp.ConflictPlans;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.FilteredIterable;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.IntersectionPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayConflict;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtu;
import org.opentrafficsim.road.gtu.lane.plan.operational.SimpleOperationalPlan;
import org.opentrafficsim.road.gtu.lane.tactical.Blockable;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.AccelerationIncentive;
import org.opentrafficsim.road.network.speed.SpeedLimitInfo;

/**
 * Same as AccelerationConflicts, except uses ConflictUtilTmp. This has a different evaluation of sufficient downstream space to
 * pass a conflict.
 * @author wjschakel
 */
public class AccelerationConflictsTmp implements AccelerationIncentive, Blockable
{

    /** Set of yield plans at conflicts with priority. Remembering for static model. */
    private final ConflictPlans yieldPlans = new ConflictPlans();

    /** {@inheritDoc} */
    @Override
    public final void accelerate(final SimpleOperationalPlan simplePlan, final RelativeLane lane, final Length mergeDistance,
            final LaneBasedGtu gtu, final LanePerception perception, final CarFollowingModel carFollowingModel,
            final Speed speed, final Parameters params, final SpeedLimitInfo speedLimitInfo)
            throws OperationalPlanException, ParameterException, GtuException
    {
        EgoPerception<?, ?> ego = perception.getPerceptionCategory(EgoPerception.class);
        Acceleration acceleration = ego.getAcceleration();
        Length length = ego.getLength();
        Length width = ego.getWidth();
        Iterable<HeadwayConflict> conflicts = perception.getPerceptionCategory(IntersectionPerception.class).getConflicts(lane);
        PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders =
                perception.getPerceptionCategory(NeighborsPerception.class).getLeaders(lane);
        if (!lane.isCurrent())
        {
            conflicts = new FilteredIterable<>(conflicts, (conflict) ->
            {
                return conflict.getDistance().gt(mergeDistance);
            });
        }
        conflicts = onRoute(conflicts, gtu);
        Acceleration a = ConflictUtilTmp.approachConflicts(params, conflicts, leaders, carFollowingModel, length, width, speed,
                acceleration, speedLimitInfo, this.yieldPlans, gtu, lane);
        simplePlan.minimizeAcceleration(a);
        if (this.yieldPlans.getIndicatorIntent().isLeft())
        {
            simplePlan.setIndicatorIntentLeft(this.yieldPlans.getIndicatorObjectDistance());
        }
        else if (this.yieldPlans.getIndicatorIntent().isRight())
        {
            simplePlan.setIndicatorIntentRight(this.yieldPlans.getIndicatorObjectDistance());
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBlocking()
    {
        return this.yieldPlans.isBlocking();
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return "AccelerationConflicts";
    }

}
