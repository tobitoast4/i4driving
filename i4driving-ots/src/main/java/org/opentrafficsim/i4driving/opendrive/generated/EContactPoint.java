
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_contactPoint.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_contactPoint"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="start"/&gt;
 *     &lt;enumeration value="end"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_contactPoint")
@XmlEnum
@SuppressWarnings("all") public enum EContactPoint {

    @XmlEnumValue("start")
    START("start"),
    @XmlEnumValue("end")
    END("end");
    private final String value;

    EContactPoint(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EContactPoint fromValue(String v) {
        for (EContactPoint c: EContactPoint.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
