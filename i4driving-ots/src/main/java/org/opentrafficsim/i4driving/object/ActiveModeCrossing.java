package org.opentrafficsim.i4driving.object;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Try;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.perception.collections.HistoricalLinkedHashMap;
import org.opentrafficsim.core.perception.collections.HistoricalMap;
import org.opentrafficsim.road.network.lane.LanePosition;
import org.opentrafficsim.road.network.lane.object.AbstractLaneBasedObject;
import org.opentrafficsim.road.network.lane.object.LaneBasedObject;

/**
 * This object is used to simulate the crossing of active modes, including at unexpected locations (e.g. jaywalking). Arrival
 * times of objects, including zero for objects at the location, are provided externally. Perception and response can use the
 * fact whether this location is based on infrastructure (expected), and the minimum arrival time.
 * @author wjschakel
 */
public class ActiveModeCrossing extends AbstractLaneBasedObject
{

    /** Whether this location where active modes cross is based on infrastructure. */
    private final boolean infrastructureBased;

    /** Map of arriving objects. */
    private final HistoricalMap<String, ActiveModeArrival> arrivals;
    
    /** Active mode objects that have passed. */ 
    private final Set<String> passed = new LinkedHashSet<>();

    /** */
    private static final long serialVersionUID = 20250515L;

    /**
     * Constructor.
     * @param position position
     * @param infrastructureBased whether this location where active modes cross is based on infrastructure
     * @throws NetworkException when the location is not correctly defined
     */
    public ActiveModeCrossing(final LanePosition position, final boolean infrastructureBased) throws NetworkException
    {
        super(UUID.randomUUID().toString(), position.lane(), position.position(),
                LaneBasedObject.makeGeometry(position.lane(), position.position(), 1.0));
        this.infrastructureBased = infrastructureBased;
        this.arrivals = new HistoricalLinkedHashMap<>(position.lane().getLink().getSimulator().getReplication()
                .getHistoryManager(position.lane().getLink().getSimulator()));
        position.lane().addLaneBasedObject(this);
    }

    /**
     * Returns whether this location where active modes cross is based on infrastructure (e.g. zebra crossing) is not (e.g.
     * jaywalking).
     * @return whether this location where active modes cross is based on infrastructure
     */
    public boolean isInfrastructureBased()
    {
        return this.infrastructureBased;
    }

    /**
     * Sets the arrival of the object with given id. If the distance is increased from earlier, the arrival is removed.
     * @param objectId object id
     * @param distance distance of active mode object
     * @param speed speed of active mode object, towards the road
     */
    public void setArrival(final String objectId, final Length distance, final Speed speed)
    {
        if (this.passed.contains(objectId))
        {
            return;
        }
        if (this.arrivals.containsKey(objectId) && this.arrivals.get(objectId).distance().lt(distance))
        {
            this.arrivals.remove(objectId);
            if (this.arrivals.isEmpty())
            {
                Try.execute(() -> getLane().removeLaneBasedObject(this), "Unable to remove ActiveModeCrossing");
            }
            this.passed.add(objectId);
            return;
        }
        this.arrivals.put(objectId, new ActiveModeArrival(distance, speed));
    }

    /**
     * Removes the arrival of the object with given id.
     * @param objectId object id
     */
    public void removeArrival(final String objectId)
    {
        this.arrivals.remove(objectId);
    }

    /**
     * Return the arrivals.
     * @return arrivals
     */
    public SortedSet<ActiveModeArrival> getArrivals()
    {
        return new TreeSet<>(this.arrivals.get().values());
    }

    /**
     * Returns the arrivals at the given time.
     * @param time time
     * @return arrivals at the given time
     */
    public SortedSet<ActiveModeArrival> getArrivals(final Time time)
    {
        return new TreeSet<>(this.arrivals.get(time).values());
    }

    /**
     * Defines a single arrival of an active mode.
     * @param distance distance of active mode object
     * @param speed speed of active mode object, towards the road
     */
    public record ActiveModeArrival(Length distance, Speed speed) implements Comparable<ActiveModeArrival>
    {
        @Override
        public int compareTo(final ActiveModeArrival o)
        {
            int i = this.distance().compareTo(o.distance());
            if (i == 0)
            {
                return -this.speed().compareTo(o.speed());
            }
            return i;
        }
    };

}
