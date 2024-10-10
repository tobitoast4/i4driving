package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.CategoricalLanePerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionFactory;
import org.opentrafficsim.road.gtu.lane.perception.categories.AnticipationTrafficPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.DirectInfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Anticipation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborTriplet;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationHeadway;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSpeed;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller.BehavioralAdaptation;

/**
 * This factory produces a channel-based perception.
 * @author wjschakel
 */
public class ChannelPerceptionFactory implements PerceptionFactory
{

    /** Task suppliers. */
    private static final LinkedHashSet<Function<LanePerception, Set<ChannelTask>>> TASK_SUPPLIERS = new LinkedHashSet<>();

    static
    {
        TASK_SUPPLIERS.add(ChannelTaskAcceleration.SUPPLIER);
        TASK_SUPPLIERS.add(ChannelTaskCarFollowing.SUPPLIER);
        TASK_SUPPLIERS.add(ChannelTaskConflict.SUPPLIER);
        TASK_SUPPLIERS.add(ChannelTaskScan.SUPPLIER);
        TASK_SUPPLIERS.add(ChannelTaskSignal.SUPPLIER);
        TASK_SUPPLIERS.add(ChannelTaskSocio.SUPPLIER);
        TASK_SUPPLIERS.add(ChannelTaskTrafficLight.SUPPLIER);
    }

    /** Estimation instance. */
    private static final Estimation ESTIMATION_STATIC = new SaturationEstimation(true);

    /** Estimation instance. */
    private static final Estimation ESTIMATION_DYNAMIC = new SaturationEstimation(false);

    /** Anticipation instance for conflicts. */
    private static final Anticipation ANTICIPATION_CONFLICTS = new SaturationAnticipation();

    /** {@inheritDoc} */
    @Override
    public LanePerception generatePerception(final LaneBasedGtu gtu)
    {
        var behavioralAdapatations = new LinkedHashSet<BehavioralAdaptation>();
        behavioralAdapatations.add(new AdaptationHeadway());
        behavioralAdapatations.add(new AdaptationSpeedChannel());
        ChannelFuller mental = new ChannelFuller(TASK_SUPPLIERS, behavioralAdapatations);
        LanePerception perception = new CategoricalLanePerception(gtu, mental);
        perception.addPerceptionCategory(new DirectEgoPerception<>(perception));
        perception.addPerceptionCategory(new DirectInfrastructurePerception(perception));
        perception.addPerceptionCategory(
                new NeighborsPerceptionChannel(perception, ESTIMATION_DYNAMIC, Anticipation.CONSTANT_SPEED));
        // perception.addPerceptionCategory(new DirectNeighborsPerception(perception, HeadwayGtuType.WRAP));
        perception.addPerceptionCategory(new AnticipationTrafficPerception(perception));
        perception.addPerceptionCategory(
                new IntersectionPerceptionChannel(perception, ESTIMATION_STATIC, ANTICIPATION_CONFLICTS));
        // perception.addPerceptionCategory(new DirectIntersectionPerception(perception, HeadwayGtuType.WRAP));
        return perception;
    }

    /** {@inheritDoc} */
    @Override
    public Parameters getParameters() throws ParameterException
    {
        ParameterSet set = new ParameterSet().setDefaultParameter(ParameterTypes.LOOKAHEAD)
                .setDefaultParameter(ParameterTypes.LOOKBACKOLD).setDefaultParameter(ParameterTypes.PERCEPTION)
                .setDefaultParameter(ParameterTypes.LOOKBACK).setDefaultParameter(CarFollowingTask.HEXP)
                .setDefaultParameter(ChannelTaskScan.TDSCAN).setDefaultParameters(ChannelFuller.class)
                .setDefaultParameter(AdaptationHeadway.BETA_T).setDefaultParameter(AdaptationSpeed.BETA_V0)
                .setDefaultParameter(ChannelTaskSignal.TDSIGNAL).setDefaultParameter(Fuller.TS_MAX);
        set.setParameter(Fuller.TS_CRIT, 1.0); // required by behavioral adaptations, but ignored, i.e. 1.0
        return set;
    }

    /**
     * Estimation using TS.
     */
    private static class SaturationEstimation implements Estimation
    {

        /** Whether the perceiving object is static. */
        private final boolean staticObject;

        /**
         * Constructor.
         * @param conflict whether the perceiving object is static.
         */
        SaturationEstimation(final boolean conflict)
        {
            this.staticObject = conflict;
        }

        /** {@inheritDoc} */
        @Override
        public NeighborTriplet estimate(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu,
                final Length distance, final boolean downstream, final Time when) throws ParameterException
        {
            // When this is ported to OTS, the manner in which to obtain 'factor' should be generalized for AR and Attention
            // Matrix purposes
            double lamda = perceivingGtu.getParameters().getParameter(OVER_EST);
            double ts = Math.max(perceivingGtu.getParameters().getParameter(Fuller.TS), 1.0);
            double factor = Math.pow(ts, lamda);
            Length headway = getDelayedHeadway(perceivingGtu, perceivedGtu, distance, downstream, when).times(factor);
            Speed speed =
                    getEgoSpeed(perceivingGtu).plus(getDelayedSpeedDifference(perceivingGtu, perceivedGtu, when).times(factor));
            Acceleration acceleration = perceivedGtu.getAcceleration(when);
            return new NeighborTriplet(headway, Speed.max(speed, Speed.ZERO), acceleration);
        }

        // TODO: the following should be implemented in OTS, where the perceiving object does not have to be a GTU

        /** {@inheritDoc} */
        @Override
        public Length getDelayedHeadway(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu,
                final Length distance, final boolean downstream, final Time when)
        {
            if (!this.staticObject)
            {
                return Estimation.super.getDelayedHeadway(perceivingGtu, perceivedGtu, distance, downstream, when);
            }
            double delta = (perceivedGtu.getOdometer().si - perceivedGtu.getOdometer(when).si);
            if (downstream)
            {
                delta = -delta; // leader was closer
            }
            return Length.instantiateSI(distance.si + delta);
        }

        /** {@inheritDoc} */
        @Override
        public Speed getEgoSpeed(final LaneBasedGtu perceivingGtu)
        {
            if (!this.staticObject)
            {
                return Estimation.super.getEgoSpeed(perceivingGtu);
            }
            return Speed.ZERO;
        }

        /** {@inheritDoc} */
        @Override
        public Speed getDelayedSpeedDifference(final LaneBasedGtu perceivingGtu, final LaneBasedGtu perceivedGtu,
                final Time when)
        {
            if (!this.staticObject)
            {
                return Estimation.super.getDelayedSpeedDifference(perceivingGtu, perceivedGtu, when);
            }
            return perceivedGtu.getSpeed(when);
        }

    }

    /**
     * Anticipation for vehicles around conflicts, i.e. always from a static context.
     */
    private static final class SaturationAnticipation implements Anticipation
    {

        /** {@inheritDoc} */
        @Override
        public NeighborTriplet anticipate(final NeighborTriplet neighborTriplet, final Duration duration,
                final Length traveledDistance, final boolean downstream)
        {
            return new NeighborTriplet(neighborTriplet.headway().plus(neighborTriplet.speed().times(duration)),
                    neighborTriplet.speed(), neighborTriplet.acceleration());
        }

        /** {@inheritDoc} */
        @Override
        public Length egoAnticipation(final Speed speed, final Acceleration acceleration, final Duration duration)
        {
            return Length.ZERO;
        }

    }

}
