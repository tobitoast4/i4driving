
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_unitDistance.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_unitDistance"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="m"/&gt;
 *     &lt;enumeration value="km"/&gt;
 *     &lt;enumeration value="ft"/&gt;
 *     &lt;enumeration value="mile"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_unitDistance")
@XmlEnum
@SuppressWarnings("all") public enum EUnitDistance {

    @XmlEnumValue("m")
    M("m"),
    @XmlEnumValue("km")
    KM("km"),
    @XmlEnumValue("ft")
    FT("ft"),
    @XmlEnumValue("mile")
    MILE("mile");
    private final String value;

    EUnitDistance(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EUnitDistance fromValue(String v) {
        for (EUnitDistance c: EUnitDistance.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
