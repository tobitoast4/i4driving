package org.opentrafficsim.i4driving.opendrive;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djunits.value.vdouble.scalar.Length;
import org.opentrafficsim.core.geometry.FractionalLengthData;
import org.opentrafficsim.i4driving.opendrive.generated.TRoadLanesLaneOffset;

/**
 * Constructs FractionalLengthData based on a list of TRoadLanesLaneOffset. For constant sections 1 values is stored, for linear
 * sections 2 values are stored, and for curved sections 100 segments are stored.
 * @author wjschakel
 */
public class OffsetData extends FractionalLengthData
{

    /**
     * Constructor.
     * @param offset list of offset elements
     * @param length length of road
     */
    public OffsetData(final List<TRoadLanesLaneOffset> offset, final Length length)
    {
        super(toMap(offset, length));
    }

    /**
     * Converts offset sections to map of offset data.
     * @param offset offset sections
     * @param length length of road
     * @return map of offset data
     */
    private static Map<Double, Double> toMap(final List<TRoadLanesLaneOffset> offset, final Length length)
    {
        Map<Double, Double> map = new LinkedHashMap<>();
        for (int i = 0; i < offset.size(); i++)
        {
            TRoadLanesLaneOffset section = offset.get(i);
            if (section.getB() == 0.0 && section.getC() == 0.0 && section.getD() == 0.0)
            {
                // constant
                map.put(section.getS() / length.si, section.getA());
            }
            else if (section.getC() == 0.0 && section.getD() == 0.0)
            {
                // linear
                double sectionLength = (i == offset.size() - 1 ? length.si : offset.get(i + 1).getS()) - section.getS();
                map.put(section.getS() / length.si, section.getA());
                map.put((section.getS() + sectionLength) / length.si, section.getA() + section.getB() * sectionLength);
            }
            else
            {
                // curve, sample 100 length segments
                double sectionLength = (i == offset.size() - 1 ? length.si : offset.get(i + 1).getS()) - section.getS();
                for (int j = 0; j <= 100; j++)
                {
                    double ds = sectionLength * ((double) j) / 100.0;
                    double f = (section.getS() + ds) / length.si;
                    map.put(f, section.getA() + section.getB() * ds + section.getC() * ds * ds + section.getD() * ds * ds * ds);
                }
            }
        }
        if (map.isEmpty())
        {
            map.put(0.0, 0.0);
        }
        return map;
    }

    /**
     * Returns subset of the FractionalLengthData, with fraction rescaled to the range [0...1].
     * @param offsets original FractionalLengthData
     * @param from from fraction
     * @param to to fraction
     * @return subset of the FractionalLengthData
     */
    public static FractionalLengthData sub(final FractionalLengthData offsets, final double from, final double to)
    {
        Map<Double, Double> data = new LinkedHashMap<>();
        offsets.getFractionalLengths().subSet(from, false, to, false)
                .forEach((f) -> data.put((f - from) / (to - from), offsets.get(f)));
        data.put(0.0, offsets.get(from));
        data.put(1.0, offsets.get(to));
        return new FractionalLengthData(data);
    }
    
    /**
     * Adds the data of two fractional length data objects.
     * @param data1 fractional length data 1
     * @param data2 fractional length data 2
     * @return summation of fractional length data
     */
    public static FractionalLengthData add(final FractionalLengthData data1, final FractionalLengthData data2)
    {
        Map<Double, Double> data = new LinkedHashMap<>();
        data1.getFractionalLengths().forEach((f) -> data.put(f, data1.get(f) + data2.get(f)));
        data2.getFractionalLengths().forEach((f) -> data.put(f, data1.get(f) + data2.get(f)));
        return new FractionalLengthData(data);
    }

}
