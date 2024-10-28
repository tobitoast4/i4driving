
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies a single line in an explicit road mark definition.
 * 
 * <p>Java class for t_road_lanes_laneSection_lcr_lane_roadMark_explicit_line complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lcr_lane_roadMark_explicit_line"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="length" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="tOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="sOffset" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="rule" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_roadMarkRule" /&gt;
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lcr_lane_roadMark_explicit_line")
@SuppressWarnings("all") public class TRoadLanesLaneSectionLcrLaneRoadMarkExplicitLine
    extends OpenDriveElement
{

    @XmlAttribute(name = "length", required = true)
    protected String length;
    @XmlAttribute(name = "tOffset", required = true)
    protected double tOffset;
    @XmlAttribute(name = "sOffset", required = true)
    protected double sOffset;
    @XmlAttribute(name = "rule")
    protected ERoadMarkRule rule;
    @XmlAttribute(name = "width")
    protected String width;

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLength(String value) {
        this.length = value;
    }

    /**
     * Gets the value of the tOffset property.
     * 
     */
    public double getTOffset() {
        return tOffset;
    }

    /**
     * Sets the value of the tOffset property.
     * 
     */
    public void setTOffset(double value) {
        this.tOffset = value;
    }

    /**
     * Gets the value of the sOffset property.
     * 
     */
    public double getSOffset() {
        return sOffset;
    }

    /**
     * Sets the value of the sOffset property.
     * 
     */
    public void setSOffset(double value) {
        this.sOffset = value;
    }

    /**
     * Gets the value of the rule property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkRule }
     *     
     */
    public ERoadMarkRule getRule() {
        return rule;
    }

    /**
     * Sets the value of the rule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkRule }
     *     
     */
    public void setRule(ERoadMarkRule value) {
        this.rule = value;
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

}
