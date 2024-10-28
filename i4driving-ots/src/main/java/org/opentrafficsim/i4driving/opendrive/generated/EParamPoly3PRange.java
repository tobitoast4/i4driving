
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_paramPoly3_pRange.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_paramPoly3_pRange"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="arcLength"/&gt;
 *     &lt;enumeration value="normalized"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_paramPoly3_pRange")
@XmlEnum
@SuppressWarnings("all") public enum EParamPoly3PRange {

    @XmlEnumValue("arcLength")
    ARC_LENGTH("arcLength"),
    @XmlEnumValue("normalized")
    NORMALIZED("normalized");
    private final String value;

    EParamPoly3PRange(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EParamPoly3PRange fromValue(String v) {
        for (EParamPoly3PRange c: EParamPoly3PRange.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
