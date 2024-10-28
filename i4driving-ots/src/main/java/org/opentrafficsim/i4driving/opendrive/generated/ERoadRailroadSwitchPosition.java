
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_railroad_switch_position.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_railroad_switch_position"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="dynamic"/&gt;
 *     &lt;enumeration value="straight"/&gt;
 *     &lt;enumeration value="turn"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_railroad_switch_position")
@XmlEnum
@SuppressWarnings("all") public enum ERoadRailroadSwitchPosition {

    @XmlEnumValue("dynamic")
    DYNAMIC("dynamic"),
    @XmlEnumValue("straight")
    STRAIGHT("straight"),
    @XmlEnumValue("turn")
    TURN("turn");
    private final String value;

    ERoadRailroadSwitchPosition(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadRailroadSwitchPosition fromValue(String v) {
        for (ERoadRailroadSwitchPosition c: ERoadRailroadSwitchPosition.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
