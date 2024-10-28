
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_roadMarkWeight.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_roadMarkWeight"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="standard"/&gt;
 *     &lt;enumeration value="bold"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_roadMarkWeight")
@XmlEnum
@SuppressWarnings("all") public enum ERoadMarkWeight {

    @XmlEnumValue("standard")
    STANDARD("standard"),
    @XmlEnumValue("bold")
    BOLD("bold");
    private final String value;

    ERoadMarkWeight(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadMarkWeight fromValue(String v) {
        for (ERoadMarkWeight c: ERoadMarkWeight.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
