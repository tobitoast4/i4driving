
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_roadMarkColor.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_roadMarkColor"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="standard"/&gt;
 *     &lt;enumeration value="blue"/&gt;
 *     &lt;enumeration value="green"/&gt;
 *     &lt;enumeration value="red"/&gt;
 *     &lt;enumeration value="white"/&gt;
 *     &lt;enumeration value="yellow"/&gt;
 *     &lt;enumeration value="orange"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_roadMarkColor")
@XmlEnum
@SuppressWarnings("all") public enum ERoadMarkColor {


    /**
     * equivalent to "white"
     * 
     */
    @XmlEnumValue("standard")
    STANDARD("standard"),
    @XmlEnumValue("blue")
    BLUE("blue"),
    @XmlEnumValue("green")
    GREEN("green"),
    @XmlEnumValue("red")
    RED("red"),
    @XmlEnumValue("white")
    WHITE("white"),
    @XmlEnumValue("yellow")
    YELLOW("yellow"),
    @XmlEnumValue("orange")
    ORANGE("orange");
    private final String value;

    ERoadMarkColor(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadMarkColor fromValue(String v) {
        for (ERoadMarkColor c: ERoadMarkColor.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
