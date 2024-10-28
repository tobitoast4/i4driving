
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_lanes_laneSection_lr_lane_access_rule.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_lanes_laneSection_lr_lane_access_rule"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="allow"/&gt;
 *     &lt;enumeration value="deny"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_lanes_laneSection_lr_lane_access_rule")
@XmlEnum
@SuppressWarnings("all") public enum ERoadLanesLaneSectionLrLaneAccessRule {

    @XmlEnumValue("allow")
    ALLOW("allow"),
    @XmlEnumValue("deny")
    DENY("deny");
    private final String value;

    ERoadLanesLaneSectionLrLaneAccessRule(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadLanesLaneSectionLrLaneAccessRule fromValue(String v) {
        for (ERoadLanesLaneSectionLrLaneAccessRule c: ERoadLanesLaneSectionLrLaneAccessRule.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
