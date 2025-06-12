package org.opentrafficsim.i4driving.opendrive;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.cli.CliUtil;
import org.djutils.data.Column;
import org.djutils.data.ListTable;
import org.djutils.data.csv.CsvData;
import org.djutils.data.serialization.TextSerializationException;
import org.djutils.draw.line.PolyLine2d;
import org.djutils.draw.point.Point2d;
import org.opentrafficsim.core.dsol.OtsAnimator;
import org.opentrafficsim.core.geometry.OtsGeometryException;
import org.opentrafficsim.core.network.Link;
import org.opentrafficsim.draw.OtsDrawingException;
import org.opentrafficsim.road.network.lane.CrossSectionLink;
import org.opentrafficsim.road.network.lane.Lane;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.language.DsolException;
import picocli.CommandLine.Option;

/**
 * Derives all conflict coordinates, relevant for data processing for data produced with an experiment using an OpenDRIVE
 * network.
 * @author wjschakel
 */
public final class DeriveConflictCoordinates
{

    /** Folder. */
    @Option(names = "folder")
    private String folder;

    /** OpenDRIVE file. */
    @Option(names = "file")
    private String file;

    /**
     * Constructor.
     */
    private DeriveConflictCoordinates()
    {
        //
    }

    public static void main(final String[] args)
    {
        DeriveConflictCoordinates program = new DeriveConflictCoordinates();
        CliUtil.execute(program, args);
        try
        {
            getConflictData(program.folder, program.file);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Saves relevant data as csv, and prints Matlab code to plot it.
     * @param folder folder
     * @param file file
     * @throws SimRuntimeException
     * @throws NamingException
     * @throws DsolException
     * @throws OtsDrawingException
     * @throws OtsGeometryException
     * @throws IOException
     * @throws TextSerializationException
     */
    public static void getConflictData(final String folder, final String file) throws SimRuntimeException, NamingException,
            DsolException, OtsDrawingException, OtsGeometryException, IOException, TextSerializationException
    {
        String prefix = File.separator + file.replace(".xodr", "") + "_";

        OtsAnimator sim = new OtsAnimator("animator");
        OpenDriveModel model = new OpenDriveModel(sim, folder + File.separator + file);
        sim.initialize(Time.ZERO, Duration.ZERO, Duration.instantiateSI(3600.0), model);

        ListTable lanes = new ListTable("lanes", "Lane information", List.of(new Column<String>("id", "Lane id", String.class),
                new Column<String>("roadId", "Road id, or 'intersection' for lanes on intersections", String.class),
                new Column<String>("prev", "Previous lanes", String.class),
                new Column<String>("next", "Next lanes", String.class), new Column<String>("x", "X-coordinates", String.class),
                new Column<String>("y", "Y-coordinates", String.class)));
        ListTable conflicts = new ListTable("conflicts", "Conflict information",
                List.of(new Column<>("lane1Id", "Id of lane1", String.class),
                        new Column<>("lane2Id", "Id of lane2", String.class), new Column<>("x", "X-coordinate", Double.class),
                        new Column<>("y", "Y-coordinate", Double.class)));

        Set<String> roadIds = new LinkedHashSet<>();
        List<Lane> laneList = new ArrayList<>();
        for (Link link : model.getNetwork().getLinkMap().values())
        {
            String roadId;
            if (link.getId().contains("_"))
            {
                roadId = link.getId().substring(0, link.getId().indexOf("_"));
                if (roadIds.add(roadId))
                {
                    double x = link.getLocation().x;
                    double y = link.getLocation().y;
                    System.out.println(String.format("    text(%.3f, %.3f, \"%s\", \"Clipping\", \"on\");", x, y, roadId));
                }
            }
            else
            {
                roadId = "intersection";
            }

            for (Lane lane : ((CrossSectionLink) link).getLanes())
            {
                laneList.add(lane);
                List<Double> x = new ArrayList<>();
                List<Double> y = new ArrayList<>();
                for (Point2d point : lane.getCenterLine().getPoints())
                {
                    x.add(point.x);
                    y.add(point.y);
                }
                List<String> prev = new ArrayList<>();
                lane.prevLanes(null).forEach((l) -> prev.add(l.getFullId()));
                List<String> next = new ArrayList<>();
                lane.nextLanes(null).forEach((l) -> next.add(l.getFullId()));
                System.out.println(String.format("    plot(%s, %s);", x, y));
                lanes.addRow(
                        new Object[] {lane.getFullId(), roadId, prev.toString(), next.toString(), x.toString(), y.toString()});
            }
        }
        for (int i = 0; i < laneList.size() - 1; i++)
        {
            PolyLine2d line1 = laneList.get(i).getCenterLine().getLine2d();
            for (int j = i + 1; j < laneList.size(); j++)
            {
                PolyLine2d line2 = laneList.get(j).getCenterLine().getLine2d();
                for (int k = 0; k < line1.size() - 1; k++)
                {
                    for (int m = 0; m < line2.size() - 1; m++)
                    {
                        Point2d point = Point2d.intersectionOfLineSegments(line1.get(k), line1.get(k + 1), line2.get(m),
                                line2.get(m + 1));
                        if (point != null)
                        {
                            System.out.println(String.format("    text(%.3f, %.3f, \"(%.3f, %.3f)\", \"Clipping\", \"on\");",
                                    point.x, point.y, point.x, point.y));
                            conflicts.addRow(
                                    new Object[] {laneList.get(i).getFullId(), laneList.get(j).getFullId(), point.x, point.y});
                        }
                    }
                }
            }
        }

        CsvData.writeData(folder + prefix + "lanes.csv", folder + prefix + "lanes.header", lanes);
        CsvData.writeData(folder + prefix + "conflicts.csv", folder + prefix + "conflicts.header", conflicts);
    }

}
