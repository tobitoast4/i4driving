
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_surface_CRG_mode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_surface_CRG_mode"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="attached"/&gt;
 *     &lt;enumeration value="attached0"/&gt;
 *     &lt;enumeration value="genuine"/&gt;
 *     &lt;enumeration value="global"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_surface_CRG_mode")
@XmlEnum
@SuppressWarnings("all") public enum ERoadSurfaceCRGMode {

    @XmlEnumValue("attached")
    ATTACHED("attached"),
    @XmlEnumValue("attached0")
    ATTACHED_0("attached0"),
    @XmlEnumValue("genuine")
    GENUINE("genuine"),
    @XmlEnumValue("global")
    GLOBAL("global");
    private final String value;

    ERoadSurfaceCRGMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadSurfaceCRGMode fromValue(String v) {
        for (ERoadSurfaceCRGMode c: ERoadSurfaceCRGMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
