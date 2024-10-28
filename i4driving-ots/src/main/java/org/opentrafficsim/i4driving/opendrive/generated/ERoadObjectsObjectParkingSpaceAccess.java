
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_objects_object_parkingSpace_access.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_objects_object_parkingSpace_access"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="all"/&gt;
 *     &lt;enumeration value="car"/&gt;
 *     &lt;enumeration value="women"/&gt;
 *     &lt;enumeration value="handicapped"/&gt;
 *     &lt;enumeration value="bus"/&gt;
 *     &lt;enumeration value="truck"/&gt;
 *     &lt;enumeration value="electric"/&gt;
 *     &lt;enumeration value="residents"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_objects_object_parkingSpace_access")
@XmlEnum
@SuppressWarnings("all") public enum ERoadObjectsObjectParkingSpaceAccess {

    @XmlEnumValue("all")
    ALL("all"),
    @XmlEnumValue("car")
    CAR("car"),
    @XmlEnumValue("women")
    WOMEN("women"),
    @XmlEnumValue("handicapped")
    HANDICAPPED("handicapped"),
    @XmlEnumValue("bus")
    BUS("bus"),
    @XmlEnumValue("truck")
    TRUCK("truck"),
    @XmlEnumValue("electric")
    ELECTRIC("electric"),
    @XmlEnumValue("residents")
    RESIDENTS("residents");
    private final String value;

    ERoadObjectsObjectParkingSpaceAccess(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadObjectsObjectParkingSpaceAccess fromValue(String v) {
        for (ERoadObjectsObjectParkingSpaceAccess c: ERoadObjectsObjectParkingSpaceAccess.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
