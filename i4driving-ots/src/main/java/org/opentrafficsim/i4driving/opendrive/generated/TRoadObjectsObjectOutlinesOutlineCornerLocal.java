
package org.opentrafficsim.i4driving.opendrive.generated;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Used to describe complex forms of objects. Defines a corner point on the object outline relative to the object pivot point in local u/v-coordinates. The pivot point and the orientation of the object are given by the s/t/heading arguments of the <object> element.
 * 
 * <p>Java class for t_road_objects_object_outlines_outline_cornerLocal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_outlines_outline_cornerLocal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="u" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="v" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="z" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="height" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_outlines_outline_cornerLocal")
@SuppressWarnings("all") public class TRoadObjectsObjectOutlinesOutlineCornerLocal
    extends OpenDriveElement
{

    @XmlAttribute(name = "u", required = true)
    protected double u;
    @XmlAttribute(name = "v", required = true)
    protected double v;
    @XmlAttribute(name = "z", required = true)
    protected double z;
    @XmlAttribute(name = "height", required = true)
    protected double height;
    @XmlAttribute(name = "id")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger id;

    /**
     * Gets the value of the u property.
     * 
     */
    public double getU() {
        return u;
    }

    /**
     * Sets the value of the u property.
     * 
     */
    public void setU(double value) {
        this.u = value;
    }

    /**
     * Gets the value of the v property.
     * 
     */
    public double getV() {
        return v;
    }

    /**
     * Sets the value of the v property.
     * 
     */
    public void setV(double value) {
        this.v = value;
    }

    /**
     * Gets the value of the z property.
     * 
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the value of the z property.
     * 
     */
    public void setZ(double value) {
        this.z = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(double value) {
        this.height = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

}
