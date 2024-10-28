
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_borderType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_borderType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="concrete"/&gt;
 *     &lt;enumeration value="curb"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_borderType")
@XmlEnum
@SuppressWarnings("all") public enum EBorderType {

    @XmlEnumValue("concrete")
    CONCRETE("concrete"),
    @XmlEnumValue("curb")
    CURB("curb");
    private final String value;

    EBorderType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EBorderType fromValue(String v) {
        for (EBorderType c: EBorderType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
