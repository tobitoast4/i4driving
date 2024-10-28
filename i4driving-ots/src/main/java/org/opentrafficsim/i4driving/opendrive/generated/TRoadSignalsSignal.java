
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
 * Used to provide information about signals along a road. Consists of a main element and an optional lane validity element.
 * 
 * <p>Java class for t_road_signals_signal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_signals_signal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="validity" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object_laneValidity" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dependency" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_signals_signal_dependency" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="reference" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_signals_signal_reference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="positionRoad" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_signals_signal_positionRoad"/&gt;
 *           &lt;element name="positionInertial" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_signals_signal_positionInertial"/&gt;
 *         &lt;/choice&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="s" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="t" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="dynamic" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_yesNo" /&gt;
 *       &lt;attribute name="orientation" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_orientation" /&gt;
 *       &lt;attribute name="zOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="country" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_countryCode" /&gt;
 *       &lt;attribute name="countryRevision" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="subtype" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="unit" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_unit" /&gt;
 *       &lt;attribute name="height" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="width" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="hOffset" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="pitch" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="roll" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_signals_signal", propOrder = {
    "validity",
    "dependency",
    "reference",
    "positionRoad",
    "positionInertial",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadSignalsSignal
    extends OpenDriveElement
{

    protected List<TRoadObjectsObjectLaneValidity> validity;
    protected List<TRoadSignalsSignalDependency> dependency;
    protected List<TRoadSignalsSignalReference2> reference;
    protected TRoadSignalsSignalPositionRoad positionRoad;
    protected TRoadSignalsSignalPositionInertial positionInertial;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "s", required = true)
    protected double s;
    @XmlAttribute(name = "t", required = true)
    protected double t;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "dynamic", required = true)
    protected TYesNo dynamic;
    @XmlAttribute(name = "orientation", required = true)
    protected String orientation;
    @XmlAttribute(name = "zOffset", required = true)
    protected double zOffset;
    @XmlAttribute(name = "country")
    protected String country;
    @XmlAttribute(name = "countryRevision")
    protected String countryRevision;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "subtype", required = true)
    protected String subtype;
    @XmlAttribute(name = "value")
    protected Double value;
    @XmlAttribute(name = "unit")
    protected String unit;
    @XmlAttribute(name = "height")
    protected Double height;
    @XmlAttribute(name = "width")
    protected Double width;
    @XmlAttribute(name = "text")
    protected String text;
    @XmlAttribute(name = "hOffset")
    protected Double hOffset;
    @XmlAttribute(name = "pitch")
    protected Double pitch;
    @XmlAttribute(name = "roll")
    protected Double roll;

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
     * Gets the value of the dependency property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dependency property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDependency().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadSignalsSignalDependency }
     * 
     * 
     */
    public List<TRoadSignalsSignalDependency> getDependency() {
        if (dependency == null) {
            dependency = new ArrayList<TRoadSignalsSignalDependency>();
        }
        return this.dependency;
    }

    /**
     * Gets the value of the reference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadSignalsSignalReference2 }
     * 
     * 
     */
    public List<TRoadSignalsSignalReference2> getReference() {
        if (reference == null) {
            reference = new ArrayList<TRoadSignalsSignalReference2>();
        }
        return this.reference;
    }

    /**
     * Gets the value of the positionRoad property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadSignalsSignalPositionRoad }
     *     
     */
    public TRoadSignalsSignalPositionRoad getPositionRoad() {
        return positionRoad;
    }

    /**
     * Sets the value of the positionRoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadSignalsSignalPositionRoad }
     *     
     */
    public void setPositionRoad(TRoadSignalsSignalPositionRoad value) {
        this.positionRoad = value;
    }

    /**
     * Gets the value of the positionInertial property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadSignalsSignalPositionInertial }
     *     
     */
    public TRoadSignalsSignalPositionInertial getPositionInertial() {
        return positionInertial;
    }

    /**
     * Sets the value of the positionInertial property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadSignalsSignalPositionInertial }
     *     
     */
    public void setPositionInertial(TRoadSignalsSignalPositionInertial value) {
        this.positionInertial = value;
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
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the countryRevision property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryRevision() {
        return countryRevision;
    }

    /**
     * Sets the value of the countryRevision property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryRevision(String value) {
        this.countryRevision = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
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
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
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
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
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

}
