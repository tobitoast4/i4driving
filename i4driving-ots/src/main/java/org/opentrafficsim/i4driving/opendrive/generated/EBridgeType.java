
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_bridgeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_bridgeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="concrete"/&gt;
 *     &lt;enumeration value="steel"/&gt;
 *     &lt;enumeration value="brick"/&gt;
 *     &lt;enumeration value="wood"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_bridgeType")
@XmlEnum
@SuppressWarnings("all") public enum EBridgeType {

    @XmlEnumValue("concrete")
    CONCRETE("concrete"),
    @XmlEnumValue("steel")
    STEEL("steel"),
    @XmlEnumValue("brick")
    BRICK("brick"),
    @XmlEnumValue("wood")
    WOOD("wood");
    private final String value;

    EBridgeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EBridgeType fromValue(String v) {
        for (EBridgeType c: EBridgeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
