
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
 * Defines a series of corner points, including the height of the object relative to the road reference line. For areas, the points should be listed in counter-clockwise order.
 * An <outline> element shall be followed by one or more <cornerRoad> element or by one or more <cornerLocal> element.
 * 
 * OpenDRIVE 1.4 outline definitions (without <outlines> parent element) shall still be supported.
 * 
 * <p>Java class for t_road_objects_object_outlines_outline complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_outlines_outline"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="cornerRoad" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_outlines_outline_cornerRoad" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="cornerLocal" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_outlines_outline_cornerLocal" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" /&gt;
 *       &lt;attribute name="fillType" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_outlineFillType" /&gt;
 *       &lt;attribute name="outer" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_bool" /&gt;
 *       &lt;attribute name="closed" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_bool" /&gt;
 *       &lt;attribute name="laneType" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_laneType" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_outlines_outline", propOrder = {
    "cornerRoad",
    "cornerLocal",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadObjectsObjectOutlinesOutline
    extends OpenDriveElement
{

    protected List<TRoadObjectsObjectOutlinesOutlineCornerRoad> cornerRoad;
    protected List<TRoadObjectsObjectOutlinesOutlineCornerLocal> cornerLocal;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "id")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger id;
    @XmlAttribute(name = "fillType")
    protected EOutlineFillType fillType;
    @XmlAttribute(name = "outer")
    protected TBool outer;
    @XmlAttribute(name = "closed")
    protected TBool closed;
    @XmlAttribute(name = "laneType")
    protected ELaneType laneType;

    /**
     * Gets the value of the cornerRoad property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cornerRoad property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCornerRoad().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectOutlinesOutlineCornerRoad }
     * 
     * 
     */
    public List<TRoadObjectsObjectOutlinesOutlineCornerRoad> getCornerRoad() {
        if (cornerRoad == null) {
            cornerRoad = new ArrayList<TRoadObjectsObjectOutlinesOutlineCornerRoad>();
        }
        return this.cornerRoad;
    }

    /**
     * Gets the value of the cornerLocal property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cornerLocal property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCornerLocal().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectOutlinesOutlineCornerLocal }
     * 
     * 
     */
    public List<TRoadObjectsObjectOutlinesOutlineCornerLocal> getCornerLocal() {
        if (cornerLocal == null) {
            cornerLocal = new ArrayList<TRoadObjectsObjectOutlinesOutlineCornerLocal>();
        }
        return this.cornerLocal;
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

    /**
     * Gets the value of the fillType property.
     * 
     * @return
     *     possible object is
     *     {@link EOutlineFillType }
     *     
     */
    public EOutlineFillType getFillType() {
        return fillType;
    }

    /**
     * Sets the value of the fillType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EOutlineFillType }
     *     
     */
    public void setFillType(EOutlineFillType value) {
        this.fillType = value;
    }

    /**
     * Gets the value of the outer property.
     * 
     * @return
     *     possible object is
     *     {@link TBool }
     *     
     */
    public TBool getOuter() {
        return outer;
    }

    /**
     * Sets the value of the outer property.
     * 
     * @param value
     *     allowed object is
     *     {@link TBool }
     *     
     */
    public void setOuter(TBool value) {
        this.outer = value;
    }

    /**
     * Gets the value of the closed property.
     * 
     * @return
     *     possible object is
     *     {@link TBool }
     *     
     */
    public TBool getClosed() {
        return closed;
    }

    /**
     * Sets the value of the closed property.
     * 
     * @param value
     *     allowed object is
     *     {@link TBool }
     *     
     */
    public void setClosed(TBool value) {
        this.closed = value;
    }

    /**
     * Gets the value of the laneType property.
     * 
     * @return
     *     possible object is
     *     {@link ELaneType }
     *     
     */
    public ELaneType getLaneType() {
        return laneType;
    }

    /**
     * Sets the value of the laneType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ELaneType }
     *     
     */
    public void setLaneType(ELaneType value) {
        this.laneType = value;
    }

}
