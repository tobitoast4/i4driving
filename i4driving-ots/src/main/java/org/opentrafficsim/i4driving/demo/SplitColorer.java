package org.opentrafficsim.i4driving.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.opentrafficsim.animation.gtu.colorer.GtuColorer;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.gtu.Gtu;
import org.opentrafficsim.core.gtu.GtuException;
import org.opentrafficsim.core.network.Link;
import org.opentrafficsim.core.network.NetworkException;
import org.opentrafficsim.core.network.route.Route;
import org.opentrafficsim.road.gtu.lane.LaneBasedGtu;
import org.opentrafficsim.road.network.lane.LanePosition;

/**
 * This class is a temporary fix for as long as we use OTS 1.7.5, solving a bug in the SplitColorer. This has been solved in OTS
 * and will be published at a later stage.
 * @author wjschakel
 */
@Deprecated // to be removed when a later version of OTS is used
final class SplitColorer implements GtuColorer
{

    /** Left color. */
    static final Color LEFT = Color.GREEN;

    /** Other color. */
    static final Color OTHER = Color.BLUE;

    /** Right color. */
    static final Color RIGHT = Color.RED;

    /** Unknown color. */
    static final Color UNKNOWN = Color.WHITE;

    /** The legend. */
    private static final List<LegendEntry> LEGEND;

    static
    {
        LEGEND = new ArrayList<>(4);
        LEGEND.add(new LegendEntry(LEFT, "Left", "Left"));
        LEGEND.add(new LegendEntry(RIGHT, "Right", "Right"));
        LEGEND.add(new LegendEntry(OTHER, "Other", "Other"));
        LEGEND.add(new LegendEntry(UNKNOWN, "Unknown", "Unknown"));
    }

    @Override
    public Color getColor(final Gtu gtu)
    {
        if (!(gtu instanceof LaneBasedGtu))
        {
            return UNKNOWN;
        }
        LaneBasedGtu laneGtu = (LaneBasedGtu) gtu;
        LanePosition refPos;
        try
        {
            refPos = laneGtu.getReferencePosition();
        }
        catch (GtuException exception)
        {
            return UNKNOWN;
        }
        Link link = refPos.lane().getLink();
        Route route = laneGtu.getStrategicalPlanner().getRoute();
        if (route == null)
        {
            return UNKNOWN;
        }

        // get all links we can go in to
        Set<Link> nextLinks;
        Link preLink;
        do
        {
            try
            {
                preLink = link;
                nextLinks = link.getEndNode().nextLinks(gtu.getType(), link);
                if (!nextLinks.isEmpty())
                {
                    link = laneGtu.getStrategicalPlanner().nextLink(preLink, gtu.getType());
                }
            }
            catch (NetworkException exception)
            {
                return UNKNOWN;
            }
        }
        while (nextLinks.size() == 1);

        // dead end
        if (nextLinks.isEmpty())
        {
            return UNKNOWN;
        }

        // split
        try
        {
            double preAngle = preLink.getDesignLine().getLocationFraction(1.0).getDirZ();
            double angleLeft = 0.0;
            double angleRight = 0.0;
            Link linkLeft = null;
            Link linkRight = null;
            for (Link nextLink : nextLinks)
            {
                double angle = nextLink.getStartNode().equals(link.getStartNode())
                        ? nextLink.getDesignLine().getLocationFraction(0.0).getDirZ()
                        : nextLink.getDesignLine().getLocationFraction(1.0).getDirZ() + Math.PI;
                angle -= preAngle; // difference with from
                while (angle < -Math.PI)
                {
                    angle += Math.PI * 2;
                }
                while (angle > Math.PI)
                {
                    angle -= Math.PI * 2;
                }
                if (angle < angleRight)
                {
                    angleRight = angle;
                    linkRight = nextLink;
                }
                else if (angle > angleLeft)
                {
                    angleLeft = angle;
                    linkLeft = nextLink;
                }
            }
            if (link.equals(linkRight))
            {
                return RIGHT;
            }
            else if (link.equals(linkLeft))
            {
                return LEFT;
            }
            return OTHER;
        }
        catch (OtsGeometryException exception)
        {
            // should not happen as the fractions are 0.0 and 1.0
            throw new RuntimeException("Angle could not be calculated.", exception);
        }
    }

    @Override
    public List<LegendEntry> getLegend()
    {
        return LEGEND;
    }

    @Override
    public String toString()
    {
        return "Split";
    }

}
