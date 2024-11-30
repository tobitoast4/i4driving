package org.opentrafficsim.i4driving.opendrive;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.line.PolyLine2d;
import org.djutils.draw.line.Ray2d;
import org.djutils.draw.point.OrientedPoint2d;
import org.djutils.draw.point.Point2d;
import org.opentrafficsim.core.geometry.ContinuousArc;
import org.opentrafficsim.core.geometry.ContinuousClothoid;
import org.opentrafficsim.core.geometry.ContinuousLine;
import org.opentrafficsim.core.geometry.ContinuousStraight;
import org.opentrafficsim.core.geometry.FlattableLine;
import org.opentrafficsim.core.geometry.Flattener;
import org.opentrafficsim.core.geometry.FractionalLengthData;
import org.opentrafficsim.i4driving.opendrive.generated.EParamPoly3PRange;
import org.opentrafficsim.i4driving.opendrive.generated.TRoadPlanViewGeometry;
import org.opentrafficsim.i4driving.opendrive.generated.TRoadPlanViewGeometryParamPoly3;

/**
 * Design line definition, as a string of segments defined each as a straight line, arc, spiral (clothoid) or parameterized 3rd
 * degree polynomial.
 * @author wjschakel
 */
public class SegmentedLine implements ContinuousLine
{

    /** Design line segments, where each is a continuous line. */
    private NavigableMap<Double, ContinuousLine> segments = new TreeMap<>();

    /**
     * Constructor.
     * @param geometry list of geometry tags
     * @param roadLength length of the road
     */
    public SegmentedLine(final List<TRoadPlanViewGeometry> geometry, final Length roadLength)
    {
        for (TRoadPlanViewGeometry geom : geometry)
        {
            ContinuousLine line;
            OrientedPoint2d start = new OrientedPoint2d(geom.getX(), geom.getY(), geom.getHdg());
            if (geom.getLine() != null)
            {
                line = new ContinuousStraight(start, geom.getLength().si);
            }
            else if (geom.getArc() != null)
            {
                double curvature = geom.getArc().getCurvature();
                line = new ContinuousArc(start, 1.0 / Math.abs(curvature), curvature > 0.0, geom.getLength().si);
            }
            else if (geom.getSpiral() != null)
            {
                line = ContinuousClothoid.withLength(start, geom.getLength().si, geom.getSpiral().getCurvStart(),
                        geom.getSpiral().getCurvEnd());
            }
            else if (geom.getPoly3() != null)
            {
                // note that <poly3> is a deprecated tag
                throw new UnsupportedOperationException("<poly3> not supported.");
            }
            else if (geom.getParamPoly3() != null)
            {
                line = new ParamPoly3(start, geom.getParamPoly3(), geom.getLength());
            }
            else
            {
                throw new UnsupportedOperationException("TRoadPlanViewGeometry missing all shape tags.");
            }
            this.segments.put(geom.getS() / roadLength.si, line);
        }
    }

    @Override
    public OrientedPoint2d getStartPoint()
    {
        return this.segments.firstEntry().getValue().getStartPoint();
    }

    @Override
    public OrientedPoint2d getEndPoint()
    {
        return this.segments.lastEntry().getValue().getEndPoint();
    }

    @Override
    public double getStartCurvature()
    {
        return this.segments.firstEntry().getValue().getStartCurvature();
    }

    @Override
    public double getEndCurvature()
    {
        return this.segments.lastEntry().getValue().getEndCurvature();
    }

    @Override
    public PolyLine2d flatten(final Flattener flattener)
    {
        return flattener.flatten(new FlattableLine()
        {
            @Override
            public Point2d get(final double fraction)
            {
                Entry<Double, ContinuousLine> entry = SegmentedLine.this.segments.floorEntry(fraction);
                Double next = SegmentedLine.this.segments.higherKey(entry.getKey());
                if (next == null)
                {
                    next = 1.0;
                }
                double f = (fraction - entry.getKey()) / (next - entry.getKey());
                if (entry.getValue() instanceof ContinuousStraight)
                {
                    // ContinuousStraight does not use flattener internally, obtain point from 2-point polyline
                    Ray2d ray = entry.getValue().flatten(null).getLocationFraction(f);
                    return new Point2d(ray.x, ray.y);
                }
                PointDirectionFlattener flattener = new PointDirectionFlattener(f);
                entry.getValue().flatten(flattener); // internally obtains point at fraction
                return flattener.getPoint();
            }

            @Override
            public double getDirection(final double fraction)
            {
                Entry<Double, ContinuousLine> entry = SegmentedLine.this.segments.floorEntry(fraction);
                Double next = SegmentedLine.this.segments.higherKey(entry.getKey());
                if (next == null)
                {
                    next = 1.0;
                }
                double f = (fraction - entry.getKey()) / (next - entry.getKey());
                if (entry.getValue() instanceof ContinuousStraight)
                {
                    // ContinuousStraight does not use flattener internally, obtain direction from 2-point polyline
                    return entry.getValue().flatten(null).getLocationFraction(f).phi;
                }
                PointDirectionFlattener flattener = new PointDirectionFlattener(f);
                entry.getValue().flatten(flattener); // internally obtains direction at fraction
                return flattener.getDirection();
            }
        });
    }

    @Override
    public PolyLine2d flattenOffset(final FractionalLengthData offsets, final Flattener flattener)
    {
        // Split offsets for segments
        NavigableMap<Double, FractionalLengthData> partialOffsets = new TreeMap<>();
        for (double from : this.segments.keySet())
        {
            Double next = this.segments.higherKey(from);
            double to = next == null ? 1.0 : next;
            partialOffsets.put(from, OffsetData.sub(offsets, from, to));
        }
        return flattener.flatten(new FlattableLine()
        {
            @Override
            public Point2d get(final double fraction)
            {
                Entry<Double, ContinuousLine> entry = SegmentedLine.this.segments.floorEntry(fraction);
                Double next = SegmentedLine.this.segments.higherKey(entry.getKey());
                if (next == null)
                {
                    next = 1.0;
                }
                FractionalLengthData partialOffset = partialOffsets.floorEntry(fraction).getValue();
                double f = (fraction - entry.getKey()) / (next - entry.getKey());
                if (entry.getValue() instanceof ContinuousStraight)
                {
                    // ContinuousStraight does not use flattener internally, obtain point from 2-point polyline
                    Ray2d ray = entry.getValue().flattenOffset(partialOffset, null).getLocationFraction(f);
                    return new Point2d(ray.x, ray.y);
                }
                PointDirectionFlattener flattener = new PointDirectionFlattener(f);
                entry.getValue().flattenOffset(partialOffset, flattener); // internally obtains point at fraction
                return flattener.getPoint();
            }

            @Override
            public double getDirection(final double fraction)
            {
                Entry<Double, ContinuousLine> entry = SegmentedLine.this.segments.floorEntry(fraction);
                Double next = SegmentedLine.this.segments.higherKey(entry.getKey());
                if (next == null)
                {
                    next = 1.0;
                }
                FractionalLengthData partialOffset = partialOffsets.floorEntry(fraction).getValue();
                double f = (fraction - entry.getKey()) / (next - entry.getKey());
                if (entry.getValue() instanceof ContinuousStraight)
                {
                    // ContinuousStraight does not use flattener internally, obtain direction from 2-point polyline
                    return entry.getValue().flattenOffset(partialOffset, null).getLocationFraction(f).phi;
                }
                PointDirectionFlattener flattener = new PointDirectionFlattener(f);
                entry.getValue().flattenOffset(partialOffset, flattener); // internally obtains direction at fraction
                return flattener.getDirection();
            }
        });
    }

    @Override
    public double getLength()
    {
        return this.segments.lastKey() + this.segments.lastEntry().getValue().getLength();
    }

    /**
     * Continuous definition of &lt;paramPoly3&gt; tag.
     * @author wjschakel
     */
    private static final class ParamPoly3 implements ContinuousLine
    {
        /** Start point. */
        private OrientedPoint2d start;

        /** aU coefficient. */
        private final double aU;

        /** bU coefficient. */
        private final double bU;

        /** cU coefficient. */
        private final double cU;

        /** dU coefficient. */
        private final double dU;

        /** aV coefficient. */
        private final double aV;

        /** bV coefficient. */
        private final double bV;

        /** cV coefficient. */
        private final double cV;

        /** dV coefficient. */
        private final double dV;

        /** Range of p, either 1.0 or length. */
        private final double pRange;

        /** Geometry length. */
        private final Length length;

        /**
         * Constructor.
         * @param startPoint start point
         * @param tag tag
         * @param length length
         */
        private ParamPoly3(final OrientedPoint2d startPoint, final TRoadPlanViewGeometryParamPoly3 tag, final Length length)
        {
            this.start = startPoint;
            this.aU = tag.getAU();
            this.bU = tag.getBU();
            this.cU = tag.getCU();
            this.dU = tag.getDU();
            this.aV = tag.getAV();
            this.bV = tag.getBV();
            this.cV = tag.getCV();
            this.dV = tag.getDV();
            this.pRange = EParamPoly3PRange.ARC_LENGTH.equals(tag.getPRange()) ? length.si : 1.0;
            this.length = length;
        }

        @Override
        public OrientedPoint2d getStartPoint()
        {
            return this.start;
        }

        @Override
        public OrientedPoint2d getEndPoint()
        {
            return getPoint(this.pRange);
        }

        /**
         * Returns the point at given p-value.
         * @param p p-value.
         * @return point at given p-value
         */
        public OrientedPoint2d getPoint(final double p)
        {
            double p2 = p * p;
            double p3 = p2 * p;
            double du = this.aU + this.bU * p + this.cU * p2 + this.dU * p3;
            double dv = this.aV + this.bV * p + this.cV * p2 + this.dV * p3;
            double ddu = this.bU + 2.0 * this.cU * p + 3.0 * this.dU * p2;
            double ddv = this.bV + 2.0 * this.cV * p + 3.0 * this.dV * p2;
            return new OrientedPoint2d(this.start.x + du, this.start.y + dv, this.start.dirZ + Math.atan2(ddv, ddu));
        }

        @Override
        public double getStartCurvature()
        {
            return getCurvature(0.0);
        }

        @Override
        public double getEndCurvature()
        {
            return getCurvature(this.pRange);
        }

        /**
         * Returns the curvature for given p-value.
         * @param p p-value
         * @return curvature for given p-value
         */
        private double getCurvature(final double p)
        {
            // https://en.wikipedia.org/wiki/Curvature#In_terms_of_a_general_parametrization
            double p2 = p * p;
            double ddu = this.bU + 2.0 * this.cU * p + 3.0 * this.dU * p2;
            double ddv = this.bV + 2.0 * this.cV * p + 3.0 * this.dV * p2;
            double dddu = 2.0 * this.cU + 6.0 * this.dU * p;
            double dddv = 2.0 * this.cV + 6.0 * this.dV * p;
            return (ddu * dddv - ddv * dddu) / Math.pow(ddu * ddu + ddv * ddv, 1.5);
        }

        @Override
        public PolyLine2d flatten(final Flattener flattener)
        {
            return flattener.flatten(new FlattableLine()
            {
                @Override
                public Point2d get(final double fraction)
                {
                    return getPoint(fraction);
                }

                @Override
                public double getDirection(final double fraction)
                {
                    return getPoint(fraction).dirZ;
                }
            });
        }

        @Override
        public PolyLine2d flattenOffset(final FractionalLengthData offsets, final Flattener flattener)
        {
            return flattener.flatten(new FlattableLine()
            {
                @Override
                public Point2d get(final double fraction)
                {
                    OrientedPoint2d point = getPoint(fraction);
                    double offset = offsets.get(fraction);
                    return new Point2d(point.x + offset * Math.cos(point.dirZ), point.y + offset * Math.sin(point.dirZ));
                }

                @Override
                public double getDirection(final double fraction)
                {
                    return getPoint(fraction).dirZ + offsets.getDerivative(fraction) / getLength();
                }
            });
        }

        @Override
        public double getLength()
        {
            return this.length.si;
        }
    }

    /**
     * Flattener that is used to extract a point and direction from a continuous line that represents itself as FlattableLine.
     */
    private static final class PointDirectionFlattener implements Flattener
    {
        /** Obtained point. */
        private Point2d point;

        /** Direction. */
        private double direction;

        /** Fraction to obtain point at. */
        private final double fraction;

        /**
         * Constructor.
         * @param fraction fraction to obtain point at
         */
        private PointDirectionFlattener(final double fraction)
        {
            this.fraction = fraction;
        }

        /**
         * Obtains point and direction from flattable line at fraction that is given to this flattener, stores them, and returns
         * null.
         * @param line flattable line
         * @return {@code null} always
         */
        @Override
        public PolyLine2d flatten(final FlattableLine line)
        {
            this.point = line.get(this.fraction);
            this.direction = line.getDirection(fraction);
            return null;
        }

        /**
         * Return point.
         * @return point
         */
        public Point2d getPoint()
        {
            return this.point;
        }

        /**
         * Returns direction.
         * @return direction
         */
        public double getDirection()
        {
            return this.direction;
        }
    }

}
