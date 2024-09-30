package org.opentrafficsim.i4driving.tactical;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.exceptions.Throw;
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterType;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.TurnIndicatorIntent;
import org.opentrafficsim.core.gtu.perception.EgoPerception;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlan;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.InfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.plan.operational.LaneChange;
import org.opentrafficsim.road.gtu.lane.plan.operational.LaneOperationalPlanBuilder;
import org.opentrafficsim.road.gtu.lane.plan.operational.SimpleOperationalPlan;
import org.opentrafficsim.road.gtu.lane.tactical.Blockable;
import org.opentrafficsim.road.gtu.lane.tactical.DesireBased;
import org.opentrafficsim.road.gtu.lane.tactical.Synchronizable;
import org.opentrafficsim.road.gtu.lane.tactical.following.AbstractCarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.DesiredSpeedModel;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.AbstractIncentivesTacticalPlanner;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.AccelerationConflicts;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.AccelerationIncentive;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Cooperation;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Desire;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.GapAcceptance;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Incentive;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsData;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsUtil;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Synchronization;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Tailgating;
import org.opentrafficsim.road.network.speed.SpeedLimitInfo;
import org.opentrafficsim.road.network.speed.SpeedLimitProspect;

import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;

/**
 * Tactical planner that uses the LMRS, but overrides actions based on {@code Commands} typically invoked by a
 * {@code CommandsHandler}. This class is similar to the {@code Lmrs} tactical planner.
 * @author wjschakel
 */
public class ScenarioTacticalPlanner extends AbstractIncentivesTacticalPlanner implements DesireBased, Synchronizable, Blockable
{

    /**  */
    private static final long serialVersionUID = 20230426L;

    /** Lane change status. */
    private final LaneChange laneChange;

    /** LMRS data. */
    private final LmrsData lmrsData;

    /** Overruled acceleration. */
    private Acceleration acceleration;

    /** Overruled indicator. */
    private LateralDirectionality indicator;

    /** Overruled lane change ability. */
    private boolean laneChangesEnabled = true;

    /** Desired speed model for when the model should be reset. */
    private DesiredSpeedModel desiredSpeedModel;

    /** Lane change direction. */
    private LateralDirectionality laneChangeDirection;

    /**
     * Constructor setting the car-following model.
     * @param carFollowingModel Car-following model.
     * @param gtu GTU
     * @param lanePerception perception
     * @param synchronization type of synchronization
     * @param cooperation type of cooperation
     * @param gapAcceptance gap-acceptance
     * @param tailgating tail gating
     */
    public ScenarioTacticalPlanner(final CarFollowingModel carFollowingModel, final LaneBasedGtu gtu,
            final LanePerception lanePerception, final Synchronization synchronization, final Cooperation cooperation,
            final GapAcceptance gapAcceptance, final Tailgating tailgating)
    {
        super(carFollowingModel, gtu, lanePerception);
        this.laneChange = Try.assign(() -> new LaneChange(gtu), "Parameter LCDUR is required.", GtuException.class);
        this.lmrsData = new LmrsData(synchronization, cooperation, gapAcceptance, tailgating);
    }

    /** {@inheritDoc} */
    @Override
    public final OperationalPlan generateOperationalPlan(final Time startTime, final OrientedPoint2d locationAtStartTime)
            throws OperationalPlanException, GtuException, NetworkException, ParameterException
    {
        // obtain objects to get info
        SpeedLimitProspect slp = getPerception().getPerceptionCategory(InfrastructurePerception.class)
                .getSpeedLimitProspect(RelativeLane.CURRENT);
        SpeedLimitInfo sli = slp.getSpeedLimitInfo(Length.ZERO);
        Parameters params = getGtu().getParameters();

        // LMRS
        SimpleOperationalPlan simplePlan = LmrsUtil.determinePlan(getGtu(), startTime, getCarFollowingModel(), this.laneChange,
                this.lmrsData, getPerception(), getMandatoryIncentives(), getVoluntaryIncentives());

        // Lower acceleration from additional sources, consider adjacent lane when changing lane or synchronizing
        Speed speed = getPerception().getPerceptionCategory(EgoPerception.class).getSpeed();
        RelativeLane[] lanes;
        double dLeft = params.getParameterOrNull(LmrsParameters.DLEFT);
        double dRight = params.getParameterOrNull(LmrsParameters.DRIGHT);
        double dSync = params.getParameterOrNull(LmrsParameters.DSYNC);
        if (this.laneChange.isChangingLane())
        {
            lanes = new RelativeLane[] {RelativeLane.CURRENT, this.laneChange.getSecondLane(getGtu())};
        }
        else if (dLeft >= dSync && dLeft >= dRight)
        {
            lanes = new RelativeLane[] {RelativeLane.CURRENT, RelativeLane.LEFT};
        }
        else if (dRight >= dSync)
        {
            lanes = new RelativeLane[] {RelativeLane.CURRENT, RelativeLane.RIGHT};
        }
        else
        {
            lanes = new RelativeLane[] {RelativeLane.CURRENT};
        }
        for (RelativeLane lane : lanes)
        {
            // On the current lane, consider all incentives. On adjacent lanes only consider incentives beyond the distance over
            // which a lane change is not yet possible, i.e. the merge distance.
            // TODO: consider route in incentives (only if not on current lane?)
            Length mergeDistance = lane.isCurrent() ? Length.ZERO
                    : Synchronization.getMergeDistance(getPerception(), lane.getLateralDirectionality());
            for (AccelerationIncentive incentive : getAccelerationIncentives())
            {
                incentive.accelerate(simplePlan, lane, mergeDistance, getGtu(), getPerception(), getCarFollowingModel(), speed,
                        params, sli);
            }
        }

        if (simplePlan.isLaneChange())
        {
            this.laneChange.setDesiredLaneChangeDuration(params.getParameter(ParameterTypes.LCDUR));
            // adjust lane based data in perception
        }

        // check overrules
        if (this.acceleration != null)
        {
            simplePlan.setAcceleration(acceleration);
        }
        if (!this.laneChangesEnabled)
        {
            try
            {
                Field field = SimpleOperationalPlan.class.getDeclaredField("indicatorIntent");
                field.setAccessible(true);
                field.set(simplePlan, TurnIndicatorIntent.NONE);
            }
            catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (this.indicator != null && !this.indicator.isNone())
        {
            if (this.indicator.isLeft())
            {
                simplePlan.setIndicatorIntentLeft(Length.ZERO);
            }
            else
            {
                simplePlan.setIndicatorIntentRight(Length.ZERO);
            }
        }
        if (!this.laneChangesEnabled && simplePlan.isLaneChange() && !this.laneChange.isChangingLane())
        {
            try
            {
                Field field = SimpleOperationalPlan.class.getDeclaredField("laneChangeDirection");
                field.setAccessible(true);
                field.set(simplePlan, LateralDirectionality.NONE);
            }
            catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (this.laneChangeDirection != null) // this overrules 'this.laneChangesEnabled == false'
        {
            try
            {
                Field field = SimpleOperationalPlan.class.getDeclaredField("laneChangeDirection");
                field.setAccessible(true);
                field.set(simplePlan, this.laneChangeDirection);
            }
            catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            this.laneChangeDirection = null; // trigger, not a state
            this.laneChange.setDesiredLaneChangeDuration(getGtu().getParameters().getParameter(ParameterTypes.LCDUR));
        }

        // set turn indicator
        simplePlan.setTurnIndicator(getGtu());

        // create plan
        return LaneOperationalPlanBuilder.buildPlanFromSimplePlan(getGtu(), startTime, simplePlan, this.laneChange);
    }

    /** {@inheritDoc} */
    @Override
    public final Desire getLatestDesire(final Class<? extends Incentive> incentiveClass)
    {
        return this.lmrsData.getLatestDesire(incentiveClass);
    }

    /** {@inheritDoc} */
    @Override
    public Synchronizable.State getSynchronizationState()
    {
        return this.lmrsData.getSynchronizationState();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBlocking()
    {
        for (AccelerationIncentive acc : getAccelerationIncentives())
        {
            if (acc instanceof AccelerationConflicts)
            {
                return ((AccelerationConflicts) acc).isBlocking();
            }
        }
        return false;
    }

    /**
     * Sets a fixed acceleration.
     * @param acceleration acceleration;
     */
    public void setAcceleration(final Acceleration acceleration)
    {
        this.acceleration = acceleration;
        interruptMove();
    }

    /**
     * Removes fixed acceleration.
     */
    public void resetAcceleration()
    {
        this.acceleration = null;
        interruptMove();
    }

    /**
     * Sets the indicator.
     * @param indicator indicator.
     * @param duration duration;
     */
    public void setIndicator(final LateralDirectionality indicator, final Duration duration)
    {
        this.indicator = indicator;
        getGtu().getSimulator().scheduleEventRel(duration, this, "resetIndicator", new Object[0]);
        interruptMove();
    }

    /**
     * Resets the indicator.
     */
    @SuppressWarnings("unused") // scheduled
    private void resetIndicator()
    {
        this.indicator = null;
        interruptMove();
    }

    /**
     * Disable lane changes.
     */
    public void disableLaneChanges()
    {
        this.laneChangesEnabled = false;
        interruptMove();
    }

    /**
     * Enable lane changes.
     */
    public void enableLaneChanges()
    {
        this.laneChangesEnabled = true;
        interruptMove();
    }

    /**
     * Sets the desired speed.
     * @param speed desired speed.
     */
    public void setDesiredSpeed(final Speed speed)
    {
        clearCache();
        try
        {
            Field modelField = AbstractCarFollowingModel.class.getDeclaredField("desiredSpeedModel");
            modelField.setAccessible(true);
            this.desiredSpeedModel = (DesiredSpeedModel) modelField.get(getCarFollowingModel());
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        setDesiredSpeedModel(new DesiredSpeedModel()
        {
            /** {@inheritDoc} */
            @Override
            public Speed desiredSpeed(final Parameters parameters, final SpeedLimitInfo speedInfo) throws ParameterException
            {
                return speed;
            }
        });
        interruptMove();
    }

    /**
     * Reset desired speed.
     */
    public void resetDesiredSpeed()
    {
        Throw.when(this.desiredSpeedModel == null, IllegalStateException.class,
                "Attempting to reset desired speed, but no desired speed was ever set.");
        clearCache();
        setDesiredSpeedModel(this.desiredSpeedModel);
        interruptMove();
    }

    /**
     * Clears the cache for desired speed and acceleration, so the set desired speed has effect even if a plan has been
     * calculated at the same time.
     */
    private void clearCache()
    {
        try
        {
            // clear time of cached desired speed, so a new value will be calculated
            Field speedCacheField = LaneBasedGtu.class.getDeclaredField("desiredSpeedTime");
            speedCacheField.setAccessible(true);
            speedCacheField.set(getGtu(), null);
            // clear time of cached acceleration, so a new value will be calculated
            Field accelerationCacheField = LaneBasedGtu.class.getDeclaredField("carFollowingAccelerationTime");
            accelerationCacheField.setAccessible(true);
            accelerationCacheField.set(getGtu(), null);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the desired speed model in the car-following mode using reflection.
     * @param desiredSpeedModel desired speed model.
     */
    private void setDesiredSpeedModel(final DesiredSpeedModel desiredSpeedModel)
    {
        try
        {
            Field field = AbstractCarFollowingModel.class.getDeclaredField("desiredSpeedModel");
            field.setAccessible(true);
            field.set(getCarFollowingModel(), desiredSpeedModel);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set parameter.
     * @param parameter parameter type.
     * @param value string representation of the value.
     * @throws ParameterException when the parameter value does not comply to the type.
     */
    @SuppressWarnings("unchecked")
    public void setParameter(String parameter, String value) throws ParameterException
    {
        ParameterType<?> parameterType;
        try
        {
            int dot = parameter.lastIndexOf(".");
            Class<?> clazz = Class.forName(parameter.substring(0, dot));
            Field field = clazz.getDeclaredField(parameter.substring(dot + 1, parameter.length()));
            parameterType = (ParameterType<?>) field.get(null);
        }
        catch (NoSuchFieldException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        if (parameterType.getValueClass().equals(Acceleration.class))
        {
            getGtu().getParameters().setParameter((ParameterType<Acceleration>) parameterType, Acceleration.valueOf(value));
        }
        else if (parameterType.getValueClass().equals(Duration.class))
        {
            getGtu().getParameters().setParameter((ParameterType<Duration>) parameterType, Duration.valueOf(value));
        }
        else if (parameterType.getValueClass().equals(Length.class))
        {
            getGtu().getParameters().setParameter((ParameterType<Length>) parameterType, Length.valueOf(value));
        }
        else if (parameterType.getValueClass().equals(Speed.class))
        {
            getGtu().getParameters().setParameter((ParameterType<Speed>) parameterType, Speed.valueOf(value));
        }
        else if (parameterType.getValueClass().equals(Time.class))
        {
            getGtu().getParameters().setParameter((ParameterType<Time>) parameterType, Time.valueOf(value));
        }
        else if (parameterType.getValueClass().equals(Double.class))
        {
            getGtu().getParameters().setParameter((ParameterType<Double>) parameterType, Double.valueOf(value));
        }
        else
        {
            throw new RuntimeException("Setting parameter of type " + parameterType.getValueClass() + " is not supported.");
        }
    }

    /**
     * Initiates a lane change.
     * @param direction lane change direction.
     */
    public void changeLane(final LateralDirectionality direction)
    {
        this.laneChangeDirection = direction;
        interruptMove();
    }

    /**
     * Invokes {@code interruptMove} on the GTU through reflection. This will cancel the scheduled move event, and trigger a new
     * move now.
     */
    private void interruptMove()
    {
        try
        {
            // there's a bug in interruptMove(), so need to perform its contents indirectly
            Field field = Gtu.class.getDeclaredField("nextMoveEvent");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            SimEvent<Duration> event = (SimEvent<Duration>) field.get(getGtu());
            getGtu().getSimulator().cancelEvent(event);

            Method move = Gtu.class.getDeclaredMethod("move", OrientedPoint2d.class);
            move.setAccessible(true);
            move.invoke(getGtu(), getGtu().getLocation());
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return "ScenarioTacticalPlanner [mandatoryIncentives=" + getMandatoryIncentives() + ", voluntaryIncentives="
                + getVoluntaryIncentives() + ", accelerationIncentives = " + getAccelerationIncentives() + "]";
    }

}
