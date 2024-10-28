
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies a marking that is either attached to one side of the object bounding box or referencing outline points.
 * 
 * <p>Java class for t_road_objects_object_markings_marking complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_markings_marking"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cornerReference" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_markings_marking_cornerReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="side" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_sideType" /&gt;
 *       &lt;attribute name="weight" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_roadMarkWeight" /&gt;
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="color" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_roadMarkColor" /&gt;
 *       &lt;attribute name="zOffset" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="spaceLength" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="lineLength" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="startOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="stopOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_markings_marking", propOrder = {
    "cornerReference",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadObjectsObjectMarkingsMarking
    extends OpenDriveElement
{

    protected List<TRoadObjectsObjectMarkingsMarkingCornerReference> cornerReference;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "side")
    protected ESideType side;
    @XmlAttribute(name = "weight")
    protected ERoadMarkWeight weight;
    @XmlAttribute(name = "width")
    protected String width;
    @XmlAttribute(name = "color", required = true)
    protected ERoadMarkColor color;
    @XmlAttribute(name = "zOffset")
    protected Double zOffset;
    @XmlAttribute(name = "spaceLength", required = true)
    protected double spaceLength;
    @XmlAttribute(name = "lineLength", required = true)
    protected String lineLength;
    @XmlAttribute(name = "startOffset", required = true)
    protected double startOffset;
    @XmlAttribute(name = "stopOffset", required = true)
    protected double stopOffset;

    /**
     * Gets the value of the cornerReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cornerReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCornerReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectMarkingsMarkingCornerReference }
     * 
     * 
     */
    public List<TRoadObjectsObjectMarkingsMarkingCornerReference> getCornerReference() {
        if (cornerReference == null) {
            cornerReference = new ArrayList<TRoadObjectsObjectMarkingsMarkingCornerReference>();
        }
        return this.cornerReference;
    }

    /**
     * Gets the value of the gAdditionalData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gAdditionalData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGAdditionalData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDataQuality }
     * {@link TInclude }
     * {@link TUserData }
     * 
     * 
     */
    public List<Object> getGAdditionalData() {
        if (gAdditionalData == null) {
            gAdditionalData = new ArrayList<Object>();
        }
        return this.gAdditionalData;
    }

    /**
     * Gets the value of the side property.
     * 
     * @return
     *     possible object is
     *     {@link ESideType }
     *     
     */
    public ESideType getSide() {
        return side;
    }

    /**
     * Sets the value of the side property.
     * 
     * @param value
     *     allowed object is
     *     {@link ESideType }
     *     
     */
    public void setSide(ESideType value) {
        this.side = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkWeight }
     *     
     */
    public ERoadMarkWeight getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkWeight }
     *     
     */
    public void setWeight(ERoadMarkWeight value) {
        this.weight = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWidth(String value) {
        this.width = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkColor }
     *     
     */
    public ERoadMarkColor getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkColor }
     *     
     */
    public void setColor(ERoadMarkColor value) {
        this.color = value;
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
     * Gets the value of the spaceLength property.
     * 
     */
    public double getSpaceLength() {
        return spaceLength;
    }

    /**
     * Sets the value of the spaceLength property.
     * 
     */
    public void setSpaceLength(double value) {
        this.spaceLength = value;
    }

    /**
     * Gets the value of the lineLength property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineLength() {
        return lineLength;
    }

    /**
     * Sets the value of the lineLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineLength(String value) {
        this.lineLength = value;
    }

    /**
     * Gets the value of the startOffset property.
     * 
     */
    public double getStartOffset() {
        return startOffset;
    }

    /**
     * Sets the value of the startOffset property.
     * 
     */
    public void setStartOffset(double value) {
        this.startOffset = value;
    }

    /**
     * Gets the value of the stopOffset property.
     * 
     */
    public double getStopOffset() {
        return stopOffset;
    }

    /**
     * Sets the value of the stopOffset property.
     * 
     */
    public void setStopOffset(double value) {
        this.stopOffset = value;
    }

}
