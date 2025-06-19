package org.opentrafficsim.i4driving.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.opentrafficsim.i4driving.tactical.perception.mental.channel.AttentionMatrix;

public class AttentionTest
{

    /**
     * Manual test code.
     * @param args
     */
    public static void main(final String[] args)
    {
        double[] td = {0.4, 0.8};
        AttentionMatrix m = new AttentionMatrix(td);
        for (int i = 0; i < td.length; i++)
        {
            System.out.println(m.getAttention(i));
        }
    }

    /**
     * This tests that AttentionMatrix results in levels of attention that make sense.
     */
    @Test
    public void test()
    {
        for (int i = 1; i < 1000; i++)
        {
            int n = ThreadLocalRandom.current().nextInt(1, 7); // number of areas
            double[] demand = new double[n]; // demand per area
            for (int j = 0; j < n; j++)
            {
                // sqrt so we also test saturated attention when demandSum > 1.0
                demand[j] = Math.sqrt(ThreadLocalRandom.current().nextDouble() / n);
            }
            double demandSum = 0.0;
            for (int j = 0; j < n; j++)
            {
                demandSum += demand[j];
            }

            AttentionMatrix m = new AttentionMatrix(demand);
            double attentionSum = 0.0;
            for (int j = 0; j < demand.length; j++)
            {
                assertTrue("Attention is negative", m.getAttention(j) >= 0.0);
                if (demandSum <= 1.0)
                {
                    assertTrue("Non saturated attention must not be smaller than task demand", m.getAttention(j) >= demand[j]);
                    assertEquals("AR must be zero when not saturated.", 0.0, m.getAnticipationReliance(j), 1e-12);
                    assertEquals("AR must be zero when not saturated.", 0.0, m.getDeterioration(j), 1e-12);
                }
                else
                {
                    assertTrue("Saturated attention must not be larger than task demand", m.getAttention(j) <= demand[j]);
                    // demand[j] == 0.0 is a special case, in other cases there must be some anticipation reliance
                    assertTrue("Anticipation reliance must be non-zero when saturated.",
                            demand[j] == 0.0 || m.getAnticipationReliance(j) > 0.0);
                    assertTrue("Anticipation reliance must be non-zero when saturated.",
                            demand[j] == 0.0 || m.getDeterioration(j) > 0.0);
                }
                attentionSum += m.getAttention(j);
            }
            assertTrue("Attention sum must not exceed 1.0", attentionSum <= 1.0001);
        }
    }

}
