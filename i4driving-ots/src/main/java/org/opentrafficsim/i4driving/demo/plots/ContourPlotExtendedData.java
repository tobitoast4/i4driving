package org.opentrafficsim.i4driving.demo.plots;

import java.awt.Color;
import java.util.List;

import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.means.ArithmeticMean;
import org.opentrafficsim.core.dsol.OtsSimulatorInterface;
import org.opentrafficsim.draw.BoundsPaintScale;
import org.opentrafficsim.draw.egtf.Converter;
import org.opentrafficsim.draw.egtf.Quantity;
import org.opentrafficsim.draw.graphs.AbstractContourPlot;
import org.opentrafficsim.draw.graphs.ContourDataSource;
import org.opentrafficsim.draw.graphs.ContourDataSource.ContourDataType;
import org.opentrafficsim.draw.graphs.GraphType;
import org.opentrafficsim.draw.graphs.GraphUtil;
import org.opentrafficsim.kpi.sampling.SamplingException;
import org.opentrafficsim.kpi.sampling.Trajectory;
import org.opentrafficsim.kpi.sampling.TrajectoryGroup;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataNumber;
import org.opentrafficsim.kpi.sampling.data.ExtendedDataType;
import org.opentrafficsim.road.network.sampling.GtuDataRoad;
import org.opentrafficsim.swing.graphs.OtsPlotScheduler;

/**
 * Contour plot to plot the value of an extended data type.
 * @author wjschakel
 */
public class ContourPlotExtendedData extends AbstractContourPlot<Double>
{

    /** Temporarily stored data type. */
    private static ExtendedContourDataType constructDataType;

    /** Contour data type. */
    private final ExtendedContourDataType contourDataType;

    /**
     * Constructor.
     * @param caption caption
     * @param simulator simulator
     * @param dataPool data pool
     * @param extendedDataType extended data type
     * @param min minimum value
     * @param max maximum value
     * @param legendStep step between legend values
     */
    public ContourPlotExtendedData(final String caption, final OtsSimulatorInterface simulator,
            final ContourDataSource dataPool, final ExtendedDataNumber<GtuDataRoad> extendedDataType, final double min,
            final double max, final double legendStep)
    {
        super(constructDataType(caption, extendedDataType), new OtsPlotScheduler(simulator), dataPool,
                createPaintScale(min, max), legendStep, "%.2f", "value %.2f");
        this.contourDataType = constructDataType;
    }

    /**
     * This method is a total hack, as super will call getContourDataType() which will return this.contourDataType, which at
     * that point has not been set. So we set one statically before it is called, by forwarding the caption argument through
     * this method.
     * @param caption caption
     * @param extendedDataType extended data type
     * @return caption
     */
    private static String constructDataType(final String caption, final ExtendedDataNumber<GtuDataRoad> extendedDataType)
    {
        Quantity<Double, double[][]> quantity =
                new Quantity<>("extended_data_" + extendedDataType.getId(), new Converter<double[][]>()
                {
                    @Override
                    public double[][] convert(final double[][] filteredData)
                    {
                        return filteredData;
                    }
                });
        constructDataType = new ExtendedContourDataType(extendedDataType, quantity);
        return caption;
    }

    /**
     * Creates a paint scale from red, via yellow to green.
     * @param min minimum value
     * @param max maximum value
     * @return paint scale
     */
    private static BoundsPaintScale createPaintScale(final double min, final double max)
    {
        Color[] colorValues = BoundsPaintScale.GREEN_RED;
        return new BoundsPaintScale(new double[] {min, 0.5 * (min + max), max}, colorValues);
    }

    /** {@inheritDoc} */
    @Override
    public GraphType getGraphType()
    {
        return GraphType.OTHER;
    }

    /** {@inheritDoc} */
    @Override
    protected double scale(final double si)
    {
        return si;
    }

    /** {@inheritDoc} */
    @Override
    protected double getValue(final int item, final double cellLength, final double cellSpan)
    {
        return getDataPool().get(item, getContourDataType());
    }

    /** {@inheritDoc} */
    @Override
    protected ContourDataType<Double, ArithmeticMean<Double, Double>> getContourDataType()
    {
        return this.contourDataType == null ? constructDataType : this.contourDataType;
    }

    /**
     * Attention contour data type.
     */
    private static class ExtendedContourDataType implements ContourDataType<Double, ArithmeticMean<Double, Double>>
    {
        /** Extended data type. */
        private final ExtendedDataType<?, ? extends float[], ?, ?> dataType;

        /** Quantity. */
        private final Quantity<Double, ?> quantity;

        /**
         * Constructor.
         * @param dataType extended data type
         * @param quantity quantity
         */
        ExtendedContourDataType(final ExtendedDataType<?, ? extends float[], ?, ?> dataType, final Quantity<Double, ?> quantity)
        {
            this.dataType = dataType;
            this.quantity = quantity;
        }

        /** {@inheritDoc} */
        @Override
        public ArithmeticMean<Double, Double> identity()
        {
            return new ArithmeticMean<>();
        }

        /** {@inheritDoc} */
        @Override
        public ArithmeticMean<Double, Double> processSeries(final ArithmeticMean<Double, Double> intermediate,
                final List<TrajectoryGroup<?>> trajectories, final List<Length> xFrom, final List<Length> xTo, final Time tFrom,
                final Time tTo)
        {
            for (int i = 0; i < trajectories.size(); i++)
            {
                TrajectoryGroup<?> trajectoryGroup = trajectories.get(i);
                for (Trajectory<?> trajectory : trajectoryGroup.getTrajectories())
                {
                    if (GraphUtil.considerTrajectory(trajectory, tFrom, tTo))
                    {
                        trajectory = trajectory.subSet(xFrom.get(i), xTo.get(i), tFrom, tTo);
                        try
                        {
                            float[] out = trajectory.getExtendedData(this.dataType);
                            for (float f : out)
                            {
                                if (!Float.isNaN(f))
                                {
                                    intermediate.add((double) f, 1.0);
                                }
                            }
                        }
                        catch (SamplingException ex)
                        {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
            return intermediate;
        }

        /** {@inheritDoc} */
        @Override
        public Double finalize(final ArithmeticMean<Double, Double> intermediate)
        {
            return intermediate.getMean();
        }

        /** {@inheritDoc} */
        @Override
        public Quantity<Double, ?> getQuantity()
        {
            return this.quantity;
        }
    };
}
