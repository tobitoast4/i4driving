package org.opentrafficsim.i4driving.tactical.perception.mental;

import java.util.Iterator;

import org.opentrafficsim.base.OtsRuntimeException;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.i4driving.object.LocalDistraction;
import org.opentrafficsim.i4driving.tactical.perception.LocalDistractionPerception;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.mental.AbstractTask;

/**
 * Task demand from local distraction.
 * @author wjschakel
 */
public class LocalDistractionTask extends AbstractTask
{

    /**
     * Constructor.
     */
    public LocalDistractionTask()
    {
        super("local_distraction");
    }

    @Override
    public double calculateTaskDemand(final LanePerception perception, final LaneBasedGtu gtu, final Parameters parameters)
            throws ParameterException, GtuException
    {
        double td = 0.0;
        try
        {
            Iterator<UnderlyingDistance<LocalDistraction>> distractions = perception
                    .getPerceptionCategory(LocalDistractionPerception.class).getActiveModes().underlyingWithDistance();
            while (distractions.hasNext())
            {
                UnderlyingDistance<LocalDistraction> distraction = distractions.next();
                if (distraction.getDistance().le(distraction.getObject().getRange()))
                {
                    td = Math.max(td, distraction.getObject().getDistractionLevel());
                }
            }
        }
        catch (OperationalPlanException | ParameterException ex)
        {
            throw new OtsRuntimeException(ex);
        }
        return td;
    }

}
