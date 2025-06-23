package org.opentrafficsim.i4driving.demo;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.djunits.value.vdouble.scalar.Duration;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlan;
import org.opentrafficsim.core.gtu.plan.operational.OperationalPlanException;
import org.opentrafficsim.core.network.Network;
import org.opentrafficsim.road.gtu.lane.AbstractLaneBasedMoveChecker;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.NeighborsPerception;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtu;

/**
 * Stops GTU once it has collided.
 * @author wjschakel
 */
public class StopCollisionDetector extends AbstractLaneBasedMoveChecker
{

    /** */
    private static final long serialVersionUID = 20250623L;

    /**
     * Constructor.
     * @param network Network; network
     */
    public StopCollisionDetector(final Network network)
    {
        super(network);
    }

    /** {@inheritDoc} */
    @Override
    public void checkMove(final LaneBasedGtu gtu) throws Exception
    {
        try
        {
            NeighborsPerception neighbors =
                    gtu.getTacticalPlanner().getPerception().getPerceptionCategory(NeighborsPerception.class);
            PerceptionCollectable<HeadwayGtu, LaneBasedGtu> leaders = neighbors.getLeaders(RelativeLane.CURRENT);
            Iterator<UnderlyingDistance<LaneBasedGtu>> gtus = leaders.underlyingWithDistance();
            if (!gtus.hasNext())
            {
                return;
            }
            UnderlyingDistance<LaneBasedGtu> leader = gtus.next();
            if (leader.getDistance().lt0())
            {
                System.err.println("GTU " + gtu.getId() + " collided with GTU " + leader.getObject().getId());
                Method method = Gtu.class.getDeclaredMethod("setOperationalPlan", OperationalPlan.class);
                method.setAccessible(true);
                method.invoke(gtu, OperationalPlan.standStill(gtu, gtu.getLocation(), gtu.getSimulator().getSimulatorAbsTime(),
                        Duration.POSITIVE_INFINITY));
                gtu.getSimulator().cancelEvent(gtu.getNextMoveEvent());
            }
        }
        catch (OperationalPlanException exception)
        {
            throw new GtuException(exception);
        }
    }

}
