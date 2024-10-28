
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_maxSpeedString.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_maxSpeedString"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="no limit"/&gt;
 *     &lt;enumeration value="undefined"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_maxSpeedString")
@XmlEnum
@SuppressWarnings("all") public enum EMaxSpeedString {

    @XmlEnumValue("no limit")
    NO_LIMIT("no limit"),
    @XmlEnumValue("undefined")
    UNDEFINED("undefined");
    private final String value;

    EMaxSpeedString(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EMaxSpeedString fromValue(String v) {
        for (EMaxSpeedString c: EMaxSpeedString.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
