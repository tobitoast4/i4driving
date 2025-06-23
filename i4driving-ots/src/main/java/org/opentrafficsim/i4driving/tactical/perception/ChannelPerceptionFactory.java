package org.opentrafficsim.i4driving.tactical.perception;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTask;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTaskAcceleration;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTaskCarFollowing;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTaskConflict;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTaskScan;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTaskSignal;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTaskSocio;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelTaskTrafficLight;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.CategoricalLanePerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionFactory;
import org.opentrafficsim.road.gtu.lane.perception.categories.AnticipationTrafficPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.DirectInfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Anticipation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationHeadway;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSpeed;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller.BehavioralAdaptation;

/**
 * This factory produces a channel-based perception.
 * @author wjschakel
 */
@Deprecated // use ScenarioTacticalPlannerFactory instead
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

    /** Estimation instance for absolute speeds. */
    private static final Estimation ESTIMATION_ABSOLUTE = new SaturationEstimation(false);

    /** Estimation instance for relative speeds. */
    private static final Estimation ESTIMATION_RELATIVE = new SaturationEstimation(true);

    /** Anticipation instance for conflicts. */
    private static final Anticipation ANTICIPATION_CONFLICTS = new ConflictAnticipation();

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
                new NeighborsPerceptionChannel(perception, ESTIMATION_RELATIVE, Anticipation.CONSTANT_SPEED));
        // perception.addPerceptionCategory(new DirectNeighborsPerception(perception, HeadwayGtuType.WRAP));
        perception.addPerceptionCategory(new AnticipationTrafficPerception(perception));
        perception.addPerceptionCategory(
                new IntersectionPerceptionChannel(perception, ESTIMATION_ABSOLUTE, ANTICIPATION_CONFLICTS));
        // perception.addPerceptionCategory(new DirectIntersectionPerception(perception, HeadwayGtuType.WRAP));
        perception.addPerceptionCategory(new ActiveModePerception(perception));
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
                .setDefaultParameter(ChannelTaskSignal.TDSIGNAL).setDefaultParameter(Fuller.TS_MAX)
                .setDefaultParameter(ChannelTaskAcceleration.X0).setDefaultParameters(ChannelTaskConflict.class);
        set.setParameter(Fuller.TS_CRIT, 1.0); // required by behavioral adaptations, but ignored, i.e. 1.0
        return set;
    }

}
