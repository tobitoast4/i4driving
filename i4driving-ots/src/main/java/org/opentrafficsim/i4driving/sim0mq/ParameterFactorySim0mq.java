package org.opentrafficsim.i4driving.sim0mq;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.opentrafficsim.base.parameters.ParameterException;
import org.opentrafficsim.base.parameters.ParameterType;
import org.opentrafficsim.base.parameters.Parameters;
import org.opentrafficsim.core.gtu.GtuType;
import org.opentrafficsim.core.parameters.ParameterFactoryByType;


/**
 * Extension of parameter factory that allows temporary setting and resetting of parameter drawn.
 * @author wjschakel
 */
public class ParameterFactorySim0mq extends ParameterFactoryByType
{

    /** Overwriting parameters. */
    private final Map<ParameterType<?>, Object> values = new LinkedHashMap<>();
    
    /**
     * Sets overwriting parameter value.
     * @param <T> value type
     * @param parameter parameter
     * @param value value
     */
    public <T> void setParameterValue(final ParameterType<T> parameter, final T value)
    {
        this.values.put(parameter, value);
    }
    
    /**
     * Clears overwriting parameter value.
     * @param parameter parameter
     */
    public void clearParameterValue(final ParameterType<?> parameter)
    {
        this.values.remove(parameter);
    }
    
    @Override
    public void setValues(final Parameters parameters, final GtuType gtuType) throws ParameterException
    {
        super.setValues(parameters, gtuType);
        setValues(parameters);
    }
    
    @SuppressWarnings("unchecked")
    private <T> void setValues(final Parameters parameters) throws ParameterException
    {
        for (Entry<ParameterType<?>, Object> entry : this.values.entrySet())
        {
            parameters.setParameter((ParameterType<T>) entry.getKey(), (T) entry.getValue());
        }
    }
    
}
