
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_roadType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_roadType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="unknown"/&gt;
 *     &lt;enumeration value="rural"/&gt;
 *     &lt;enumeration value="motorway"/&gt;
 *     &lt;enumeration value="town"/&gt;
 *     &lt;enumeration value="lowSpeed"/&gt;
 *     &lt;enumeration value="pedestrian"/&gt;
 *     &lt;enumeration value="bicycle"/&gt;
 *     &lt;enumeration value="townExpressway"/&gt;
 *     &lt;enumeration value="townCollector"/&gt;
 *     &lt;enumeration value="townArterial"/&gt;
 *     &lt;enumeration value="townPrivate"/&gt;
 *     &lt;enumeration value="townLocal"/&gt;
 *     &lt;enumeration value="townPlayStreet"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_roadType")
@XmlEnum
@SuppressWarnings("all") public enum ERoadType {

    @XmlEnumValue("unknown")
    UNKNOWN("unknown"),
    @XmlEnumValue("rural")
    RURAL("rural"),
    @XmlEnumValue("motorway")
    MOTORWAY("motorway"),
    @XmlEnumValue("town")
    TOWN("town"),

    /**
     * In Germany, lowSpeed is equivalent to a 30km/h zone
     * 
     */
    @XmlEnumValue("lowSpeed")
    LOW_SPEED("lowSpeed"),
    @XmlEnumValue("pedestrian")
    PEDESTRIAN("pedestrian"),
    @XmlEnumValue("bicycle")
    BICYCLE("bicycle"),
    @XmlEnumValue("townExpressway")
    TOWN_EXPRESSWAY("townExpressway"),
    @XmlEnumValue("townCollector")
    TOWN_COLLECTOR("townCollector"),
    @XmlEnumValue("townArterial")
    TOWN_ARTERIAL("townArterial"),
    @XmlEnumValue("townPrivate")
    TOWN_PRIVATE("townPrivate"),
    @XmlEnumValue("townLocal")
    TOWN_LOCAL("townLocal"),
    @XmlEnumValue("townPlayStreet")
    TOWN_PLAY_STREET("townPlayStreet");
    private final String value;

    ERoadType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadType fromValue(String v) {
        for (ERoadType c: ERoadType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
