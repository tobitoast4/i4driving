
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the default maximum speed allowed in conjunction with the specified road type.
 * 
 * <p>Java class for t_road_type_speed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_type_speed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="max" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_maxSpeed" /&gt;
 *       &lt;attribute name="unit" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_unitSpeed" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_type_speed")
@SuppressWarnings("all") public class TRoadTypeSpeed
    extends OpenDriveElement
{

    @XmlAttribute(name = "max", required = true)
    protected String max;
    @XmlAttribute(name = "unit")
    protected EUnitSpeed unit;

    /**
     * Gets the value of the max property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMax(String value) {
        this.max = value;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link EUnitSpeed }
     *     
     */
    public EUnitSpeed getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link EUnitSpeed }
     *     
     */
    public void setUnit(EUnitSpeed value) {
        this.unit = value;
    }

}
