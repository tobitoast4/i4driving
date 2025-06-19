package org.opentrafficsim.i4driving.tactical.perception;

import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.exceptions.Throw;
import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDouble;
import org.opentrafficsim.base.parameters.ParameterTypeDuration;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.base.parameters.constraint.NumericConstraint;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller.BehavioralAdaptation;

/**
 * Behavioral adaptation which increases the update time (time step) for lower levels of attention (maximum in steady-state in
 * the Attention Matrix).
 * @author wjschakel
 */
public class AdaptationUpdateTime implements BehavioralAdaptation
{

    /** Update time. */
    public static final ParameterTypeDuration DT = ParameterTypes.DT;

    /** Minimum update time. */
    public static final ParameterTypeDuration DT_MIN = new ParameterTypeDuration("dt_min", "Minimum update time.",
            Duration.instantiateSI(0.0), NumericConstraint.POSITIVEZERO)
    {
        /** */
        private static final long serialVersionUID = 20250616L;

        @Override
        public void check(final Duration value, final Parameters params) throws ParameterException
        {
            Duration dtMax = params.getParameterOrNull(DT_MAX);
            Throw.when(dtMax != null && value.si >= dtMax.si, ParameterException.class,
                    "Value of DT_MIN is above or equal to DT_MAX");
        }
    };

    /** Minimum update time. */
    public static final ParameterTypeDuration DT_MAX =
            new ParameterTypeDuration("dt_max", "Maximum update time.", Duration.instantiateSI(3.0), NumericConstraint.POSITIVE)
            {
                /** */
                private static final long serialVersionUID = 20250616L;

                @Override
                public void check(final Duration value, final Parameters params) throws ParameterException
                {
                    Duration dtMin = params.getParameterOrNull(DT_MIN);
                    Throw.when(dtMin != null && value.si <= dtMin.si, ParameterException.class,
                            "Value of DT_MAX is below or equal to DT_MIN");
                }
            };

    /** Level of attention, which is the maximum in the steady state of the Attention Matrix. */
    public static final ParameterTypeDouble ATT = ChannelFuller.ATT;

    @Override
    public void adapt(final Parameters parameters, final double taskSaturation) throws ParameterException
    {
        parameters.setParameter(DT, Duration.interpolate(parameters.getParameter(DT_MAX), parameters.getParameter(DT_MIN),
                parameters.getParameter(ATT)));
    }

}
