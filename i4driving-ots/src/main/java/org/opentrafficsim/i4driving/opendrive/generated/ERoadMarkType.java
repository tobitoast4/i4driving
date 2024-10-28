
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_roadMarkType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_roadMarkType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="solid"/&gt;
 *     &lt;enumeration value="broken"/&gt;
 *     &lt;enumeration value="solid solid"/&gt;
 *     &lt;enumeration value="solid broken"/&gt;
 *     &lt;enumeration value="broken solid"/&gt;
 *     &lt;enumeration value="broken broken"/&gt;
 *     &lt;enumeration value="botts dots"/&gt;
 *     &lt;enumeration value="grass"/&gt;
 *     &lt;enumeration value="curb"/&gt;
 *     &lt;enumeration value="custom"/&gt;
 *     &lt;enumeration value="edge"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_roadMarkType")
@XmlEnum
@SuppressWarnings("all") public enum ERoadMarkType {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("solid")
    SOLID("solid"),
    @XmlEnumValue("broken")
    BROKEN("broken"),

    /**
     * for double solid line
     * 
     */
    @XmlEnumValue("solid solid")
    SOLID_SOLID("solid solid"),

    /**
     * from inside to outside, exception: center lane – from left to right
     * 
     */
    @XmlEnumValue("solid broken")
    SOLID_BROKEN("solid broken"),

    /**
     * from inside to outside, exception: center lane – from left to right
     * 
     */
    @XmlEnumValue("broken solid")
    BROKEN_SOLID("broken solid"),

    /**
     * from inside to outside, exception: center lane – from left to right
     * 
     */
    @XmlEnumValue("broken broken")
    BROKEN_BROKEN("broken broken"),
    @XmlEnumValue("botts dots")
    BOTTS_DOTS("botts dots"),

    /**
     * meaning a grass edge
     * 
     */
    @XmlEnumValue("grass")
    GRASS("grass"),
    @XmlEnumValue("curb")
    CURB("curb"),

    /**
     * if detailed description is given in child tags
     * 
     */
    @XmlEnumValue("custom")
    CUSTOM("custom"),

    /**
     * describing the limit of usable space on a road
     * 
     */
    @XmlEnumValue("edge")
    EDGE("edge");
    private final String value;

    ERoadMarkType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadMarkType fromValue(String v) {
        for (ERoadMarkType c: ERoadMarkType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
