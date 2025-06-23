package org.opentrafficsim.i4driving.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.draw.ColorInterpolator;
import org.opentrafficsim.road.gtu.lane.perception.mental.Fuller;

/**
 * Task saturation colorer.
 * @author wjschakel
 */
public class TaskSaturationChannelColorer implements GtuColorer
{

    /** Full. */
    static final Color MAX = Color.RED;

    /** Medium. */
    static final Color MID = Color.YELLOW;

    /** Zero. */
    static final Color SUBCRIT = Color.GREEN;

    /** Not available. */
    static final Color NA = Color.WHITE;

    /** Legend. */
    static final List<LegendEntry> LEGEND;

    static
    {
        LEGEND = new ArrayList<>();
        LEGEND.add(new LegendEntry(SUBCRIT, "sub-critical", "sub-critical task saturation"));
        LEGEND.add(new LegendEntry(MID, "1.5", "1.5 task saturation"));
        LEGEND.add(new LegendEntry(MAX, "3.0", "3.0 or larger"));
        LEGEND.add(new LegendEntry(NA, "N/A", "N/A"));
    }

    /** {@inheritDoc} */
    @Override
    public Color getColor(final Gtu gtu)
    {
        Double ts = gtu.getParameters().getParameterOrNull(Fuller.TS);
        if (ts == null)
        {
            return NA;
        }
        if (ts <= 1.0)
        {
            return SUBCRIT;
        }
        else if (ts > 3.0)
        {
            return MAX;
        }
        else if (ts < 1.5)
        {
            return ColorInterpolator.interpolateColor(SUBCRIT, MID, (ts - 1.0) / 0.5);
        }
        return ColorInterpolator.interpolateColor(MID, MAX, (ts - 1.5) / 1.5);
    }

    /** {@inheritDoc} */
    @Override
    public List<LegendEntry> getLegend()
    {
        return LEGEND;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Task saturation";
    }

}
