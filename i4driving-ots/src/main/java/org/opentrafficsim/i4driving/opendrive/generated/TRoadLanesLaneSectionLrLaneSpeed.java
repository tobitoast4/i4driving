
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines the maximum allowed speed on a given lane. Each element is valid in direction of the increasing s-coordinate until a new element is defined.
 * 
 * <p>Java class for t_road_lanes_laneSection_lr_lane_speed complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lr_lane_speed"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="sOffset" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="max" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="unit" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_unitSpeed" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lr_lane_speed")
@SuppressWarnings("all") public class TRoadLanesLaneSectionLrLaneSpeed
    extends OpenDriveElement
{

    @XmlAttribute(name = "sOffset", required = true)
    protected double sOffset;
    @XmlAttribute(name = "max", required = true)
    protected double max;
    @XmlAttribute(name = "unit")
    protected EUnitSpeed unit;

    /**
     * Gets the value of the sOffset property.
     * 
     */
    public double getSOffset() {
        return sOffset;
    }

    /**
     * Sets the value of the sOffset property.
     * 
     */
    public void setSOffset(double value) {
        this.sOffset = value;
    }

    /**
     * Gets the value of the max property.
     * 
     */
    public double getMax() {
        return max;
    }

    /**
     * Sets the value of the max property.
     * 
     */
    public void setMax(double value) {
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
