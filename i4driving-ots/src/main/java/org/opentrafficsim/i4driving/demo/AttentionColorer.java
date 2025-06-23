package org.opentrafficsim.i4driving.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.draw.ColorInterpolator;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.ChannelFuller;

/**
 * Attention (maximum of channels) colorer.
 * @author wjschakel
 */
public class AttentionColorer implements GtuColorer
{

    /** Full. */
    static final Color MAX = Color.RED;

    /** Medium. */
    static final Color MID = Color.YELLOW;

    /** Zero. */
    static final Color MIN = Color.GREEN;

    /** Not available. */
    static final Color NA = Color.WHITE;

    /** Legend. */
    static final List<LegendEntry> LEGEND;

    static
    {
        LEGEND = new ArrayList<>();
        LEGEND.add(new LegendEntry(MIN, "0.0", "0.0 max attention"));
        LEGEND.add(new LegendEntry(MID, "0.5", "0.5 max attention"));
        LEGEND.add(new LegendEntry(MAX, "1.0", "1.0 max attention"));
        LEGEND.add(new LegendEntry(NA, "N/A", "N/A"));
    }

    @Override
    public Color getColor(final Gtu drawable)
    {
        Double attention = drawable.getParameters().getParameterOrNull(ChannelFuller.ATT);
        if (attention == null)
        {
            return NA;
        }
        if (attention < 0.5)
        {
            return ColorInterpolator.interpolateColor(MIN, MID, attention / 0.5);
        }
        return ColorInterpolator.interpolateColor(MID, MAX, (attention - 0.5) / 0.5);
    }

    @Override
    public List<LegendEntry> getLegend()
    {
        return LEGEND;
    }

    @Override
    public String toString()
    {
        return "Attention";
    }
    
}
