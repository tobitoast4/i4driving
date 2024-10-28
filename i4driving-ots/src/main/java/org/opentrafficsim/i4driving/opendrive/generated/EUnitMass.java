
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_unitMass.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_unitMass"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="kg"/&gt;
 *     &lt;enumeration value="t"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_unitMass")
@XmlEnum
@SuppressWarnings("all") public enum EUnitMass {

    @XmlEnumValue("kg")
    KG("kg"),
    @XmlEnumValue("t")
    T("t");
    private final String value;

    EUnitMass(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EUnitMass fromValue(String v) {
        for (EUnitMass c: EUnitMass.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
