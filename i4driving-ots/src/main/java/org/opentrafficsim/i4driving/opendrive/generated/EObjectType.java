
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_objectType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_objectType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="obstacle"/&gt;
 *     &lt;enumeration value="car"/&gt;
 *     &lt;enumeration value="pole"/&gt;
 *     &lt;enumeration value="tree"/&gt;
 *     &lt;enumeration value="vegetation"/&gt;
 *     &lt;enumeration value="barrier"/&gt;
 *     &lt;enumeration value="building"/&gt;
 *     &lt;enumeration value="parkingSpace"/&gt;
 *     &lt;enumeration value="patch"/&gt;
 *     &lt;enumeration value="railing"/&gt;
 *     &lt;enumeration value="trafficIsland"/&gt;
 *     &lt;enumeration value="crosswalk"/&gt;
 *     &lt;enumeration value="streetLamp"/&gt;
 *     &lt;enumeration value="gantry"/&gt;
 *     &lt;enumeration value="soundBarrier"/&gt;
 *     &lt;enumeration value="van"/&gt;
 *     &lt;enumeration value="bus"/&gt;
 *     &lt;enumeration value="trailer"/&gt;
 *     &lt;enumeration value="bike"/&gt;
 *     &lt;enumeration value="motorbike"/&gt;
 *     &lt;enumeration value="tram"/&gt;
 *     &lt;enumeration value="train"/&gt;
 *     &lt;enumeration value="pedestrian"/&gt;
 *     &lt;enumeration value="wind"/&gt;
 *     &lt;enumeration value="roadMark"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_objectType")
@XmlEnum
@SuppressWarnings("all") public enum EObjectType {


    /**
     * i.e. unknown
     * 
     */
    @XmlEnumValue("none")
    NONE("none"),

    /**
     * for anything that is not further categorized
     * 
     */
    @XmlEnumValue("obstacle")
    OBSTACLE("obstacle"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("car")
    CAR("car"),
    @XmlEnumValue("pole")
    POLE("pole"),
    @XmlEnumValue("tree")
    TREE("tree"),
    @XmlEnumValue("vegetation")
    VEGETATION("vegetation"),
    @XmlEnumValue("barrier")
    BARRIER("barrier"),
    @XmlEnumValue("building")
    BUILDING("building"),
    @XmlEnumValue("parkingSpace")
    PARKING_SPACE("parkingSpace"),
    @XmlEnumValue("patch")
    PATCH("patch"),
    @XmlEnumValue("railing")
    RAILING("railing"),
    @XmlEnumValue("trafficIsland")
    TRAFFIC_ISLAND("trafficIsland"),
    @XmlEnumValue("crosswalk")
    CROSSWALK("crosswalk"),
    @XmlEnumValue("streetLamp")
    STREET_LAMP("streetLamp"),
    @XmlEnumValue("gantry")
    GANTRY("gantry"),
    @XmlEnumValue("soundBarrier")
    SOUND_BARRIER("soundBarrier"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("van")
    VAN("van"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("bus")
    BUS("bus"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("trailer")
    TRAILER("trailer"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("bike")
    BIKE("bike"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("motorbike")
    MOTORBIKE("motorbike"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("tram")
    TRAM("tram"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("train")
    TRAIN("train"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("pedestrian")
    PEDESTRIAN("pedestrian"),

    /**
     * deprecated
     * 
     */
    @XmlEnumValue("wind")
    WIND("wind"),
    @XmlEnumValue("roadMark")
    ROAD_MARK("roadMark");
    private final String value;

    EObjectType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EObjectType fromValue(String v) {
        for (EObjectType c: EObjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
