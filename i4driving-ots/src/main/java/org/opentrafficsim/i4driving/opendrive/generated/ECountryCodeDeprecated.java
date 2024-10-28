
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_countryCode_deprecated.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_countryCode_deprecated"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OpenDRIVE"/&gt;
 *     &lt;enumeration value="Austria"/&gt;
 *     &lt;enumeration value="Brazil"/&gt;
 *     &lt;enumeration value="China"/&gt;
 *     &lt;enumeration value="France"/&gt;
 *     &lt;enumeration value="Germany"/&gt;
 *     &lt;enumeration value="Italy"/&gt;
 *     &lt;enumeration value="Switzerland"/&gt;
 *     &lt;enumeration value="USA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_countryCode_deprecated")
@XmlEnum
@SuppressWarnings("all") public enum ECountryCodeDeprecated {

    @XmlEnumValue("OpenDRIVE")
    OPEN_DRIVE("OpenDRIVE"),
    @XmlEnumValue("Austria")
    AUSTRIA("Austria"),
    @XmlEnumValue("Brazil")
    BRAZIL("Brazil"),
    @XmlEnumValue("China")
    CHINA("China"),
    @XmlEnumValue("France")
    FRANCE("France"),
    @XmlEnumValue("Germany")
    GERMANY("Germany"),
    @XmlEnumValue("Italy")
    ITALY("Italy"),
    @XmlEnumValue("Switzerland")
    SWITZERLAND("Switzerland"),
    USA("USA");
    private final String value;

    ECountryCodeDeprecated(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ECountryCodeDeprecated fromValue(String v) {
        for (ECountryCodeDeprecated c: ECountryCodeDeprecated.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
