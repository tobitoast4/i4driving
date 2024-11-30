package org.opentrafficsim.i4driving.opendrive;

import java.util.function.Supplier;

import org.djutils.exceptions.Throw;

/**
 * Id generator that produces A, B, C, ... X, Y, Z, AA, AB, AC, ... AX, AY, AZ, BA, BB, BC, etc., with possible prefix.
 * @author wjschakel
 */
public class AlphabeticIdGenerator implements Supplier<String>
{

    /** Prefix. */
    private final String prefix;

    /** Id counter. */
    private int counter = 1;

    /**
     * Constructor setting no prefix.
     */
    public AlphabeticIdGenerator()
    {
        this.prefix = "";
    }

    /**
     * Constructor setting id prefix.
     * @param prefix prefix
     */
    public AlphabeticIdGenerator(final String prefix)
    {
        Throw.whenNull(prefix, "prefix should not be null.");
        this.prefix = prefix;
    }

    @Override
    public String get()
    {
        /*
         * Note that 'A' cannot represent '0', as then 'AA' would not be the 27th id, but somehow equal to the 1st id 'A'.
         * Hence, the number is always reduced by 1 before applying the modulo for any digit. For 2033, which is 3 * 26^2 + 0 *
         * 26^1 + 5 * 26^0, we do not get 'C?E', but 'BZE', where 'Z' represents a full 26 * 26^1 = 1 * 26^2 extra. This is
         * similar to 2'10'5 to represent 305 in base-10. We are in base-26, but have to represent a normal '0' by a 'Z', and
         * then subtracting 1 from the next higher decimal place. To explain it the other way around; suppose we have 702 = 1 *
         * 26^2 + 1 * 26^1 + 0 * 26^0, normal counting would give 'AA?'. We cannot have '?', so that becomes Z and we subtract 1
         * from the higher digit, i.e. 'A?Z'. We have the same issue and do the same: '?ZZ'. Now '?' is a 0 we can just drop. We
         * obtain 0 * 26^2 + 26 * 26^1 + 26 * 26^0 = 702.
         */
        String id = "";
        int num = this.counter++;
        while (num > 0)
        {
            int remainder = (num - 1) % 26;
            num = (num - 1 - remainder) / 26;
            id = (char) (remainder + 'A') + id;
        }
        return this.prefix + id;
    }

}
