
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
 * Describes common 3D objects that have a reference to a given road. Objects are items that influence a road by expanding, delimiting, and supplementing its course. The most common examples are parking spaces, crosswalks, and traffic barriers.
 * There are two ways to describe the bounding box of objects.
 * - For an angular object: definition of the width, length and height.
 * - For a circular object: definition of the radius and height.
 * 
 * <p>Java class for t_road_objects_object complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="repeat" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_repeat" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="outline" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_outlines_outline" minOccurs="0"/&gt;
 *         &lt;element name="outlines" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_outlines" minOccurs="0"/&gt;
 *         &lt;element name="material" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_material" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="validity" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_laneValidity" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="parkingSpace" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_parkingSpace" minOccurs="0"/&gt;
 *         &lt;element name="markings" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_markings" minOccurs="0"/&gt;
 *         &lt;element name="borders" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_borders" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="t" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="type" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_objectType" /&gt;
 *       &lt;attribute name="validLength" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="orientation" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_orientation" /&gt;
 *       &lt;attribute name="subtype" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="dynamic" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_yesNo" /&gt;
 *       &lt;attribute name="hdg" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="pitch" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="roll" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="s" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="radius" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object", propOrder = {
    "repeat",
    "outline",
    "outlines",
    "material",
    "validity",
    "parkingSpace",
    "markings",
    "borders",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadObjectsObject
    extends OpenDriveElement
{

    protected List<TRoadObjectsObjectRepeat> repeat;
    protected TRoadObjectsObjectOutlinesOutline outline;
    protected TRoadObjectsObjectOutlines outlines;
    protected List<TRoadObjectsObjectMaterial> material;
    protected List<TRoadObjectsObjectLaneValidity> validity;
    protected TRoadObjectsObjectParkingSpace parkingSpace;
    protected TRoadObjectsObjectMarkings markings;
    protected TRoadObjectsObjectBorders borders;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "t", required = true)
    protected double t;
    @XmlAttribute(name = "zOffset", required = true)
    protected double zOffset;
    @XmlAttribute(name = "type")
    protected EObjectType type;
    @XmlAttribute(name = "validLength")
    protected Double validLength;
    @XmlAttribute(name = "orientation")
    protected String orientation;
    @XmlAttribute(name = "subtype")
    protected String subtype;
    @XmlAttribute(name = "dynamic")
    protected TYesNo dynamic;
    @XmlAttribute(name = "hdg")
    protected Double hdg;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "pitch")
    protected Double pitch;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "roll")
    protected Double roll;
    @XmlAttribute(name = "height")
    protected Double height;
    @XmlAttribute(name = "s", required = true)
    protected double s;
    @XmlAttribute(name = "length")
    protected Double length;
    @XmlAttribute(name = "width")
    protected Double width;
    @XmlAttribute(name = "radius")
    protected Double radius;

    /**
     * Gets the value of the repeat property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the repeat property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRepeat().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectRepeat }
     * 
     * 
     */
    public List<TRoadObjectsObjectRepeat> getRepeat() {
        if (repeat == null) {
            repeat = new ArrayList<TRoadObjectsObjectRepeat>();
        }
        return this.repeat;
    }

    /**
     * Gets the value of the outline property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectOutlinesOutline }
     *     
     */
    public TRoadObjectsObjectOutlinesOutline getOutline() {
        return outline;
    }

    /**
     * Sets the value of the outline property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectOutlinesOutline }
     *     
     */
    public void setOutline(TRoadObjectsObjectOutlinesOutline value) {
        this.outline = value;
    }

    /**
     * Gets the value of the outlines property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectOutlines }
     *     
     */
    public TRoadObjectsObjectOutlines getOutlines() {
        return outlines;
    }

    /**
     * Sets the value of the outlines property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectOutlines }
     *     
     */
    public void setOutlines(TRoadObjectsObjectOutlines value) {
        this.outlines = value;
    }

    /**
     * Gets the value of the material property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the material property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterial().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectMaterial }
     * 
     * 
     */
    public List<TRoadObjectsObjectMaterial> getMaterial() {
        if (material == null) {
            material = new ArrayList<TRoadObjectsObjectMaterial>();
        }
        return this.material;
    }

    /**
     * Gets the value of the validity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the validity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValidity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectLaneValidity }
     * 
     * 
     */
    public List<TRoadObjectsObjectLaneValidity> getValidity() {
        if (validity == null) {
            validity = new ArrayList<TRoadObjectsObjectLaneValidity>();
        }
        return this.validity;
    }

    /**
     * Gets the value of the parkingSpace property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectParkingSpace }
     *     
     */
    public TRoadObjectsObjectParkingSpace getParkingSpace() {
        return parkingSpace;
    }

    /**
     * Sets the value of the parkingSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectParkingSpace }
     *     
     */
    public void setParkingSpace(TRoadObjectsObjectParkingSpace value) {
        this.parkingSpace = value;
    }

    /**
     * Gets the value of the markings property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectMarkings }
     *     
     */
    public TRoadObjectsObjectMarkings getMarkings() {
        return markings;
    }

    /**
     * Sets the value of the markings property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectMarkings }
     *     
     */
    public void setMarkings(TRoadObjectsObjectMarkings value) {
        this.markings = value;
    }

    /**
     * Gets the value of the borders property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectBorders }
     *     
     */
    public TRoadObjectsObjectBorders getBorders() {
        return borders;
    }

    /**
     * Sets the value of the borders property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectBorders }
     *     
     */
    public void setBorders(TRoadObjectsObjectBorders value) {
        this.borders = value;
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
     * Gets the value of the t property.
     * 
     */
    public double getT() {
        return t;
    }

    /**
     * Sets the value of the t property.
     * 
     */
    public void setT(double value) {
        this.t = value;
    }

    /**
     * Gets the value of the zOffset property.
     * 
     */
    public double getZOffset() {
        return zOffset;
    }

    /**
     * Sets the value of the zOffset property.
     * 
     */
    public void setZOffset(double value) {
        this.zOffset = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EObjectType }
     *     
     */
    public EObjectType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EObjectType }
     *     
     */
    public void setType(EObjectType value) {
        this.type = value;
    }

    /**
     * Gets the value of the validLength property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getValidLength() {
        return validLength;
    }

    /**
     * Sets the value of the validLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setValidLength(Double value) {
        this.validLength = value;
    }

    /**
     * Gets the value of the orientation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Sets the value of the orientation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrientation(String value) {
        this.orientation = value;
    }

    /**
     * Gets the value of the subtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * Sets the value of the subtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtype(String value) {
        this.subtype = value;
    }

    /**
     * Gets the value of the dynamic property.
     * 
     * @return
     *     possible object is
     *     {@link TYesNo }
     *     
     */
    public TYesNo getDynamic() {
        return dynamic;
    }

    /**
     * Sets the value of the dynamic property.
     * 
     * @param value
     *     allowed object is
     *     {@link TYesNo }
     *     
     */
    public void setDynamic(TYesNo value) {
        this.dynamic = value;
    }

    /**
     * Gets the value of the hdg property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getHdg() {
        return hdg;
    }

    /**
     * Sets the value of the hdg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setHdg(Double value) {
        this.hdg = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the pitch property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getPitch() {
        return pitch;
    }

    /**
     * Sets the value of the pitch property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setPitch(Double value) {
        this.pitch = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the roll property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRoll() {
        return roll;
    }

    /**
     * Sets the value of the roll property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRoll(Double value) {
        this.roll = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setHeight(Double value) {
        this.height = value;
    }

    /**
     * Gets the value of the s property.
     * 
     */
    public double getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     */
    public void setS(double value) {
        this.s = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLength(Double value) {
        this.length = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWidth(Double value) {
        this.width = value;
    }

    /**
     * Gets the value of the radius property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRadius(Double value) {
        this.radius = value;
    }

}
