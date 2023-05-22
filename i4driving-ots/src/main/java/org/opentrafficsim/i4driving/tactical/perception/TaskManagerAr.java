package org.opentrafficsim.i4driving.tactical.perception;

import java.util.LinkedHashSet;
import java.util.Set;

import org.djutils.exceptions.Throw;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDouble;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.base.parameters.constraint.DualBound;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.mental.Task;
import org.opentrafficsim.road.gtu.lane.perception.mental.TaskManager;

/**
 * Task manager that applies anticipation reliance. 
 * @author wjschakel
 */
public class TaskManagerAr implements TaskManager
{

    /** Fraction of primary task that can be reduced by anticipation reliance. */
    public static final ParameterTypeDouble ALPHA = new ParameterTypeDouble("alpha",
            "Fraction of primary task that can be reduced by anticipation reliance.", 0.8, DualBound.UNITINTERVAL);

    /** Fraction of auxiliary tasks that can be reduced by anticipation reliance. */
    public static final ParameterTypeDouble BETA = new ParameterTypeDouble("beta",
            "Fraction of auxiliary tasks that can be reduced by anticipation reliance.", 0.6, DualBound.UNITINTERVAL);
    
    /** Primary task id. */
    private final String primaryTaskId;
    
    /**
     * Constructor.
     * @param primaryTaskId String; primary task id.
     */
    public TaskManagerAr(final String primaryTaskId)
    {
        Throw.whenNull(primaryTaskId, "Primary task id may not be null.");
        this.primaryTaskId = primaryTaskId;
    }
    
    /** {@inheritDoc} */
    @Override
    public void manage(final Set<Task> tasks, final LanePerception perception, final LaneBasedGtu gtu,
            final Parameters parameters) throws ParameterException, GtuException
    {
        Task primary = null;
        Set<Task> auxiliaryTasks = new LinkedHashSet<>();
        for (Task task : tasks)
        {
            if (task.getId().equals(this.primaryTaskId))
            {
                primary = task;
            }
            else
            {
                auxiliaryTasks.add(task);
            }
        }
        Throw.whenNull(primary, "There is no task with id '%s'.", this.primaryTaskId);
        double primaryTaskDemand = primary.calculateTaskDemand(perception, gtu, parameters);
        primary.setTaskDemand(primaryTaskDemand);
        // max AR is alpha of TD, actual AR approaches 0 for increasing TD
        double a = parameters.getParameter(ALPHA);
        double b = parameters.getParameter(BETA);
        primary.setAnticipationReliance(a * primaryTaskDemand * (1.0 - primaryTaskDemand));
        for (Task auxiliary : auxiliaryTasks)
        {
            double auxiliaryTaskLoad = auxiliary.calculateTaskDemand(perception, gtu, parameters);
            auxiliary.setTaskDemand(auxiliaryTaskLoad);
            // max AR is beta of TD, actual AR approaches 0 as primary TD approaches 0
            auxiliary.setAnticipationReliance(b * auxiliaryTaskLoad * primaryTaskDemand);
        }
    }
    
}
