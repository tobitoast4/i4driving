
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_tunnelType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_tunnelType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="standard"/&gt;
 *     &lt;enumeration value="underpass"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_tunnelType")
@XmlEnum
@SuppressWarnings("all") public enum ETunnelType {

    @XmlEnumValue("standard")
    STANDARD("standard"),

    /**
     * i.e. sides are open for daylight
     * 
     */
    @XmlEnumValue("underpass")
    UNDERPASS("underpass");
    private final String value;

    ETunnelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ETunnelType fromValue(String v) {
        for (ETunnelType c: ETunnelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
