
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_laneType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_laneType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="shoulder"/&gt;
 *     &lt;enumeration value="border"/&gt;
 *     &lt;enumeration value="driving"/&gt;
 *     &lt;enumeration value="stop"/&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="restricted"/&gt;
 *     &lt;enumeration value="parking"/&gt;
 *     &lt;enumeration value="median"/&gt;
 *     &lt;enumeration value="biking"/&gt;
 *     &lt;enumeration value="sidewalk"/&gt;
 *     &lt;enumeration value="curb"/&gt;
 *     &lt;enumeration value="exit"/&gt;
 *     &lt;enumeration value="entry"/&gt;
 *     &lt;enumeration value="onRamp"/&gt;
 *     &lt;enumeration value="offRamp"/&gt;
 *     &lt;enumeration value="connectingRamp"/&gt;
 *     &lt;enumeration value="bidirectional"/&gt;
 *     &lt;enumeration value="special1"/&gt;
 *     &lt;enumeration value="special2"/&gt;
 *     &lt;enumeration value="special3"/&gt;
 *     &lt;enumeration value="roadWorks"/&gt;
 *     &lt;enumeration value="tram"/&gt;
 *     &lt;enumeration value="rail"/&gt;
 *     &lt;enumeration value="bus"/&gt;
 *     &lt;enumeration value="taxi"/&gt;
 *     &lt;enumeration value="HOV"/&gt;
 *     &lt;enumeration value="mwyEntry"/&gt;
 *     &lt;enumeration value="mwyExit"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_laneType")
@XmlEnum
@SuppressWarnings("all") public enum ELaneType {


    /**
     * Describes a soft shoulder  at the edge of the road
     * 
     */
    @XmlEnumValue("shoulder")
    SHOULDER("shoulder"),

    /**
     * Describes a hard border at the edge of the road. has the same height as the drivable lane.
     * 
     */
    @XmlEnumValue("border")
    BORDER("border"),

    /**
     * “normal” drivable road, which is not one of the other types
     * 
     */
    @XmlEnumValue("driving")
    DRIVING("driving"),

    /**
     * Hard shoulder on motorways for emergency stops
     * 
     */
    @XmlEnumValue("stop")
    STOP("stop"),

    /**
     * "Invisible" lane. This lane is on the most ouside of the road. Its only purpose is for simulation, that there is still opendrive present in case the (human) driver leaves the road. 
     * 
     */
    @XmlEnumValue("none")
    NONE("none"),

    /**
     * Lane on which cars should not drive, but have the same height as the drivable lanes. Typically they are separated with lines and often there are additional striped lines on them. 
     * 
     */
    @XmlEnumValue("restricted")
    RESTRICTED("restricted"),

    /**
     * Lane with parking spaces
     * 
     */
    @XmlEnumValue("parking")
    PARKING("parking"),

    /**
     * Lane between driving lanes in oposite directions. Typically used in towns on large roads, to separate the traffic.
     * 
     */
    @XmlEnumValue("median")
    MEDIAN("median"),

    /**
     * Lane reserved for Cyclists 
     * 
     */
    @XmlEnumValue("biking")
    BIKING("biking"),

    /**
     * Lane on which pedestrians can walk savely
     * 
     */
    @XmlEnumValue("sidewalk")
    SIDEWALK("sidewalk"),

    /**
     * Lane "curb" is used for curbstones. These have a different height compared to the drivable lanes.
     * 
     */
    @XmlEnumValue("curb")
    CURB("curb"),

    /**
     * Lane Type „exit“ is used for the sections which is parallel to the main road (meaning deceleration lanes) 
     * 
     */
    @XmlEnumValue("exit")
    EXIT("exit"),

    /**
     * Lane Type „entry“ is used for the sections which is parallel to the main road (meaning acceleration lanes
     * 
     */
    @XmlEnumValue("entry")
    ENTRY("entry"),

    /**
     * A ramp leading to a motorway from rural/urban roads is an „onRamp“. 
     * 
     */
    @XmlEnumValue("onRamp")
    ON_RAMP("onRamp"),

    /**
     * A ramp leading away from a motorway and onto rural/urban roads is an „offRamp”. 
     * 
     */
    @XmlEnumValue("offRamp")
    OFF_RAMP("offRamp"),

    /**
     * A ramp connecting two motorways is a „connectingRamp“ (e.g. motorway junction)
     * 
     */
    @XmlEnumValue("connectingRamp")
    CONNECTING_RAMP("connectingRamp"),

    /**
     * this lane type has two use cases:
     * a) only driving lane on a narrow road which may be used in both directions;
     * b) continuous two-way left turn lane on multi-lane roads – US road networks
     * 
     */
    @XmlEnumValue("bidirectional")
    BIDIRECTIONAL("bidirectional"),
    @XmlEnumValue("special1")
    SPECIAL_1("special1"),
    @XmlEnumValue("special2")
    SPECIAL_2("special2"),
    @XmlEnumValue("special3")
    SPECIAL_3("special3"),
    @XmlEnumValue("roadWorks")
    ROAD_WORKS("roadWorks"),
    @XmlEnumValue("tram")
    TRAM("tram"),
    @XmlEnumValue("rail")
    RAIL("rail"),
    @XmlEnumValue("bus")
    BUS("bus"),
    @XmlEnumValue("taxi")
    TAXI("taxi"),

    /**
     * high-occupancy vehicle / carpool vehicle
     * 
     */
    HOV("HOV"),

    /**
     * entry (deprecated)
     * 
     */
    @XmlEnumValue("mwyEntry")
    MWY_ENTRY("mwyEntry"),

    /**
     * exit (deprecated)
     * 
     */
    @XmlEnumValue("mwyExit")
    MWY_EXIT("mwyExit");
    private final String value;

    ELaneType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ELaneType fromValue(String v) {
        for (ELaneType c: ELaneType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
