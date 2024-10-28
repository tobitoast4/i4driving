
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_trafficRule.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="e_trafficRule"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="RHT"/&gt;
 *     &lt;enumeration value="LHT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "e_trafficRule")
@XmlEnum
@SuppressWarnings("all") public enum ETrafficRule {

    RHT,
    LHT;

    public String value() {
        return name();
    }

    public static ETrafficRule fromValue(String v) {
        return valueOf(v);
    }

}
