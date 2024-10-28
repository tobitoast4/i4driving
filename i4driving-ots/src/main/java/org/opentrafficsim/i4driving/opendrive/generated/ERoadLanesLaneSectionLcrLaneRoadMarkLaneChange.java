
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_lanes_laneSection_lcr_lane_roadMark_laneChange.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_lanes_laneSection_lcr_lane_roadMark_laneChange"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="increase"/&gt;
 *     &lt;enumeration value="decrease"/&gt;
 *     &lt;enumeration value="both"/&gt;
 *     &lt;enumeration value="none"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_lanes_laneSection_lcr_lane_roadMark_laneChange")
@XmlEnum
@SuppressWarnings("all") public enum ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange {

    @XmlEnumValue("increase")
    INCREASE("increase"),
    @XmlEnumValue("decrease")
    DECREASE("decrease"),
    @XmlEnumValue("both")
    BOTH("both"),
    @XmlEnumValue("none")
    NONE("none");
    private final String value;

    ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange fromValue(String v) {
        for (ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange c: ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
