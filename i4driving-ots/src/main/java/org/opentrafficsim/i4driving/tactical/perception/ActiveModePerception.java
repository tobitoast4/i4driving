package org.opentrafficsim.i4driving.tactical.perception;

import java.util.SortedSet;
import java.util.TreeSet;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDouble;
import org.opentrafficsim.base.parameters.ParameterTypeDuration;
import org.opentrafficsim.base.parameters.ParameterTypeLength;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.perception.AbstractPerceptionCategory;
import org.opentrafficsim.i4driving.object.ActiveModeCrossing;
import org.opentrafficsim.i4driving.object.ActiveModeCrossing.ActiveModeArrival;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LaneBasedObjectIterable;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.categories.neighbors.Estimation;
import org.opentrafficsim.road.gtu.lane.perception.headway.AbstractHeadway;

/**
 * Perception of ActiveModeCrossing.
 * @author wjschakel
 */
public class ActiveModePerception extends AbstractPerceptionCategory<LaneBasedGtu, LanePerception>
{

    /** Perception delay. */
    public static final ParameterTypeDuration TR = ParameterTypes.TR;

    /** Over estimation. */
    public static final ParameterTypeDouble OVER_EST = Estimation.OVER_EST;

    /** Look-ahead distance. */
    public static final ParameterTypeLength LOOKAHEAD = ParameterTypes.LOOKAHEAD;

    /** Erroneous estimation factor on distance and speed difference. */
    public static final ParameterTypeDouble EST_FACTOR = ChannelFuller.EST_FACTOR;

    /** */
    private static final long serialVersionUID = 20250515L;

    /**
     * Constructor.
     * @param perception perception
     */
    public ActiveModePerception(final LanePerception perception)
    {
        super(perception);
    }

    /**
     * Return active modes.
     * @return active modes.
     * @throws ParameterException on missing parameter
     */
    public PerceptionCollectable<ActiveModeCrossingHeadway, ActiveModeCrossing> getActiveModes() throws ParameterException
    {
        Parameters parameters = getGtu().getParameters();
        double factor = parameters.getParameter(EST_FACTOR);
        Duration delay = parameters.getParameter(TR);
        Time when = getGtu().getSimulator().getSimulatorAbsTime().minus(delay);

        Length position = Try.assign(() -> getGtu().getReferencePosition().position(), "No valid reference position.");
        Length maxDistance = parameters.getParameter(LOOKAHEAD);

        return new LaneBasedObjectIterable<ActiveModeCrossingHeadway, ActiveModeCrossing>(getGtu(), ActiveModeCrossing.class,
                ActiveModePerception.this.getPerception().getLaneStructure().getRootRecord(RelativeLane.CURRENT), position,
                true, maxDistance, getGtu().getFront(), getGtu().getStrategicalPlanner().getRoute())
        {
            @Override
            protected ActiveModeCrossingHeadway perceive(final LaneBasedGtu perceivingGtu, final ActiveModeCrossing crossing,
                    final Length distance) throws GtuException, ParameterException
            {
                SortedSet<ActiveModeArrival> out = new TreeSet<>();
                for (ActiveModeArrival arrival : crossing.getArrivals(when))
                {
                    Speed estimatedSpeed = arrival.speed().times(factor);
                    Length anticipatedDistanceToCrossing =
                            Length.max(Length.ZERO, arrival.distance().times(factor).minus(estimatedSpeed.times(delay)));
                    out.add(new ActiveModeArrival(anticipatedDistanceToCrossing, estimatedSpeed));
                }
                return new ActiveModeCrossingHeadway(crossing.getId(), distance.times(factor), out);
            }
        };
    }

    /**
     * Information on crossing active mode.
     */
    public class ActiveModeCrossingHeadway extends AbstractHeadway
    {

        /**  */
        private static final long serialVersionUID = 20250515L;

        /** Id. */
        private final String id;

        /** Arrival time. */
        private SortedSet<ActiveModeArrival> activeModeArrivals;

        /**
         * Constructor.
         * @param id id
         * @param distance distance
         * @param activeModes active mode arrivals
         * @throws GtuException
         */
        public ActiveModeCrossingHeadway(final String id, final Length distance, final SortedSet<ActiveModeArrival> activeModes)
                throws GtuException
        {
            super(distance);
            this.id = id;
            this.activeModeArrivals = activeModes;
        }

        @Override
        public String getId()
        {
            return this.id;
        }

        @Override
        public Length getLength()
        {
            return Length.ZERO;
        }

        @Override
        public Speed getSpeed()
        {
            return Speed.ZERO;
        }

        @Override
        public ObjectType getObjectType()
        {
            return ObjectType.OBJECT;
        }

        @Override
        public Acceleration getAcceleration()
        {
            return Acceleration.ZERO;
        }

        /**
         * Returns the active mode arrivals.
         * @return active mode arrivals
         */
        public SortedSet<ActiveModeArrival> getActiveModeArrivals()
        {
            return this.activeModeArrivals;
        }

    }

}
