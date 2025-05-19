package org.opentrafficsim.i4driving.tactical.perception;

import java.util.function.Supplier;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.exceptions.Throw;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.road.gtu.lane.perception.headway.AbstractHeadwayLaneBasedObject;
import org.opentrafficsim.road.network.lane.object.trafficlight.TrafficLight;
import org.opentrafficsim.road.network.lane.object.trafficlight.TrafficLightColor;

/**
 * Traffic light perceived through perception channel.
 * @author wjschakel
 */
public class HeadwayTrafficLightChannel extends AbstractHeadwayLaneBasedObject // TODO: implements HeadwayTrafficLight
{

    /** */
    private static final long serialVersionUID = 20240925L;

    /** the traffic light object for further observation, can not be null. */
    private final TrafficLight trafficLight;

    /** Whether we can turn on red. */
    private final boolean turnOnRed;

    /** Perception delay supplier. */
    private final Supplier<Duration> perceptionDelay;

    /** Simulator. */
    private final OtsSimulatorInterface simulator;

    /**
     * Construct a new Headway information object, for a traffic light ahead of us (or behind us, although that does not seem
     * very useful).
     * @param trafficLight the traffic light object for further observation, can not be null.
     * @param distance the distance to the traffic light, distance cannot be null.
     * @param turnOnRed whether the perceiving GTU may turn on red.
     * @param perceptionDelay perception delay supplier.
     * @throws GtuException when id is null, or parameters are inconsistent
     */
    public HeadwayTrafficLightChannel(final TrafficLight trafficLight, final Length distance, final boolean turnOnRed,
            final Supplier<Duration> perceptionDelay) throws GtuException
    {
        super(ObjectType.TRAFFICLIGHT, id(trafficLight), distance, trafficLight.getLane());
        this.trafficLight = trafficLight;
        this.turnOnRed = turnOnRed;
        this.perceptionDelay = perceptionDelay;
        this.simulator = trafficLight.getLane().getNetwork().getSimulator();
    }

    /**
     * Get the id of the traffic light; throw an exception if traffic light is null.
     * @param trafficLight the traffic light object for further observation, can not be null.
     * @return he id of the traffic light.
     * @throws GtuException when the trafficLight object is null
     */
    private static String id(final TrafficLight trafficLight) throws GtuException
    {
        Throw.when(trafficLight == null, GtuException.class, "Headway constructor: trafficLight == null");
        return trafficLight.getId();
    }

    /**
     * Returns the traffic light color.
     * @return the traffic light color.
     */
    public final TrafficLightColor getTrafficLightColor()
    {
        // Time when = this.simulator.getSimulatorAbsTime().minus(this.perceptionDelay.get());
        // TODO: traffic light color is not yet historical
        return this.trafficLight.getTrafficLightColor();
    }

    /**
     * Whether the perceiving GTU may turn on red.
     * @return whether the perceiving GTU may turn on red.
     */
    public final boolean canTurnOnRed()
    {
        return this.turnOnRed;
    }

    /** {@inheritDoc} */
    @Override
    public final String toString()
    {
        return "HeadwayTrafficLightChannel [trafficLight=" + this.trafficLight + "]";
    }

}
