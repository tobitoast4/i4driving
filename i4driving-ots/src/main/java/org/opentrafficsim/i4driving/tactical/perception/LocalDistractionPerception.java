package org.opentrafficsim.i4driving.tactical.perception;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeLength;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.gtu.perception.AbstractPerceptionCategory;
import org.opentrafficsim.core.network.LateralDirectionality;
import org.opentrafficsim.i4driving.object.LocalDistraction;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.gtu.lane.perception.LaneBasedObjectIterable;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;
import org.opentrafficsim.road.gtu.lane.perception.PerceptionCollectable;
import org.opentrafficsim.road.gtu.lane.perception.RelativeLane;
import org.opentrafficsim.road.gtu.lane.perception.headway.AbstractHeadway;

/**
 * Perception category for distractions.
 * @author wjschakel
 */
public class LocalDistractionPerception extends AbstractPerceptionCategory<LaneBasedGtu, LanePerception>
{

    /** */
    private static final long serialVersionUID = 20250617L;

    /** Look-ahead distance. */
    public static final ParameterTypeLength LOOKAHEAD = ParameterTypes.LOOKAHEAD;

    /**
     * Constructor.
     * @param perception perception.
     */
    public LocalDistractionPerception(final LanePerception perception)
    {
        super(perception);
    }

    /**
     * Return active modes.
     * @return active modes.
     * @throws ParameterException on missing parameter
     */
    public PerceptionCollectable<LocalDistractionHeadway, LocalDistraction> getActiveModes() throws ParameterException
    {
        Parameters parameters = getGtu().getParameters();
        Length position = Try.assign(() -> getGtu().getReferencePosition().position(), "No valid reference position.");
        Length maxDistance = parameters.getParameter(LOOKAHEAD);

        return new LaneBasedObjectIterable<LocalDistractionHeadway, LocalDistraction>(getGtu(), LocalDistraction.class,
                LocalDistractionPerception.this.getPerception().getLaneStructure().getRootRecord(RelativeLane.CURRENT),
                position, true, maxDistance, getGtu().getFront(), getGtu().getStrategicalPlanner().getRoute())
        {
            @Override
            protected LocalDistractionHeadway perceive(final LaneBasedGtu perceivingGtu, final LocalDistraction distraction,
                    final Length distance) throws GtuException, ParameterException
            {
                return new LocalDistractionHeadway(distance, distraction);
            }
        };
    }

    /**
     * Information on distraction.
     */
    public class LocalDistractionHeadway extends AbstractHeadway
    {

        /**  */
        private static final long serialVersionUID = 20250617L;

        /** Distraction. */
        private final LocalDistraction distraction;

        /**
         * Constructor.
         * @param distance distance
         * @param distraction distraction
         */
        public LocalDistractionHeadway(final Length distance, final LocalDistraction distraction) throws GtuException
        {
            super(distance);
            this.distraction = distraction;
        }

        @Override
        public String getId()
        {
            return this.distraction.getFullId();
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
         * Returns the range of the distraction, which applies upstream of the location.
         * @return range of the distraction
         */
        public Length getRange()
        {
            return distraction.getRange();
        }

        /**
         * Returns the distraction level as normalized task demand.
         * @return distraction level
         */
        public double getDistractionLevel()
        {
            return distraction.getDistractionLevel();
        }

        /**
         * Returns the side of the distraction, relative to the driving direction.
         * @return side of the distraction
         */
        public LateralDirectionality getSide()
        {
            return distraction.getSide();
        }

    }

}
