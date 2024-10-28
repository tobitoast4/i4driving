
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_accessRestrictionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_accessRestrictionType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="simulator"/&gt;
 *     &lt;enumeration value="autonomousTraffic"/&gt;
 *     &lt;enumeration value="pedestrian"/&gt;
 *     &lt;enumeration value="passengerCar"/&gt;
 *     &lt;enumeration value="bus"/&gt;
 *     &lt;enumeration value="delivery"/&gt;
 *     &lt;enumeration value="emergency"/&gt;
 *     &lt;enumeration value="taxi"/&gt;
 *     &lt;enumeration value="throughTraffic"/&gt;
 *     &lt;enumeration value="truck"/&gt;
 *     &lt;enumeration value="bicycle"/&gt;
 *     &lt;enumeration value="motorcycle"/&gt;
 *     &lt;enumeration value="none"/&gt;
 *     &lt;enumeration value="trucks"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_accessRestrictionType")
@XmlEnum
@SuppressWarnings("all") public enum EAccessRestrictionType {

    @XmlEnumValue("simulator")
    SIMULATOR("simulator"),
    @XmlEnumValue("autonomousTraffic")
    AUTONOMOUS_TRAFFIC("autonomousTraffic"),
    @XmlEnumValue("pedestrian")
    PEDESTRIAN("pedestrian"),
    @XmlEnumValue("passengerCar")
    PASSENGER_CAR("passengerCar"),
    @XmlEnumValue("bus")
    BUS("bus"),
    @XmlEnumValue("delivery")
    DELIVERY("delivery"),
    @XmlEnumValue("emergency")
    EMERGENCY("emergency"),
    @XmlEnumValue("taxi")
    TAXI("taxi"),
    @XmlEnumValue("throughTraffic")
    THROUGH_TRAFFIC("throughTraffic"),
    @XmlEnumValue("truck")
    TRUCK("truck"),
    @XmlEnumValue("bicycle")
    BICYCLE("bicycle"),
    @XmlEnumValue("motorcycle")
    MOTORCYCLE("motorcycle"),
    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("trucks")
    TRUCKS("trucks");
    private final String value;

    EAccessRestrictionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EAccessRestrictionType fromValue(String v) {
        for (EAccessRestrictionType c: EAccessRestrictionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
