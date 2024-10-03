package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.exceptions.Try;
import org.djutils.immutablecollections.ImmutableSet;
import org.opentrafficsim.base.parameters.ParameterTypeDuration;
import org.opentrafficsim.base.parameters.ParameterTypeLength;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.core.gtu.perception.EgoPerception;
import org.opentrafficsim.core.network.Link;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.i4driving.tactical.perception.mental.CarFollowingTask;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable.UnderlyingDistance;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.IntersectionPerception;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.HeadwayGtuType;
import org.opentrafficsim.road.gtu.lane.perception.headway.HeadwayGtu;
import org.opentrafficsim.road.network.lane.conflict.Conflict;

/**
 * Task demand for a group of conflicts pertaining to the same conflicting road. This is defined as
 * {@code exp(-max(T_ego, min(T_conf))/h)} where {@code T_ego} is the ego time until the first conflict, {@code T_conf} is the
 * time until the respective conflict of a conflicting vehicle and {@code h} is the car-following task parameter that scales it.
 * @author wjschakel
 */
public final class ChannelTaskConflict implements ChannelTask
{
    /** Look-ahead distance. */
    public static final ParameterTypeLength LOOKAHEAD = ParameterTypes.LOOKAHEAD;

    /** Car-following task parameter. */
    public static final ParameterTypeDuration HEXP = CarFollowingTask.HEXP;

    /** We need to see whether we need to account for acceleration. */
    private static final boolean USE_ACCELERATION = false;

    /** Comparator for underlying objects. */
    // TODO: remove this and its use once UnderlyingDistance implements Comparable
    private static final Comparator<UnderlyingDistance<Conflict>> COMPARATOR = new Comparator<>()
    {
        /** {@inheritDoc} */
        @Override
        public int compare(final UnderlyingDistance<Conflict> o1, final UnderlyingDistance<Conflict> o2)
        {
            int out = o1.getDistance().compareTo(o2.getDistance());
            if (out != 0)
            {
                return out;
            }
            return o1.getObject().getFullId().compareTo(o2.getObject().getFullId());
        }
    };

    /**
     * Standard supplier that supplies a task per grouped set of conflicts based on common upstream nodes. This also adds a
     * scanning task demand to each of these channels.
     */
    public static final Function<LanePerception, Set<ChannelTask>> SUPPLIER = (perception) ->
    {
        IntersectionPerception intersection =
                Try.assign(() -> perception.getPerceptionCategory(IntersectionPerception.class), "No intersection perception.");
        Iterator<UnderlyingDistance<Conflict>> conflicts =
                intersection.getConflicts(RelativeLane.CURRENT).underlyingWithDistance();

        // Find groups of conflicts when their upstream nodes are intersecting sets
        Map<SortedSet<UnderlyingDistance<Conflict>>, Set<Node>> groups = new LinkedHashMap<>();
        Length x0 = Try.assign(() -> perception.getGtu().getParameters().getParameter(LOOKAHEAD), "No x0 parameter.");
        while (conflicts.hasNext())
        {
            UnderlyingDistance<Conflict> conflict = conflicts.next();
            Set<Node> nodes = getUpstreamNodes(conflict.getObject().getOtherConflict(), x0);
            // find overlap
            Entry<SortedSet<UnderlyingDistance<Conflict>>, Set<Node>> group = null;
            Iterator<Entry<SortedSet<UnderlyingDistance<Conflict>>, Set<Node>>> groupIterator = groups.entrySet().iterator();
            while (groupIterator.hasNext())
            {
                Entry<SortedSet<UnderlyingDistance<Conflict>>, Set<Node>> entry = groupIterator.next();
                if (entry.getValue().stream().anyMatch(nodes::contains))
                {
                    // overlap with this entry
                    if (group == null)
                    {
                        entry.getKey().add(conflict);
                        entry.getValue().addAll(nodes);
                        group = entry;
                        // keep looping to also merge other groups if they overlap with the upstream nodes of this conflict
                    }
                    else
                    {
                        // the nodes overlap with multiple groups that did so far not yet overlap, merge the other group too
                        group.getKey().addAll(entry.getKey());
                        group.getValue().addAll(entry.getValue());
                        groupIterator.remove();
                    }
                }
            }
            if (group == null)
            {
                // no overlap found, make new group
                // TODO: remove COMPARATOR argument once UnderlyingDistance implements Comparable
                SortedSet<UnderlyingDistance<Conflict>> key = new TreeSet<>(COMPARATOR);
                key.add(conflict);
                groups.put(key, nodes);
            }
        }

        // Create task for each group
        Set<ChannelTask> tasks = new LinkedHashSet<>();
        for (SortedSet<UnderlyingDistance<Conflict>> group : groups.keySet())
        {
            tasks.add(new ChannelTaskConflict(group));
            tasks.add(new ChannelTaskScan(group.first().getObject()));
            // make sure the channel (key is first conflict) can be found for all individual conflicts
            if (perception.getMental() instanceof ChannelMental)
            {
                ChannelMental channelMental = (ChannelMental) perception.getMental();
                group.forEach((c) -> channelMental.mapToChannel(c.getObject(), group.first().getObject()));
            }
        }
        return tasks;
    };

    /** Conflicts in the group. */
    private final SortedSet<UnderlyingDistance<Conflict>> conflicts;

    /**
     * Constructor.
     * @param conflicts conflicts in the group.
     */
    private ChannelTaskConflict(final SortedSet<UnderlyingDistance<Conflict>> conflicts)
    {
        this.conflicts = conflicts;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return this.conflicts.first().getObject().getFullId();
    }

    /** {@inheritDoc} */
    @Override
    public Object getChannel()
    {
        return this.conflicts.first().getObject();
    }

    /** {@inheritDoc} */
    @Override
    public double getDemand(final LanePerception perception)
    {
        // In the following, 'headway' means time until static conflict is reached, possibly with acceleration.
        // Get minimum headway of first vehicle on each conflict in the group
        Duration conflictHeadway = Duration.POSITIVE_INFINITY;
        LaneBasedGtu gtu = Try.assign(() -> perception.getGtu(), "Gtu not initialized.");
        Length x0 = Try.assign(() -> perception.getGtu().getParameters().getParameter(LOOKAHEAD), "No x0 parameter.");
        for (UnderlyingDistance<Conflict> conflict : this.conflicts)
        {
            PerceptionCollectable<HeadwayGtu, LaneBasedGtu> conflictingGtus =
                    conflict.getObject().getOtherConflict().getUpstreamGtus(gtu, HeadwayGtuType.WRAP, x0);
            if (!conflictingGtus.isEmpty())
            {
                HeadwayGtu conflictingGtu = conflictingGtus.first();
                if (USE_ACCELERATION)
                {
                    Acceleration a = Try.assign(() -> conflictingGtu.getParameters().getParameter(ParameterTypes.A),
                            "No parameter A in conflicting GTU.");
                    conflictHeadway = ttcFree(conflictingGtu.getDistance(), conflictingGtu.getSpeed(),
                            conflictingGtu.getDesiredSpeed(), a);
                }
                else
                {
                    conflictHeadway = Duration.min(conflictHeadway, conflictingGtu.isParallel() ? Duration.ZERO
                            : conflictingGtu.getDistance().divide(conflictingGtu.getSpeed()));
                }
            }
        }
        // Get maximum of own and minimum conflicting headway to represent urgency
        EgoPerception<?, ?> ego =
                Try.assign(() -> perception.getPerceptionCategory(EgoPerception.class), "EgoPerception not present.");
        Duration headway;
        if (USE_ACCELERATION)
        {
            Acceleration a = Try.assign(() -> perception.getGtu().getParameters().getParameter(ParameterTypes.A),
                    "No parameter A in GTU.");
            Speed desiredSpeed = Try.assign(() -> perception.getGtu().getDesiredSpeed(), "GTU not initialized.");
            Duration egoHeadway = ttcFree(this.conflicts.first().getDistance(), ego.getSpeed(), desiredSpeed, a);
            headway = Duration.max(conflictHeadway, egoHeadway);
        }
        else
        {
            headway = Duration.max(conflictHeadway, this.conflicts.first().getDistance().divide(ego.getSpeed()));
            if (headway.eq0())
            {
                return 0.999;
            }
        }
        // Scale by h in exponential function
        Duration h = Try.assign(() -> perception.getGtu().getParameters().getParameter(HEXP), "Parameter h_exp not present.");
        return Math.exp(-headway.si / h.si);
    }

    /**
     * Finds all nodes within a given distance upstream of a conflict, stopping at any diverge, branging at merges.
     * @param conflict conflict.
     * @param x0 distance to loop upstream.
     * @return set of all upstream nodes within distance.
     */
    private static Set<Node> getUpstreamNodes(final Conflict conflict, final Length x0)
    {
        Set<Node> nodes = new LinkedHashSet<>();
        Link link = conflict.getLane().getLink();
        Length distance = link.getLength().times(conflict.getLane().fraction(conflict.getLongitudinalPosition()) - 1.0);
        appendUpstreamNodes(link, distance, x0, nodes);
        return nodes;
    }

    /**
     * Append upstream nodes, branging upstream at merges, stopping at any diverge.
     * @param link next link to move along.
     * @param distance distance between end of link and conflict, upstream of conflict.
     * @param x0 search distance.
     * @param nodes collected nodes.
     */
    private static void appendUpstreamNodes(final Link link, final Length distance, final Length x0, final Set<Node> nodes)
    {
        Length nextDistance = distance.plus(link.getLength());
        if (nextDistance.le(x0))
        {
            Node start = link.getStartNode();
            nodes.add(start);
            ImmutableSet<Link> links = start.getLinks();
            Set<Link> upstreamLinks = new LinkedHashSet<>();
            for (Link next : links)
            {
                if (!next.equals(link))
                {
                    if (next.getStartNode().equals(start))
                    {
                        // diverge
                        return;
                    }
                    upstreamLinks.add(next);
                }
            }
            for (Link upstreamLink : upstreamLinks)
            {
                appendUpstreamNodes(upstreamLink, nextDistance, x0, nodes);
            }
        }
    }

    /**
     * Returns the duration until distance is covered assuming constant acceleration up to the desired speed.
     * @param distance distance.
     * @param speed initial speed.
     * @param desiredSpeed desired speed.
     * @param acceleration (assumed) acceleration.
     * @return time until distance is covered.
     */
    private static Duration ttcFree(final Length distance, final Speed speed, final Speed desiredSpeed,
            final Acceleration acceleration)
    {
        if (speed.gt(desiredSpeed))
        {
            return distance.divide(speed);
        }
        double tAccelerate = (desiredSpeed.si - speed.si) / acceleration.si;
        double dxAccelerate = speed.si * tAccelerate + .5 * acceleration.si * tAccelerate * tAccelerate;
        if (dxAccelerate <= distance.si)
        {
            double dxRemain = distance.si - dxAccelerate;
            return Duration.instantiateSI(tAccelerate + dxRemain / desiredSpeed.si);
        }
        return Duration.instantiateSI(
                (Math.sqrt(speed.si * speed.si + 2.0 * acceleration.si + distance.si) - speed.si) / acceleration.si);
    }

}
