
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Describes the material properties of objects, for example, patches that are part of the road surface but deviate from the standard road material. Supersedes the material specified in the <road material> element and is valid only within the outline of the parent road object.
 * 
 * <p>Java class for t_road_objects_object_material complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_material"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="surface" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="friction" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="roughness" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_material")
@SuppressWarnings("all") public class TRoadObjectsObjectMaterial
    extends OpenDriveElement
{

    @XmlAttribute(name = "surface")
    protected String surface;
    @XmlAttribute(name = "friction")
    protected Double friction;
    @XmlAttribute(name = "roughness")
    protected Double roughness;

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
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getFriction() {
        return friction;
    }

    /**
     * Sets the value of the friction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setFriction(Double value) {
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
