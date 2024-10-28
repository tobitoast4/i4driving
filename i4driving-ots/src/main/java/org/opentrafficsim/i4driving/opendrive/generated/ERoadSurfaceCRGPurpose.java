
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_surface_CRG_purpose.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_surface_CRG_purpose"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="elevation"/&gt;
 *     &lt;enumeration value="friction"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_surface_CRG_purpose")
@XmlEnum
@SuppressWarnings("all") public enum ERoadSurfaceCRGPurpose {

    @XmlEnumValue("elevation")
    ELEVATION("elevation"),
    @XmlEnumValue("friction")
    FRICTION("friction");
    private final String value;

    ERoadSurfaceCRGPurpose(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadSurfaceCRGPurpose fromValue(String v) {
        for (ERoadSurfaceCRGPurpose c: ERoadSurfaceCRGPurpose.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
