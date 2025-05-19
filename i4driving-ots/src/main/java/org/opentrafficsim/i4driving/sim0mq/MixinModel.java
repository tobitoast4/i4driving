package org.opentrafficsim.i4driving.sim0mq;

import java.util.LinkedHashSet;
import java.util.Set;

import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterSet;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuErrorHandler;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.perception.DirectEgoPerception;
import org.opentrafficsim.i4driving.tactical.AccelerationActiveModeCrossing;
import org.opentrafficsim.i4driving.tactical.ScenarioTacticalPlanner;
import org.opentrafficsim.i4driving.tactical.perception.ActiveModePerception;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.i4driving.tactical.perception.mental.LaneChangeTask;
import org.opentrafficsim.i4driving.tactical.perception.mental.TaskManagerAr;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;
import org.opentrafficsim.road.gtu.lane.CollisionException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.CategoricalLanePerception;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.AnticipationTrafficPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.DirectInfrastructurePerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Anticipation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.DirectNeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType.PerceivedHeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationHeadway;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSituationalAwareness;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSpeed;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller.BehavioralAdaptation;
import org.opentrafficsim.road.gtu.lane.perception.mental.Task;
import org.opentrafficsim.road.gtu.lane.perception.mental.TaskManager;
import org.opentrafficsim.road.gtu.lane.perception.mental.TaskManager.SummativeTaskManager;
import org.opentrafficsim.road.gtu.lane.tactical.LaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.AbstractIdm;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.DesiredSpeedModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlus;
import org.opentrafficsim.road.gtu.lane.tactical.following.IdmPlusMulti;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveKeep;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveRoute;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSocioSpeed;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.IncentiveSpeedWithCourtesy;
import org.opentrafficsim.road.gtu.lane.tactical.lmrs.SocioDesiredSpeed;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Cooperation;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.GapAcceptance;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsParameters;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.LmrsUtil;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Synchronization;
import org.opentrafficsim.road.gtu.lane.tactical.util.lmrs.Tailgating;

import nl.tudelft.simulation.jstats.streams.StreamInterface;
import picocli.CommandLine.Option;

/**
 * Tactical planner factory supplier that is based on command line settings.
 */
public class MixinModel
{

    /** Apply full Fuller and overwrite all other mental settings. */
    @Option(names = {"--fullFuller"}, description = "Apply full Fuller", negatable = true, defaultValue = "true")
    private boolean fullFuller = true;

    /** Apply Fuller. */
    @Option(names = {"--fuller"}, description = "Apply Fuller", negatable = true, defaultValue = "true")
    private boolean fuller = true;

    /** Include car-following task demand in Fuller. */
    @Option(names = {"--carFollowingTask"}, description = "Include car-following task.", negatable = true,
            defaultValue = "true")
    private boolean carFollowingTask = true;

    /** Include lane change task demand in Fuller. */
    @Option(names = {"--laneChangeTask"}, description = "Include lane change task.", negatable = true, defaultValue = "true")
    private boolean laneChangeTask = true;

    /** Set lane change task as primary in Fuller. */
    @Option(names = {"--laneChangeIsPrimary"}, description = "Set lane change task as primary.", negatable = true,
            defaultValue = "true")
    private boolean laneChangeIsPrimary = true;

    /** Apply anticipation reliance in Fuller. */
    @Option(names = {"--anticipationReliance"}, description = "Apply anticipation reliance in Fuller.", negatable = true,
            defaultValue = "true")
    private boolean anticipationReliance = true;
    
    /** Adapt headway in Fuller. */
    @Option(names = {"--adaptHeadway"}, description = "Adapt headway in Fuller.", negatable = true, defaultValue = "true")
    private boolean adaptHeadway = true;

    /** Adapt speed in Fuller. */
    @Option(names = {"--adaptSpeed"}, description = "Adapt speed in Fuller.", negatable = true, defaultValue = "true")
    private boolean adaptSpeed = true;

    /** Anticipate multiple leaders in car-following. */
    @Option(names = {"--multiAnticipation"}, description = "Anticipate multiple leaders in car-following.", negatable = true,
            defaultValue = "true")
    private boolean multiAnticipation = true;

    /** Apply full social interactions and overwrite all other social settings. */
    @Option(names = {"--fullSocio"}, description = "Apply full social model.", negatable = true, defaultValue = "true")
    private boolean fullSocio = true;

    /** Apply social interactions. */
    @Option(names = {"--socio"}, description = "Apply social model.", negatable = true, defaultValue = "true")
    private boolean socio = true;

    /** Apply tailgating in social interactions. */
    @Option(names = {"--tailgating"}, description = "Apply tailgating.", negatable = true, defaultValue = "true")
    private boolean tailgating = true;

    /** Apply lane change incentive in social interactions. */
    @Option(names = {"--socioLaneChangeIncentive"}, description = "Apply social lane change incentive.", negatable = true,
            defaultValue = "true")
    private boolean socioLaneChangeIncentive = true;

    /** Apply desired speed in social interactions. */
    @Option(names = {"--socioDesiredSpeed"}, description = "Apply social desired speed.", negatable = true,
            defaultValue = "true")
    private boolean socioDesiredSpeed = true;

    /** Fraction of drivers with over estimation. */
    @Option(names = {"--fractionOverEstimation"}, description = "Fraction of drivers with over estimation.",
            defaultValue = "1.0")
    private double fractionOverEstimation = 1.0;
    
    /**
     * Creates tactical planner factory for GTUs with ScenarioTacticalPlanner.
     * @param randomStream stream
     * @return tactical planner factory for GTUs with ScenarioTacticalPlanner
     */
    public LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner> getTacticalPlanner(final StreamInterface randomStream)
    {
        LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner> tacticalFactory =
                new LaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner>()
                {
                    /** GTU error handler for collisions. */
                    private GtuErrorHandler errorHandler = new GtuErrorHandler()
                    {
                        /** {@inheritDoc} */
                        @Override
                        public void handle(final Gtu gtu, final Exception ex) throws Exception
                        {
                            if (ex.getCause() instanceof CollisionException)
                            {
                                System.out.println(ex.getCause().getMessage());
                                gtu.getSimulator().endReplication();
                                System.exit(0);
                            }
                            else
                            {
                                throw ex;
                            }
                        }
                    };

                    /** {@inheritDoc} */
                    @Override
                    public Parameters getParameters() throws ParameterException
                    {
                        ParameterSet parameters = new ParameterSet();
                        parameters.setDefaultParameters(LmrsUtil.class);
                        parameters.setDefaultParameters(LmrsParameters.class);
                        parameters.setDefaultParameters(AbstractIdm.class);
                        parameters.setDefaultParameter(ParameterTypes.PERCEPTION);
                        parameters.setDefaultParameter(ParameterTypes.LOOKBACK);
                        parameters.setDefaultParameter(ParameterTypes.LOOKAHEAD);
                        parameters.setDefaultParameter(ParameterTypes.VCONG);
                        parameters.setDefaultParameter(ParameterTypes.LCDUR);
                        if (MixinModel.this.fullFuller || MixinModel.this.fuller)
                        {
                            parameters.setDefaultParameter(Fuller.TC);
                            parameters.setDefaultParameter(Fuller.TS);
                            parameters.setDefaultParameter(Fuller.TS_CRIT);
                            parameters.setDefaultParameter(Fuller.TS_MAX);
                            if (MixinModel.this.fullFuller || MixinModel.this.carFollowingTask)
                            {
                                parameters.setDefaultParameter(CarFollowingTask.HEXP);
                            }
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA_MIN);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.SA_MAX);
                            parameters.setDefaultParameter(AdaptationSituationalAwareness.TR_MAX);
                            if (MixinModel.this.fullFuller || MixinModel.this.adaptHeadway)
                            {
                                parameters.setDefaultParameter(AdaptationHeadway.BETA_T);
                            }
                            if (MixinModel.this.fullFuller || MixinModel.this.adaptSpeed)
                            {
                                parameters.setDefaultParameter(AdaptationSpeed.BETA_V0);
                            }
                            if (MixinModel.this.fullFuller || MixinModel.this.multiAnticipation)
                            {
                                parameters.setDefaultParameter(IdmPlusMulti.NLEADERS);
                            }
                            if (MixinModel.this.fullFuller || MixinModel.this.anticipationReliance)
                            {
                                parameters.setDefaultParameter(TaskManagerAr.ALPHA);
                                parameters.setDefaultParameter(TaskManagerAr.BETA);
                            }
                        }
                        if (MixinModel.this.fullSocio || MixinModel.this.socio)
                        {
                            parameters.setDefaultParameter(Tailgating.RHO);
                            parameters.setDefaultParameter(LmrsParameters.SOCIO);
                        }
                        parameters.setParameter(Estimation.OVER_EST,
                                randomStream.nextDouble() <= MixinModel.this.fractionOverEstimation ? 1.0 : -1.0);
                        parameters.setParameter(ChannelFuller.EST_FACTOR, 1.0);
                        return parameters;
                    }

                    /** {@inheritDoc} */
                    @Override
                    public ScenarioTacticalPlanner create(final LaneBasedGtu gtu) throws GtuException
                    {
                        gtu.setErrorHandler(this.errorHandler);

                        DesiredSpeedModel desiredSpeedModel = (MixinModel.this.fullSocio
                                || (MixinModel.this.socio && MixinModel.this.socioDesiredSpeed))
                                        ? new SocioDesiredSpeed(AbstractIdm.DESIRED_SPEED) : AbstractIdm.DESIRED_SPEED;

                        CarFollowingModel idm = MixinModel.this.multiAnticipation
                                ? new IdmPlusMulti(AbstractIdm.HEADWAY, desiredSpeedModel)
                                : new IdmPlus(AbstractIdm.HEADWAY, desiredSpeedModel);

                        // TODO integrate ChannelPerceptionFactory (or its functionality).
                        Fuller mental = null;
                        if (MixinModel.this.fullFuller || MixinModel.this.fuller)
                        {
                            Set<Task> tasks = new LinkedHashSet<>();
                            if (MixinModel.this.fullFuller || MixinModel.this.carFollowingTask)
                            {
                                tasks.add(new CarFollowingTask());
                            }
                            if (MixinModel.this.fullFuller || MixinModel.this.laneChangeTask)
                            {
                                tasks.add(new LaneChangeTask());
                            }
                            Set<BehavioralAdaptation> behavioralAdapatations = new LinkedHashSet<>();
                            behavioralAdapatations.add(new AdaptationSituationalAwareness());
                            if (MixinModel.this.fullFuller || MixinModel.this.adaptHeadway)
                            {
                                behavioralAdapatations.add(new AdaptationHeadway());
                            }
                            if (MixinModel.this.fullFuller || MixinModel.this.adaptSpeed)
                            {
                                behavioralAdapatations.add(new AdaptationSpeed());
                            }
                            String primaryTask =
                                    MixinModel.this.laneChangeIsPrimary ? "lane-changing" : "car-following";
                            TaskManager taskManager =
                                    (MixinModel.this.fullFuller || MixinModel.this.anticipationReliance)
                                            ? new TaskManagerAr(primaryTask) : new SummativeTaskManager();
                            mental = new Fuller(tasks, behavioralAdapatations, taskManager);
                        }
                        
                        LanePerception lanePerception = new CategoricalLanePerception(gtu, mental);
                        lanePerception.addPerceptionCategory(new DirectEgoPerception<>(lanePerception));
                        lanePerception.addPerceptionCategory(new AnticipationTrafficPerception(lanePerception));
                        lanePerception.addPerceptionCategory(new DirectInfrastructurePerception(lanePerception));
                        lanePerception.addPerceptionCategory(new ActiveModePerception(lanePerception));
                        Estimation estimation = Estimation.FACTOR_ESTIMATION;
                        HeadwayGtuType headwayGtuType =
                                (MixinModel.this.fullFuller || MixinModel.this.fuller)
                                        ? new PerceivedHeadwayGtuType(estimation, Anticipation.CONSTANT_SPEED)
                                        : HeadwayGtuType.WRAP;
                        lanePerception.addPerceptionCategory(new DirectNeighborsPerception(lanePerception, headwayGtuType));
                        Tailgating tail = (MixinModel.this.fullSocio
                                || (MixinModel.this.socio && MixinModel.this.tailgating))
                                        ? Tailgating.PRESSURE : Tailgating.NONE;
                        ScenarioTacticalPlanner tacticalPlanner = new ScenarioTacticalPlanner(idm, gtu, lanePerception,
                                Synchronization.PASSIVE, Cooperation.PASSIVE, GapAcceptance.INFORMED, tail);
                        tacticalPlanner.addMandatoryIncentive(new IncentiveRoute());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveSpeedWithCourtesy());
                        tacticalPlanner.addVoluntaryIncentive(new IncentiveKeep());
                        tacticalPlanner.addAccelerationIncentive(new AccelerationActiveModeCrossing());
                        if (MixinModel.this.fullSocio
                                || (MixinModel.this.socio && MixinModel.this.socioLaneChangeIncentive))
                        {
                            tacticalPlanner.addVoluntaryIncentive(new IncentiveSocioSpeed());
                        }
                        return tacticalPlanner;
                    }
                };
        return tacticalFactory;
    }

}
