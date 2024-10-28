
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_junction_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_junction_type"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="default"/&gt;
 *     &lt;enumeration value="virtual"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_junction_type")
@XmlEnum
@SuppressWarnings("all") public enum EJunctionType {

    @XmlEnumValue("default")
    DEFAULT("default"),
    @XmlEnumValue("virtual")
    VIRTUAL("virtual");
    private final String value;

    EJunctionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EJunctionType fromValue(String v) {
        for (EJunctionType c: EJunctionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
