
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_link_elementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_link_elementType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="road"/&gt;
 *     &lt;enumeration value="junction"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_link_elementType")
@XmlEnum
@SuppressWarnings("all") public enum ERoadLinkElementType {

    @XmlEnumValue("road")
    ROAD("road"),
    @XmlEnumValue("junction")
    JUNCTION("junction");
    private final String value;

    ERoadLinkElementType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadLinkElementType fromValue(String v) {
        for (ERoadLinkElementType c: ERoadLinkElementType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
