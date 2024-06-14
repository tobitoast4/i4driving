package org.opentrafficsim.i4driving.test;

import org.djutils.exceptions.Throw;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * This class describes attention over areas, based on task demand per area. Transition probabilities are based on demand per
 * area, where drivers are assumed to keep perceiving the same area by the demand of that area alone. When total demand is above
 * 1, this means that the probability of switching to another area is reduced. All transition probabilities together result in
 * an overall steady-state, which describes what fraction of time is spent on what area.
 * @author wjschakel
 */
public class AttentionMatrix
{

    /** Mental task demand, i.e. desired fraction of time for perception, per area. */
    private final double demand[];

    /** Attention, i.e. fraction of time, per area. */
    private double[] attention;

    /** Anticipation reliance per area. */
    private double[] anticipationReliance;

    /**
     * Constructor which pre-calculates attention distribution following these assumptions:
     * <ul>
     * <li>Perception exists out of a series of glances.</li>
     * <li>Mental task demand (short: demand) for an area is the fraction of time required for good perception and safe
     * operations.</li>
     * <li>The probability that the next glance is towards the same area as the previous glance, is the demand of the area.</li>
     * <li>If attention is switched to another area, the relative probabilities of the other areas are based on their
     * demand.</li>
     * </ul>
     * With these assumptions, transition probabilities between all areas (and staying in the same area) can be derived. With
     * this a steady-state can be calculated, which describes how time for perception (i.e. attention) is divided over the
     * areas.
     * @param demand double[]; level of mental task demand per area.
     * @throws IllegalArgumentException when a demand value is below 0 or larger than 1
     */
    public AttentionMatrix(double[] demand)
    {
        int n = demand.length;
        this.demand = new double[n];
        System.arraycopy(demand, 0, this.demand, 0, n);
        this.attention = new double[n];
        this.anticipationReliance = new double[n];

        /*
         * matrix is the transition matrix in a Markov chain describing the probability of the next perception glance to be
         * towards area j, given previous area i.
         */
        Matrix matrix = new Matrix(n, n);
        double demandSum = 0.0;
        for (int i = 0; i < n; i++)
        {
            Throw.when(demand[i] < 0.0, IllegalArgumentException.class, "Demand must be >= 0");
            Throw.when(demand[i] >= 1.0, IllegalArgumentException.class, "Demand must be < 1");
            demandSum += demand[i];
        }
        if (demandSum == 0.0)
        {
            return;
        }

        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (i == j)
                {
                    // probability to keep perceiving the same area is the demand of the area
                    matrix.set(j, i, demand[i]);
                }
                else
                {
                    /*
                     * The probability of a switch to another area is 1 - TD(i). The relative probabilities of the other areas
                     * to be switched to, is proportional to the demand in these areas TD(j). These are normalized by the total
                     * sum of demand minus the demand of the area we switch from (i.e. the sum of demand of the other areas),
                     * and scaled by the probability to switch 1 - TD(i).
                     */
                    matrix.set(j, i, (1 - demand[i]) * demand[j] / (demandSum - demand[i]));
                }
            }
        }

        /*
         * We use Jama to find the eigenvector of the transition matrix pertaining to the eigenvalue 1. Each Markov transition
         * matrix has an eigenvalue 1, and the pertaining eigenvector is the steady-state. This steady state is the distribution
         * of attention (in time) over the areas.
         */
        EigenvalueDecomposition ed = new EigenvalueDecomposition(matrix);
        double[] eigenValues = ed.getRealEigenvalues();
        // find the eigenvalue closest to 1 (these values are not highly exact)
        int eigenIndex = 0;
        double dMin = 1.0;
        for (int i = 0; i < n; i++)
        {
            double di = Math.abs(eigenValues[i] - 1.0);
            if (di < dMin)
            {
                dMin = di;
                eigenIndex = i;
            }
        }
        // obtain the eigenvector pertaining to the eigenvalue of 1
        double[][] v = ed.getV().getArray();
        for (int i = 0; i < n; i++)
        {
            this.attention[i] = v[i][eigenIndex];
        }
        // normalize so it sums to 1
        double sumEigenVector = 0.0;
        for (int i = 0; i < n; i++)
        {
            sumEigenVector += this.attention[i];
        }
        for (int i = 0; i < n; i++)
        {
            this.attention[i] /= sumEigenVector;
        }

        /*
         * Anticipation reliance per area is the difference between the steady state (actual proportion of time we look at an
         * area) and the desired proportion of time to look at an area. Note that if total demand is less than 1, the steady
         * state for each area is always larger than demand and there is no anticipation reliance.
         */
        if (demandSum > 1.0)
        {
            for (int i = 0; i < n; i++)
            {
                this.anticipationReliance[i] = this.demand[i] - this.attention[i];
            }
        }
    }

    /**
     * Returns the fraction of time that is spent on area <i>i</i>.
     * @param i int; index of area.
     * @return double; fraction of time that is spent on area <i>i</i>.
     */
    public double getAttention(final int i)
    {
        return this.attention[i];
    }

    /**
     * Returns the level of anticipation reliance for area <i>i</i>. This is the fraction of time that is reduced from
     * perceiving area <i>i</i>, relative to the desired fraction of time to perceive area <i>i</i>.
     * @param i int; index of area.
     * @return double; level of anticipation reliance for area <i>i</i>.
     */
    public double getAnticipationReliance(final int i)
    {
        return this.anticipationReliance[i];
    }

    /**
     * Returns the deterioration of area <i>i</i>. This is the anticipation reliance for area <i>i</i>, divided by the desired
     * level of attention for area <i>i</i>. This value is an indication for reaction time for the area.
     * <p>
     * If demand for the area is 0, this method returns 0.
     * @param i int; index of area.
     * @return double; fraction of anticipation reliance over desired attention for area <i>i</i>.
     */
    public double getDeterioration(final int i)
    {
        return this.demand[i] == 0.0 ? 0.0 : this.anticipationReliance[i] / this.demand[i];
    }

}
