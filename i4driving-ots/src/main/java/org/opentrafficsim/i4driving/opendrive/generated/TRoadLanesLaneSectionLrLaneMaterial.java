
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Stores information about the material of lanes. Each element is valid until a new element is defined. If multiple elements are defined, they must be listed in increasing order.
 * 
 * <p>Java class for t_road_lanes_laneSection_lr_lane_material complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lr_lane_material"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="sOffset" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="surface" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="friction" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="roughness" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lr_lane_material")
@SuppressWarnings("all") public class TRoadLanesLaneSectionLrLaneMaterial
    extends OpenDriveElement
{

    @XmlAttribute(name = "sOffset", required = true)
    protected double sOffset;
    @XmlAttribute(name = "surface")
    protected String surface;
    @XmlAttribute(name = "friction", required = true)
    protected double friction;
    @XmlAttribute(name = "roughness")
    protected Double roughness;

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
     * Gets the value of the surface property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurface() {
        return surface;
    }

    /**
     * Sets the value of the surface property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurface(String value) {
        this.surface = value;
    }

    /**
     * Gets the value of the friction property.
     * 
     */
    public double getFriction() {
        return friction;
    }

    /**
     * Sets the value of the friction property.
     * 
     */
    public void setFriction(double value) {
        this.friction = value;
    }

    /**
     * Gets the value of the roughness property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRoughness() {
        return roughness;
    }

    /**
     * Sets the value of the roughness property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRoughness(Double value) {
        this.roughness = value;
    }

}
