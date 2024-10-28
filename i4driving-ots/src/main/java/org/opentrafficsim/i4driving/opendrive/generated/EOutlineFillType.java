
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_outlineFillType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_outlineFillType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="grass"/&gt;
 *     &lt;enumeration value="concrete"/&gt;
 *     &lt;enumeration value="cobble"/&gt;
 *     &lt;enumeration value="asphalt"/&gt;
 *     &lt;enumeration value="pavement"/&gt;
 *     &lt;enumeration value="gravel"/&gt;
 *     &lt;enumeration value="soil"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_outlineFillType")
@XmlEnum
@SuppressWarnings("all") public enum EOutlineFillType {

    @XmlEnumValue("grass")
    GRASS("grass"),
    @XmlEnumValue("concrete")
    CONCRETE("concrete"),
    @XmlEnumValue("cobble")
    COBBLE("cobble"),
    @XmlEnumValue("asphalt")
    ASPHALT("asphalt"),
    @XmlEnumValue("pavement")
    PAVEMENT("pavement"),
    @XmlEnumValue("gravel")
    GRAVEL("gravel"),
    @XmlEnumValue("soil")
    SOIL("soil");
    private final String value;

    EOutlineFillType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EOutlineFillType fromValue(String v) {
        for (EOutlineFillType c: EOutlineFillType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
