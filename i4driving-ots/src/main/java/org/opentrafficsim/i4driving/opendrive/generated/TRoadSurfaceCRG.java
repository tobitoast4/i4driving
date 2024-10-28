
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Data described in OpenCRG is represented by the <CRG> element within the <surface> element.
 * 
 * <p>Java class for t_road_surface_CRG complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_surface_CRG"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="file" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="sStart" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="sEnd" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="orientation" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_direction" /&gt;
 *       &lt;attribute name="mode" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_surface_CRG_mode" /&gt;
 *       &lt;attribute name="purpose" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_surface_CRG_purpose" /&gt;
 *       &lt;attribute name="sOffset" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="tOffset" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zOffset" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zScale" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="hOffset" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_surface_CRG")
@SuppressWarnings("all") public class TRoadSurfaceCRG
    extends OpenDriveElement
{

    @XmlAttribute(name = "file", required = true)
    protected String file;
    @XmlAttribute(name = "sStart", required = true)
    protected double sStart;
    @XmlAttribute(name = "sEnd", required = true)
    protected double sEnd;
    @XmlAttribute(name = "orientation", required = true)
    protected EDirection orientation;
    @XmlAttribute(name = "mode", required = true)
    protected ERoadSurfaceCRGMode mode;
    @XmlAttribute(name = "purpose")
    protected ERoadSurfaceCRGPurpose purpose;
    @XmlAttribute(name = "sOffset")
    protected Double sOffset;
    @XmlAttribute(name = "tOffset")
    protected Double tOffset;
    @XmlAttribute(name = "zOffset")
    protected Double zOffset;
    @XmlAttribute(name = "zScale")
    protected Double zScale;
    @XmlAttribute(name = "hOffset")
    protected Double hOffset;

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
     * Gets the value of the sStart property.
     * 
     */
    public double getSStart() {
        return sStart;
    }

    /**
     * Sets the value of the sStart property.
     * 
     */
    public void setSStart(double value) {
        this.sStart = value;
    }

    /**
     * Gets the value of the sEnd property.
     * 
     */
    public double getSEnd() {
        return sEnd;
    }

    /**
     * Sets the value of the sEnd property.
     * 
     */
    public void setSEnd(double value) {
        this.sEnd = value;
    }

    /**
     * Gets the value of the orientation property.
     * 
     * @return
     *     possible object is
     *     {@link EDirection }
     *     
     */
    public EDirection getOrientation() {
        return orientation;
    }

    /**
     * Sets the value of the orientation property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDirection }
     *     
     */
    public void setOrientation(EDirection value) {
        this.orientation = value;
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
        return mode;
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
     * Gets the value of the sOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSOffset() {
        return sOffset;
    }

    /**
     * Sets the value of the sOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSOffset(Double value) {
        this.sOffset = value;
    }

    /**
     * Gets the value of the tOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getTOffset() {
        return tOffset;
    }

    /**
     * Sets the value of the tOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setTOffset(Double value) {
        this.tOffset = value;
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

    /**
     * Gets the value of the hOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getHOffset() {
        return hOffset;
    }

    /**
     * Sets the value of the hOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setHOffset(Double value) {
        this.hOffset = value;
    }

}
