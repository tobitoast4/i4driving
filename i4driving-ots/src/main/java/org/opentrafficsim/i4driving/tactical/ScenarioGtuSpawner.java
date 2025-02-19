package org.opentrafficsim.i4driving.tactical;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Throw;
import org.djutils.immutablecollections.ImmutableMap.ImmutableEntry;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterType;
import org.opentrafficsim.core.definitions.Defaults;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.gtu.GtuCharacteristics;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.Node;
import org.opentrafficsim.i4driving.messages.Commands.GenerationInfo;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlanner;
import org.opentrafficsim.road.gtu.strategical.LaneBasedStrategicalRoutePlannerFactory;
import org.opentrafficsim.road.network.RoadNetwork;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;
import org.opentrafficsim.road.network.lane.LanePosition;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.jstats.streams.StreamInterface;

/**
 * This class spawns a GTU based on scenario information.
 * @author wjschakel
 */
public class ScenarioGtuSpawner
{

    /** Network. */
    private final RoadNetwork network;

    /** Generation info. */
    private final GenerationInfo generationInfo;

    /** GTU id. */
    private final String gtuId;

    /** Strategical factory. */
    private final LaneBasedStrategicalRoutePlannerFactory strategicalFactory;

    /** GTU. */
    private LaneBasedGtu gtu;

    /**
     * Constructor.
     * @param network network.
     * @param gtuId GTU id
     * @param generationInfo generation info
     * @param strategicalFactory strategical planner, may be {@code null} if no generation info is provided
     */
    public ScenarioGtuSpawner(final RoadNetwork network, final String gtuId, final GenerationInfo generationInfo,
            final LaneBasedStrategicalRoutePlannerFactory strategicalFactory)
    {
        this.network = network;
        this.generationInfo = generationInfo;
        this.gtuId = gtuId;
        this.strategicalFactory = strategicalFactory;
        if (generationInfo != null)
        {
            network.getSimulator().scheduleEventAbsTime(generationInfo.getTime(), this, "generateGtu", new Object[0]);
            Throw.whenNull(strategicalFactory, "Strategical factory may not be null when generation info is provided.");
        }
    }

    /**
     * Generates the GTU.
     * @throws GtuException
     * @throws OtsGeometryException
     * @throws NetworkException
     * @throws SimRuntimeException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws ParameterException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @SuppressWarnings({"unused", "unchecked"}) // scheduled
    private void generateGtu()
            throws GtuException, SimRuntimeException, NetworkException, OtsGeometryException, ClassNotFoundException,
            NoSuchFieldException, SecurityException, ParameterException, IllegalArgumentException, IllegalAccessException
    {
        // GTU type and characteristics
        GtuType gtuType = Defaults.getByName(GtuType.class, "NL." + this.generationInfo.getGtuType());
        StreamInterface randomStream = this.network.getSimulator().getModel().getStream("generation");
        // TODO: characteristics in generationInfo (length, width, max speed, front, max accel, max decel)
        GtuCharacteristics gtuCharacteristics = GtuType.defaultCharacteristics(gtuType, this.network, randomStream);
        this.gtu = new LaneBasedGtu(this.gtuId, gtuType, gtuCharacteristics.getLength(), gtuCharacteristics.getWidth(),
                gtuCharacteristics.getMaximumSpeed(), gtuCharacteristics.getFront(), this.network);
        this.gtu.setMaximumAcceleration(gtuCharacteristics.getMaximumAcceleration());
        this.gtu.setMaximumDeceleration(gtuCharacteristics.getMaximumDeceleration());
        this.gtu.setNoLaneChangeDistance(Length.instantiateSI(1.0));

        // position
        String linkId = this.generationInfo.getInitialPosition().getLink();
        String laneId = this.generationInfo.getInitialPosition().getLane();
        Length x = this.generationInfo.getInitialPosition().getX();
        CrossSectionLink link = (CrossSectionLink) this.network.getLink(linkId);
        Lane lane = null;
        for (Lane laneIter : link.getLanes())
        {
            if (laneIter.getId().equals(laneId))
            {
                lane = laneIter;
                break;
            }
        }
        Throw.when(lane == null, NoSuchElementException.class, "Lane %s is not present in link %s.", laneId, linkId);
        LanePosition position = new LanePosition(lane, x);

        // strategical planner (also sets default parameters)
        Node destination = this.network.getNode(this.generationInfo.getDestination());
        LaneBasedStrategicalRoutePlanner strategicalPlanner = this.strategicalFactory.create(this.gtu, null, null, destination);

        // parameters
        for (ImmutableEntry<String, String> paramEntry : this.generationInfo.getParameters().entrySet())
        {
            int dot = paramEntry.getKey().lastIndexOf(".");
            String paramClass = paramEntry.getKey().substring(0, dot);
            String paramField = paramEntry.getKey().substring(dot + 1);
            Class<?> clazz = Class.forName(paramClass);
            Field field = clazz.getDeclaredField(paramField);
            ParameterType<?> parameterType = (ParameterType<?>) field.get(null);
            if (parameterType.getValueClass().equals(Acceleration.class))
            {
                this.gtu.getParameters().setParameter((ParameterType<Acceleration>) parameterType,
                        Acceleration.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getValueClass().equals(Duration.class))
            {
                this.gtu.getParameters().setParameter((ParameterType<Duration>) parameterType,
                        Duration.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getValueClass().equals(Length.class))
            {
                this.gtu.getParameters().setParameter((ParameterType<Length>) parameterType,
                        Length.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getValueClass().equals(Speed.class))
            {
                this.gtu.getParameters().setParameter((ParameterType<Speed>) parameterType,
                        Speed.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getValueClass().equals(Time.class))
            {
                this.gtu.getParameters().setParameter((ParameterType<Time>) parameterType, Time.valueOf(paramEntry.getValue()));
            }
            else if (parameterType.getValueClass().equals(Double.class))
            {
                this.gtu.getParameters().setParameter((ParameterType<Double>) parameterType,
                        Double.valueOf(paramEntry.getValue()));
            }
            else
            {
                throw new RuntimeException("Unable to process parameter with type " + parameterType.getValueClass());
            }
        }

        this.gtu.init(strategicalPlanner, position, this.generationInfo.getInitialSpeed());
    }

    /**
     * Retrieves the GTU from the network and remembers it for later use.
     * @return GTU.
     */
    public LaneBasedGtu getGtu()
    {
        if (this.gtu == null)
        {
            this.gtu = (LaneBasedGtu) this.network.getGTU(this.gtuId);
        }
        Throw.when(this.gtu == null, IllegalStateException.class, "GTU %s could not be found.", this.gtuId);
        return this.gtu;
    }

    /**
     * Returns the GTU id.
     * @return GTU id.
     */
    public String getGtuId()
    {
        return this.gtuId;
    }

    @Override
    public String toString()
    {
        return "ScenarioGtuSpawner [gtuId=" + gtuId + "]";
    }
    
}
