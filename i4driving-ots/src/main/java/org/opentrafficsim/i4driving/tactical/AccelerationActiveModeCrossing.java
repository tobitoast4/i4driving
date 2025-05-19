package org.opentrafficsim.i4driving.tactical;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.perception.EgoPerception;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.i4driving.object.ActiveModeCrossing.ActiveModeArrival;
import org.opentrafficsim.i4driving.tactical.perception.ActiveModePerception;
import org.opentrafficsim.i4driving.tactical.perception.ActiveModePerception.ActiveModeCrossingHeadway;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.plan.operational.SimpleOperationalPlan;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.AccelerationIncentive;
import org.opentrafficsim.road.gtu.lane.tactical.util.CarFollowingUtil;
import org.opentrafficsim.road.network.speed.SpeedLimitInfo;

/**
 * Deceleration for crossing active mode objects. Active only when ego arrival is after conflict arrival, and the resulting
 * deceleration is stronger than b0.
 * @author wjschakel
 */
public class AccelerationActiveModeCrossing implements AccelerationIncentive
{

    @Override
    public void accelerate(final SimpleOperationalPlan simplePlan, final RelativeLane lane, final Length mergeDistance,
            final LaneBasedGtu gtu, final LanePerception perception, final CarFollowingModel carFollowingModel,
            final Speed speed, final Parameters params, final SpeedLimitInfo speedLimitInfo)
            throws OperationalPlanException, ParameterException, GtuException
    {
        Acceleration stop = Acceleration.POSITIVE_INFINITY;
        Speed egoSpeed = perception.getPerceptionCategory(EgoPerception.class).getSpeed();
        for (ActiveModeCrossingHeadway crossing : perception.getPerceptionCategory(ActiveModePerception.class).getActiveModes())
        {
            Duration atEgo = crossing.getDistance().divide(egoSpeed);
            for (ActiveModeArrival arrival : crossing.getActiveModeArrivals())
            {
                Duration atConf = arrival.distance().divide(arrival.speed());
                if (atConf.lt(atEgo))
                {
                    stop = Acceleration.min(stop,
                            CarFollowingUtil.stop(carFollowingModel, params, egoSpeed, speedLimitInfo, crossing.getDistance()));
                }
            }
        }
        if (stop.lt(params.getParameter(ParameterTypes.B0).neg()))
        {
            simplePlan.minimizeAcceleration(stop);
        }
    }

}
