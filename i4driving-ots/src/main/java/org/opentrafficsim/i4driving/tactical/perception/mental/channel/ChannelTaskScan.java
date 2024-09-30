package org.opentrafficsim.i4driving.tactical.perception.mental.channel;

import java.util.Set;
import java.util.function.Function;

import org.djutils.exceptions.Try;
import org.opentrafficsim.base.parameters.ParameterTypeDouble;
import org.opentrafficsim.base.parameters.constraint.NumericConstraint;
import org.opentrafficsim.road.gtu.lane.perception.LanePerception;

/**
 * Task demand for scanning. This is a constant value.
 * @author wjschakel
 */
public class ChannelTaskScan implements ChannelTask
{

    /** Scanning task demand. */
    public static final ParameterTypeDouble TDSCAN =
            new ParameterTypeDouble("td_scan", "Scanning task demand", 0.1, NumericConstraint.POSITIVEZERO);

    /** Standard set of left, right, front and rear scan task. */
    private final static Set<ChannelTask> SET = Set.of(new ChannelTaskScan(LEFT), new ChannelTaskScan(RIGHT),
            new ChannelTaskScan(FRONT), new ChannelTaskScan(REAR));

    /** Standard supplier that supplies instances for left, right, front and rear scan task. */
    public final static Function<LanePerception, Set<ChannelTask>> SUPPLIER = (p) -> SET;

    /** Channel. */
    private final Object channel;

    /**
     * Constructor.
     * @param channel channel.
     */
    public ChannelTaskScan(final Object channel)
    {
        this.channel = channel;
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return String.format("scan (%s)", this.channel);
    }

    /** {@inheritDoc} */
    @Override
    public Object getChannel()
    {
        return this.channel;
    }

    /** {@inheritDoc} */
    @Override
    public double getDemand(final LanePerception perception)
    {
        return Try.assign(() -> perception.getGtu().getParameters().getParameter(TDSCAN), "Parameter td_scan not available.");
    }

}
