
package org.opentrafficsim.i4driving.opendrive.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies a border along certain outline points.
 * 
 * <p>Java class for t_road_objects_object_borders_border complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_borders_border"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cornerReference" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_markings_marking_cornerReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="width" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="type" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_borderType" /&gt;
 *       &lt;attribute name="outlineId" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" /&gt;
 *       &lt;attribute name="useCompleteOutline" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_bool" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_borders_border", propOrder = {
    "cornerReference",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadObjectsObjectBordersBorder
    extends OpenDriveElement
{

    protected List<TRoadObjectsObjectMarkingsMarkingCornerReference> cornerReference;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "width", required = true)
    protected double width;
    @XmlAttribute(name = "type", required = true)
    protected EBorderType type;
    @XmlAttribute(name = "outlineId", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger outlineId;
    @XmlAttribute(name = "useCompleteOutline")
    protected TBool useCompleteOutline;

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
     * Gets the value of the width property.
     * 
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     */
    public void setWidth(double value) {
        this.width = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EBorderType }
     *     
     */
    public EBorderType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EBorderType }
     *     
     */
    public void setType(EBorderType value) {
        this.type = value;
    }

    /**
     * Gets the value of the outlineId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOutlineId() {
        return outlineId;
    }

    /**
     * Sets the value of the outlineId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOutlineId(BigInteger value) {
        this.outlineId = value;
    }

    /**
     * Gets the value of the useCompleteOutline property.
     * 
     * @return
     *     possible object is
     *     {@link TBool }
     *     
     */
    public TBool getUseCompleteOutline() {
        return useCompleteOutline;
    }

    /**
     * Sets the value of the useCompleteOutline property.
     * 
     * @param value
     *     allowed object is
     *     {@link TBool }
     *     
     */
    public void setUseCompleteOutline(TBool value) {
        this.useCompleteOutline = value;
    }

}
