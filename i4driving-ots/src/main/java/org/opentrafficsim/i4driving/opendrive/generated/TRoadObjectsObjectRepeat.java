
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * To avoid lengthy XML code, objects of the same type may be repeated. The attributes of the repeated object may be changed. Attributes of the repeated object shall overrule the attributes from the original object. If attributes are omitted in the repeated objects, the attributes from the original object apply.
 * 
 * <p>Java class for t_road_objects_object_repeat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_repeat"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="s" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="length" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="distance" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="tStart" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="tEnd" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="heightStart" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="heightEnd" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zOffsetStart" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zOffsetEnd" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="widthStart" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="widthEnd" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="lengthStart" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="lengthEnd" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="radiusStart" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="radiusEnd" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_repeat")
@SuppressWarnings("all") public class TRoadObjectsObjectRepeat
    extends OpenDriveElement
{

    @XmlAttribute(name = "s", required = true)
    protected double s;
    @XmlAttribute(name = "length", required = true)
    protected double length;
    @XmlAttribute(name = "distance", required = true)
    protected double distance;
    @XmlAttribute(name = "tStart", required = true)
    protected double tStart;
    @XmlAttribute(name = "tEnd", required = true)
    protected double tEnd;
    @XmlAttribute(name = "heightStart", required = true)
    protected double heightStart;
    @XmlAttribute(name = "heightEnd", required = true)
    protected double heightEnd;
    @XmlAttribute(name = "zOffsetStart", required = true)
    protected double zOffsetStart;
    @XmlAttribute(name = "zOffsetEnd", required = true)
    protected double zOffsetEnd;
    @XmlAttribute(name = "widthStart")
    protected Double widthStart;
    @XmlAttribute(name = "widthEnd")
    protected Double widthEnd;
    @XmlAttribute(name = "lengthStart")
    protected Double lengthStart;
    @XmlAttribute(name = "lengthEnd")
    protected Double lengthEnd;
    @XmlAttribute(name = "radiusStart")
    protected Double radiusStart;
    @XmlAttribute(name = "radiusEnd")
    protected Double radiusEnd;

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
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     */
    public void setLength(double value) {
        this.length = value;
    }

    /**
     * Gets the value of the distance property.
     * 
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the value of the distance property.
     * 
     */
    public void setDistance(double value) {
        this.distance = value;
    }

    /**
     * Gets the value of the tStart property.
     * 
     */
    public double getTStart() {
        return tStart;
    }

    /**
     * Sets the value of the tStart property.
     * 
     */
    public void setTStart(double value) {
        this.tStart = value;
    }

    /**
     * Gets the value of the tEnd property.
     * 
     */
    public double getTEnd() {
        return tEnd;
    }

    /**
     * Sets the value of the tEnd property.
     * 
     */
    public void setTEnd(double value) {
        this.tEnd = value;
    }

    /**
     * Gets the value of the heightStart property.
     * 
     */
    public double getHeightStart() {
        return heightStart;
    }

    /**
     * Sets the value of the heightStart property.
     * 
     */
    public void setHeightStart(double value) {
        this.heightStart = value;
    }

    /**
     * Gets the value of the heightEnd property.
     * 
     */
    public double getHeightEnd() {
        return heightEnd;
    }

    /**
     * Sets the value of the heightEnd property.
     * 
     */
    public void setHeightEnd(double value) {
        this.heightEnd = value;
    }

    /**
     * Gets the value of the zOffsetStart property.
     * 
     */
    public double getZOffsetStart() {
        return zOffsetStart;
    }

    /**
     * Sets the value of the zOffsetStart property.
     * 
     */
    public void setZOffsetStart(double value) {
        this.zOffsetStart = value;
    }

    /**
     * Gets the value of the zOffsetEnd property.
     * 
     */
    public double getZOffsetEnd() {
        return zOffsetEnd;
    }

    /**
     * Sets the value of the zOffsetEnd property.
     * 
     */
    public void setZOffsetEnd(double value) {
        this.zOffsetEnd = value;
    }

    /**
     * Gets the value of the widthStart property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWidthStart() {
        return widthStart;
    }

    /**
     * Sets the value of the widthStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWidthStart(Double value) {
        this.widthStart = value;
    }

    /**
     * Gets the value of the widthEnd property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWidthEnd() {
        return widthEnd;
    }

    /**
     * Sets the value of the widthEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWidthEnd(Double value) {
        this.widthEnd = value;
    }

    /**
     * Gets the value of the lengthStart property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLengthStart() {
        return lengthStart;
    }

    /**
     * Sets the value of the lengthStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLengthStart(Double value) {
        this.lengthStart = value;
    }

    /**
     * Gets the value of the lengthEnd property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLengthEnd() {
        return lengthEnd;
    }

    /**
     * Sets the value of the lengthEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLengthEnd(Double value) {
        this.lengthEnd = value;
    }

    /**
     * Gets the value of the radiusStart property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRadiusStart() {
        return radiusStart;
    }

    /**
     * Sets the value of the radiusStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRadiusStart(Double value) {
        this.radiusStart = value;
    }

    /**
     * Gets the value of the radiusEnd property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRadiusEnd() {
        return radiusEnd;
    }

    /**
     * Sets the value of the radiusEnd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRadiusEnd(Double value) {
        this.radiusEnd = value;
    }

}
