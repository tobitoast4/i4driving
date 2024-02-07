package org.opentrafficsim.i4driving;

import java.util.Iterator;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.road.network.RoadNetwork;

/**
 * This class can be used to stop a simulation.
 * @author wjschakel
 */
public class StopCriterion
{

    /** Simulator. */
    private final OtsSimulatorInterface simulator;

    /** Network. */
    private final RoadNetwork network;

    /** Checking interval. */
    private final Duration interval;

    /**
     * Constructor.
     * @param simulator OtsSimulatorInterface; simulator.
     * @param network RoadNetwork; network.
     * @param startTime Time; start time.
     * @param interval Duration; interval.
     */
    public StopCriterion(final OtsSimulatorInterface simulator, final RoadNetwork network, final Time startTime,
            final Duration interval)
    {
        this.simulator = simulator;
        this.network = network;
        this.interval = interval;
        simulator.scheduleEventAbsTime(startTime, this, "check", null);
    }

    /**
     * Check whether we can stop.
     */
    @SuppressWarnings("unused")
    private void check()
    {
        Iterator<Gtu> gtus = this.network.getGTUs().iterator();
        boolean allStopped = true;
        while (gtus.hasNext() && allStopped)
        {
            allStopped &= gtus.next().getSpeed().eq0();
        }
        if (allStopped)
        {
            this.simulator.endReplication();
        }
        else
        {
            this.simulator.scheduleEventRel(this.interval, this, "check", null);
        }
    }

}
