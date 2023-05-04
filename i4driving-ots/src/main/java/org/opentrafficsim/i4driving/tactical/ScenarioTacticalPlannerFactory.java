package org.opentrafficsim.i4driving.tactical;

import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionFactory;
import org.opentrafficsim.road.gtu.lane.tactical.AbstractLaneBasedTacticalPlannerFactory;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModel;
import org.opentrafficsim.road.gtu.lane.tactical.following.CarFollowingModelFactory;

public class ScenarioTacticalPlannerFactory extends AbstractLaneBasedTacticalPlannerFactory<ScenarioTacticalPlanner>
{

    /**
     * Constructor.
     * @param carFollowingModelFactory CarFollowingModelFactory&lt;? extends CarFollowingModel&gt;; car-following mode factory.
     * @param perceptionFactory PerceptionFactory; perception factory.
     */
    public ScenarioTacticalPlannerFactory(final CarFollowingModelFactory<? extends CarFollowingModel> carFollowingModelFactory,
            final PerceptionFactory perceptionFactory)
    {
        super(carFollowingModelFactory, perceptionFactory);
    }

    /** {@inheritDoc} */
    @Override
    public ScenarioTacticalPlanner create(final LaneBasedGtu gtu) throws GtuException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Parameters getParameters() throws ParameterException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
