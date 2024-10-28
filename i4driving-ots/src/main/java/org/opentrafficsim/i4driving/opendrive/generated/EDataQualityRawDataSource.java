
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_dataQuality_RawData_Source.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_dataQuality_RawData_Source"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="sensor"/&gt;
 *     &lt;enumeration value="cadaster"/&gt;
 *     &lt;enumeration value="custom"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_dataQuality_RawData_Source")
@XmlEnum
@SuppressWarnings("all") public enum EDataQualityRawDataSource {

    @XmlEnumValue("sensor")
    SENSOR("sensor"),
    @XmlEnumValue("cadaster")
    CADASTER("cadaster"),
    @XmlEnumValue("custom")
    CUSTOM("custom");
    private final String value;

    EDataQualityRawDataSource(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EDataQualityRawDataSource fromValue(String v) {
        for (EDataQualityRawDataSource c: EDataQualityRawDataSource.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
