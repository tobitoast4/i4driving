
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_road_signals_signal_reference_elementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_road_signals_signal_reference_elementType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="object"/&gt;
 *     &lt;enumeration value="signal"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_road_signals_signal_reference_elementType")
@XmlEnum
@SuppressWarnings("all") public enum ERoadSignalsSignalReferenceElementType {

    @XmlEnumValue("object")
    OBJECT("object"),
    @XmlEnumValue("signal")
    SIGNAL("signal");
    private final String value;

    ERoadSignalsSignalReferenceElementType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadSignalsSignalReferenceElementType fromValue(String v) {
        for (ERoadSignalsSignalReferenceElementType c: ERoadSignalsSignalReferenceElementType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
