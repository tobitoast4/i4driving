
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Data described in OpenCRG are represented by the <CRG> element within the <surface> element.
 * 
 * <p>Java class for t_junction_surface_CRG complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_junction_surface_CRG"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="mode" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_surface_CRG_mode" fixed="global" /&gt;
 *       &lt;attribute name="purpose" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_surface_CRG_purpose" /&gt;
 *       &lt;attribute name="zOffset" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zScale" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_junction_surface_CRG")
@SuppressWarnings("all") public class TJunctionSurfaceCRG
    extends OpenDriveElement
{

    @XmlAttribute(name = "file", required = true)
    protected String file;
    @XmlAttribute(name = "mode", required = true)
    protected ERoadSurfaceCRGMode mode;
    @XmlAttribute(name = "purpose")
    protected ERoadSurfaceCRGPurpose purpose;
    @XmlAttribute(name = "zOffset")
    protected Double zOffset;
    @XmlAttribute(name = "zScale")
    protected Double zScale;

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile(String value) {
        this.file = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadSurfaceCRGMode }
     *     
     */
    public ERoadSurfaceCRGMode getMode() {
        if (mode == null) {
            return ERoadSurfaceCRGMode.GLOBAL;
        } else {
            return mode;
        }
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadSurfaceCRGMode }
     *     
     */
    public void setMode(ERoadSurfaceCRGMode value) {
        this.mode = value;
    }

    /**
     * Gets the value of the purpose property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadSurfaceCRGPurpose }
     *     
     */
    public ERoadSurfaceCRGPurpose getPurpose() {
        return purpose;
    }

    /**
     * Sets the value of the purpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadSurfaceCRGPurpose }
     *     
     */
    public void setPurpose(ERoadSurfaceCRGPurpose value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the zOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getZOffset() {
        return zOffset;
    }

    /**
     * Sets the value of the zOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setZOffset(Double value) {
        this.zOffset = value;
    }

    /**
     * Gets the value of the zScale property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getZScale() {
        return zScale;
    }

    /**
     * Sets the value of the zScale property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setZScale(Double value) {
        this.zScale = value;
    }

}
