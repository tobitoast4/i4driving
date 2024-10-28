
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_dataQuality_RawData_PostProcessing.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_dataQuality_RawData_PostProcessing"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="raw"/&gt;
 *     &lt;enumeration value="cleaned"/&gt;
 *     &lt;enumeration value="processed"/&gt;
 *     &lt;enumeration value="fused"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_dataQuality_RawData_PostProcessing")
@XmlEnum
@SuppressWarnings("all") public enum EDataQualityRawDataPostProcessing {

    @XmlEnumValue("raw")
    RAW("raw"),
    @XmlEnumValue("cleaned")
    CLEANED("cleaned"),
    @XmlEnumValue("processed")
    PROCESSED("processed"),
    @XmlEnumValue("fused")
    FUSED("fused");
    private final String value;

    EDataQualityRawDataPostProcessing(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EDataQualityRawDataPostProcessing fromValue(String v) {
        for (EDataQualityRawDataPostProcessing c: EDataQualityRawDataPostProcessing.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
