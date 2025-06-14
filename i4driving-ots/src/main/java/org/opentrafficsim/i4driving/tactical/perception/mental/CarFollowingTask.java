package org.opentrafficsim.i4driving.tactical.perception.mental;

import org.djunits.value.vdouble.scalar.Duration;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDuration;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.base.parameters.constraint.NumericConstraint;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.TaskHeadwayCollector;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtu;
import org.opentrafficsim.road.gtu.lane.perception.mental.AbstractTask;

/**
 * Car following task in anticipation reliance framework. This is based on headway scaled by {@code h} in an exponential
 * relation.
 * @author wjschakel
 */
public class CarFollowingTask extends AbstractTask
{

    /** Car-following task parameter. */
    public static final ParameterTypeDuration HEXP = new ParameterTypeDuration("h_exp",
            "Exponential decay of car-following task by headway.", Duration.instantiateSI(3.83), NumericConstraint.POSITIVE);

    /**
     * Constructor.
     */
    public CarFollowingTask()
    {
        super("car-following");
    }

    /** {@inheritDoc} */
    @Override
    public double calculateTaskDemand(final LanePerception perception, final LaneBasedGtu gtu, final Parameters parameters)
            throws ParameterException, GtuException
    {
        try
        {
            NeighborsPerception neighbors = perception.getPerceptionCategory(NeighborsPerception.class);
            PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders = neighbors.getLeaders(RelativeLane.CURRENT);
            Duration headway = leaders.collect(new TaskHeadwayCollector(gtu.getSpeed()));
            return headway == null ? 0.0 : Math.exp(-headway.si / parameters.getParameter(HEXP).si);
        }
        catch (OperationalPlanException ex)
        {
            throw new GtuException(ex);
        }
    }
}
