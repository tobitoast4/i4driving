package org.opentrafficsim.i4driving.sim0mq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import org.djunits.value.vdouble.scalar.Frequency;
import org.djunits.value.vdouble.scalar.Time;
import org.djunits.value.vdouble.vector.FrequencyVector;
import org.djunits.value.vdouble.vector.TimeVector;
import org.djutils.exceptions.Throw;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.i4driving.messages.adapters.ScalarAsListAdapter;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.od.Categorization;
import org.opentrafficsim.road.od.Category;
import org.opentrafficsim.road.od.Interpolation;
import org.opentrafficsim.road.od.OdMatrix;

import com.google.gson.annotations.JsonAdapter;

/**
 * OD matrix information in a sim0mq context.
 */
public class OdMatrixJson
{

    /** Categorization. */
    @JsonAdapter(ScalarAsListAdapter.class)
    private List<CategoryElement> categorization = new ArrayList<>();

    /** Global time. */
    @JsonAdapter(ScalarAsListAdapter.class)
    private List<Time> globalTime = new ArrayList<>();

    /** Global interpolation. */
    private Interpolation globalInterpolation = Interpolation.LINEAR;

    /** Demand values. */
    private List<DemandJson> demand = new ArrayList<>();

    /**
     * Enum for category elements.
     */
    private enum CategoryElement
    {
        /** GTU type. */
        GTU_TYPE(GtuType.class, (objData, id) -> objData.gtuTypeMap().get(id)),

        /** Route. */
        ROUTE(Route.class, (objData, id) -> objData.routeMap().get(id));

        /** The class that represents the category element. */
        private final Class<?> typeClass;

        /** Function to obtain object. */
        private final BiFunction<ObjectData, String, Object> objectFunction;

        /**
         * Constructor.
         * @param typeClass class that represents the category element
         * @param objectFunction function to obtain object
         */
        CategoryElement(final Class<?> typeClass, final BiFunction<ObjectData, String, Object> objectFunction)
        {
            this.typeClass = typeClass;
            this.objectFunction = objectFunction;
        }
    }

    /**
     * Demand data between origin and destination for a category.
     */
    private static final class DemandJson
    {
        /** Origin. */
        private String origin;

        /** Whether the origin is defined in the design direction. */
        private Boolean originInDesignDirection;

        /** Destination. */
        private String destination;

        /** Whether the destination is defined in the design direction. */
        private Boolean destinationInDesignDirection;

        /** Category, defined as a list of element ids. */
        @JsonAdapter(ScalarAsListAdapter.class)
        private List<String> category = new ArrayList<>();

        /** Time. */
        @JsonAdapter(ScalarAsListAdapter.class)
        private List<Time> time;

        /** Frequency. */
        @JsonAdapter(ScalarAsListAdapter.class)
        private List<Frequency> frequency = new ArrayList<>();

        /** Interpolation. */
        private Interpolation interpolation;
    }

    /**
     * Record containing routes and GTU types by id to build up categories.
     * @param routeMap routes per id
     * @param gtuTypeMap GTU types per id
     */
    private record ObjectData(Map<String, Route> routeMap, Map<String, GtuType> gtuTypeMap)
    {
    }

    /**
     * Returns an {@code OdMatrix} representation of the data in this {@code OdMatrix0mq}.
     * @param network network
     * @param gtuTypes GTU types
     * @param simulation simulation
     * @return {@code OdMatrix} representation of the data in this {@code OdMatrix0mq}
     */
    public OdMatrix asOdMatrix(final RoadNetwork network, final Collection<GtuType> gtuTypes, final Sim0mqSimulation simulation)
    {
        // Origins and destinations
        List<Node> origins = new ArrayList<>();
        List<Node> destinations = new ArrayList<>();
        for (DemandJson demand0 : this.demand)
        {
            Node origin = network.getNode(simulation.getOrigin(demand0.origin, demand0.originInDesignDirection));
            Node destination =
                    network.getNode(simulation.getDestination(demand0.destination, demand0.destinationInDesignDirection));
            Throw.whenNull(origin, "Origin %s is not in the network.", demand0.origin);
            Throw.whenNull(destination, "Destination %s is not in the network.", demand0.destination);
            origins.add(origin);
            destinations.add(destination);
        }

        // Categorization
        Categorization categorization0;
        if (this.categorization.isEmpty())
        {
            categorization0 = Categorization.UNCATEGORIZED;
        }
        else
        {
            Class<?> first = this.categorization.get(0).typeClass;
            Class<?>[] theRest = new Class<?>[this.categorization.size() - 1];
            for (int i = 1; i < this.categorization.size(); i++)
            {
                theRest[i - 1] = this.categorization.get(i).typeClass;
            }
            categorization0 = new Categorization(UUID.randomUUID().toString(), first, theRest);
        }

        // Global time
        List<Time> globalTime0 = new ArrayList<>(this.globalTime);
        List<Time> bareGlobalTime = new ArrayList<>(globalTime0); // Unfixed time so we can fix it together with specific demand
        Collections.sort(new ArrayList<>(globalTime0));
        Throw.when(!globalTime0.equals(this.globalTime), IllegalArgumentException.class, "Global time is not sorted.");
        fixTime(globalTime0, null, network);
        TimeVector globalTimeVector = new TimeVector(globalTime0);

        // Create OD
        OdMatrix od = new OdMatrix(UUID.randomUUID().toString(), origins, destinations, categorization0, globalTimeVector,
                this.globalInterpolation);

        // Object data for categories
        Map<String, GtuType> gtuTypeMap = new LinkedHashMap<>();
        gtuTypes.forEach((gtuType) -> gtuTypeMap.put(gtuType.getId(), gtuType));
        Map<String, Route> routeMap = new LinkedHashMap<>();
        network.getRouteMap().values().forEach((map) -> map.values().forEach((route) -> routeMap.put(route.getId(), route)));
        ObjectData objectData = new ObjectData(routeMap, gtuTypeMap);

        // Demand
        for (DemandJson demand0 : this.demand)
        {
            // Nodes
            Node origin = network.getNode(simulation.getOrigin(demand0.origin, demand0.originInDesignDirection));
            Node destination =
                    network.getNode(simulation.getDestination(demand0.destination, demand0.destinationInDesignDirection));

            // Category
            Category category;
            if (categorization0.equals(Categorization.UNCATEGORIZED))
            {
                category = Category.UNCATEGORIZED;
            }
            else
            {
                Object first = this.categorization.get(0).objectFunction.apply(objectData, demand0.category.get(0));
                Object[] theRest = new Object[this.categorization.size() - 1];
                for (int i = 1; i < this.categorization.size(); i++)
                {
                    String id = demand0.category.get(i);
                    CategoryElement catElement = this.categorization.get(i);
                    Object obj = catElement.objectFunction.apply(objectData, id);
                    theRest[i - 1] = Throw.whenNull(obj, "Object with id %s is not present as %s.", id, catElement);
                }
                category = new Category(categorization0, first, theRest);
            }

            // Time and frequency
            List<Time> timeList = demand0.time == null ? new ArrayList<>(bareGlobalTime) : new ArrayList<>(demand0.time);
            List<Frequency> frequencyList = new ArrayList<>(demand0.frequency);
            fixTime(timeList, frequencyList, network);
            Throw.when(timeList.size() != frequencyList.size(), IllegalArgumentException.class,
                    "Time and frequency for demand between %s and %s for category %s are not of equal length.", demand0.origin,
                    demand0.destination, demand0.category);
            TimeVector timeVector = new TimeVector(timeList);
            FrequencyVector frequency = new FrequencyVector(frequencyList);

            // Interpolation
            Interpolation interpolation0 = demand0.interpolation == null ? this.globalInterpolation : demand0.interpolation;

            // Put demand
            od.putDemandVector(origin, destination, category, frequency, timeVector, interpolation0);
        }
        return od;
    }

    /**
     * Makes sure the time contains simulation bounds, and optionally amends frequency data with constant demand.
     * @param time time
     * @param frequency frequency, may be {@code null}
     * @param network network
     */
    private void fixTime(final List<Time> time, final List<Frequency> frequency, final RoadNetwork network)
    {
        if (time.isEmpty() || time.get(0).gt0())
        {
            time.add(0, Time.ZERO);
            if (frequency != null && !frequency.isEmpty() && frequency.size() < time.size())
            {
                frequency.add(0, frequency.get(0));
            }
        }
        if (time.get(time.size()-1).si < network.getSimulator().getReplication().getRunLength().si)
        {
            time.add(Time.instantiateSI(network.getSimulator().getReplication().getRunLength().si));
            if (frequency != null && !frequency.isEmpty())
            {
                frequency.add(frequency.get(time.size()-1));
            }
        }
    }

}
