
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_unitSpeed.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_unitSpeed"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="m/s"/&gt;
 *     &lt;enumeration value="mph"/&gt;
 *     &lt;enumeration value="km/h"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_unitSpeed")
@XmlEnum
@SuppressWarnings("all") public enum EUnitSpeed {

    @XmlEnumValue("m/s")
    M_S("m/s"),
    @XmlEnumValue("mph")
    MPH("mph"),
    @XmlEnumValue("km/h")
    KM_H("km/h");
    private final String value;

    EUnitSpeed(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EUnitSpeed fromValue(String v) {
        for (EUnitSpeed c: EUnitSpeed.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
