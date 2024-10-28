
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_roadMarkRule.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_roadMarkRule"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="no passing"/&gt;
 *     &lt;enumeration value="caution"/&gt;
 *     &lt;enumeration value="none"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_roadMarkRule")
@XmlEnum
@SuppressWarnings("all") public enum ERoadMarkRule {

    @XmlEnumValue("no passing")
    NO_PASSING("no passing"),
    @XmlEnumValue("caution")
    CAUTION("caution"),
    @XmlEnumValue("none")
    NONE("none");
    private final String value;

    ERoadMarkRule(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadMarkRule fromValue(String v) {
        for (ERoadMarkRule c: ERoadMarkRule.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
