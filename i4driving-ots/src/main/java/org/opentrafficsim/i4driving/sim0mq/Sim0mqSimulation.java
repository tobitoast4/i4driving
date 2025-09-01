package org.opentrafficsim.i4driving.sim0mq;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.core.network.Link;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.road.gtu.generator.characteristics.LaneBasedGtuCharacteristicsGeneratorOd;
import org.opentrafficsim.road.network.RoadNetwork;

/**
 * Interface for different simulation types (demo, OpenDRIVE, etc.) used in co-simulation.
 * @author wjschakel
 */
public interface Sim0mqSimulation
{

    Time getMergeDelay();

    double getThresholdDistance();

    double getControlLimitDistance();
    /**
     * Returns the network.
     * @return network
     */
    RoadNetwork getNetwork();

    /**
     * Returns the GTU characteristics generator.
     * @return GTU characteristics generator
     */
    LaneBasedGtuCharacteristicsGeneratorOd getGtuCharacteristicsGeneratorOd();

    /**
     * Return parameter factory.
     * @return parameter factory
     */
    ParameterFactorySim0mq getParameterFactory();

    /**
     * Returns the node id of the origin represented by the input id. Default is the input id.
     * @param id input id
     * @param designDirection of the input id, can be {@code null} when not relevant
     * @return origin node id
     */
    default String getOrigin(String id, Boolean designDirection)
    {
        return id;
    }

    /**
     * Returns the node id of the destination represented by the input id. Default is the input id.
     * @param id input id
     * @param designDirection of the input id, can be {@code null} when not relevant
     * @return destination node id
     */
    default String getDestination(String id, Boolean designDirection)
    {
        return id;
    }
    
    /**
     * Return the route object type, which can be used to find the nodes in the route.
     * @return route object type which is {@code NODE} be default 
     */
    default RouteObjectType getRouteObjectType()
    {
        return RouteObjectType.NODE;
    }
    
    /**
     * Object type the ids refer to.
     */
    enum RouteObjectType
    {
        /** Ids refer to nodes. */
        NODE((net, ids) ->
        {
            List<Node> nodes = new ArrayList<>();
            ids.forEach((id) -> nodes.add(net.getNode(id)));
            return nodes;
        }),

        /** Ids refer to links. */
        LINK((net, ids) ->
        {
            List<Node> nodes = new ArrayList<>();
            nodes.add(net.getLink(ids.get(0)).getStartNode());
            ids.forEach((id) -> nodes.add(net.getLink(id).getEndNode()));
            return nodes;
        }),

        /** Ids refer to roads, possibly consisting of multiple links. Link ids must be the id, or start with id + "_". */
        ROAD((net, ids) ->
        {
            List<Node> nodes = new ArrayList<>();
            for (Link link : net.getLinkMap().values())
            {
                if (isOnRoad(link, ids.get(0)))
                {
                    if (ids.size() > 1)
                    {
                        // check whether this is the first link
                        for (Link next : link.getEndNode().getLinks())
                        {
                            if (next.getStartNode().equals(link.getEndNode()) && isOnRoad(next, ids.get(1)))
                            {
                                upstream(link, ids.get(0), nodes);
                                downstream(next, 1, ids, nodes);
                                return nodes;
                            }
                        }
                    }
                    else
                    {
                        // just one link on the route
                        nodes.add(link.getStartNode());
                        nodes.add(link.getEndNode());
                        return nodes;
                    }
                }
            }
            return nodes;
        });

        /** Function to return nodes from object ids. */
        private final BiFunction<RoadNetwork, List<String>, List<Node>> idsToNodesFunction;

        /**
         * Constructor.
         * @param idsToNodesFunction function to obtain nodes
         */
        RouteObjectType(final BiFunction<RoadNetwork, List<String>, List<Node>> idsToNodesFunction)
        {
            this.idsToNodesFunction = idsToNodesFunction;
        }
        
        /**
         * Returns the nodes based on input object ids.
         * @param network network
         * @param objectIds input object ids
         * @return nodes
         */
        public List<Node> getNodes(final RoadNetwork network, final List<String> objectIds)
        {
            return this.idsToNodesFunction.apply(network, objectIds);
        }

        /**
         * Returns whether the link is on a road with given id, i.e. the link has the same id, or starts with id + "_".
         * @param link link
         * @param roadId road id
         * @return whether the link is on a road with given id
         */
        private static boolean isOnRoad(final Link link, final String roadId)
        {
            return link.getId().equals(roadId) || link.getId().startsWith(roadId + "_");
        }

        /**
         * Adds the start nodes for links, all on road with given road id.
         * @param link most downstream link on road (or upstream links during recursion)
         * @param roadId road id
         * @param nodes list to add nodes in
         */
        private static void upstream(final Link link, final String roadId, final List<Node> nodes)
        {
            nodes.add(0, link.getStartNode());
            for (Link prev : link.getStartNode().getLinks())
            {
                if (prev.getEndNode().equals(link.getStartNode()) && isOnRoad(prev, roadId))
                {
                    upstream(prev, roadId, nodes);
                }
            }
        }

        /**
         * Adds start nodes for all links on the road with road id at the given index, and further downstream. If no downstream
         * link on a road is found, the end node is added and the search stops.
         * @param link most upstream link on road (or downstream links during recursion)
         * @param roadIndex index of current road in list of road ids
         * @param roadIds road ids in route
         * @param nodes list to add nodes in
         */
        private static void downstream(final Link link, final int roadIndex, final List<String> roadIds, final List<Node> nodes)
        {
            nodes.add(link.getStartNode());
            for (Link next : link.getEndNode().getLinks())
            {
                if (next.getStartNode().equals(link.getEndNode()))
                {
                    if (isOnRoad(next, roadIds.get(roadIndex)))
                    {
                        downstream(next, roadIndex, roadIds, nodes);
                        return;
                    }
                    else if (roadIndex < roadIds.size() - 1 && isOnRoad(next, roadIds.get(roadIndex + 1)))
                    {
                        downstream(next, roadIndex + 1, roadIds, nodes);
                        return;
                    }
                }
            }
            if (roadIndex != roadIds.size() - 1)
            {
                throw new RuntimeException("Road ids " + roadIds + " are not of consecutive roads. The route is ill defined.");
            }
            nodes.add(link.getEndNode());
        }
    }

}
