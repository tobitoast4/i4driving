package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterTypeDouble;
import org.opentrafficsim.base.parameters.ParameterTypes;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.road.gtu.lane.perception.mental.AdaptationSpeed;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller.BehavioralAdaptation;

/**
 * Reduces the desired speed as behavioral adaptation. The equation is v0 = v0_base * m(1, 1/(1+Beta_v0*(TS-1))).
 * @author wjschakel
 */
public class AdaptationSpeedChannel implements BehavioralAdaptation
{

    /** Parameter for desired speed scaling. */
    public static final ParameterTypeDouble BETA_V0 = AdaptationSpeed.BETA_V0;

    /** Base value for the desired speed. */
    private Double fSpeed0;

    /** {@inheritDoc} */
    @Override
    public void adapt(final Parameters parameters, final double taskSaturation) throws ParameterException
    {
        if (this.fSpeed0 == null)
        {
            this.fSpeed0 = parameters.getParameter(ParameterTypes.FSPEED);
        }
        double factor =
                Math.min(1.0, 1.0 / (1.0 + parameters.getParameter(BETA_V0) * (parameters.getParameter(Fuller.TS) - 1.0)));
        parameters.setParameter(ParameterTypes.FSPEED, this.fSpeed0 * factor);
    }

}
