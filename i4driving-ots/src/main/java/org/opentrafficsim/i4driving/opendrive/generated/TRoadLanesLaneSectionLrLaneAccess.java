
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Defines access restrictions for certain types of road users.
 * Each element is valid in direction of the increasing s co-ordinate until a new element is defined. If multiple elements are defined, they must be listed in increasing order.
 * 
 * <p>Java class for t_road_lanes_laneSection_lr_lane_access complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lr_lane_access"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="sOffset" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="rule" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_lanes_laneSection_lr_lane_access_rule" /&gt;
 *       &lt;attribute name="restriction" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_accessRestrictionType" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lr_lane_access")
@SuppressWarnings("all") public class TRoadLanesLaneSectionLrLaneAccess
    extends OpenDriveElement
{

    @XmlAttribute(name = "sOffset", required = true)
    protected double sOffset;
    @XmlAttribute(name = "rule")
    protected ERoadLanesLaneSectionLrLaneAccessRule rule;
    @XmlAttribute(name = "restriction", required = true)
    protected EAccessRestrictionType restriction;

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
     *     {@link ERoadLanesLaneSectionLrLaneAccessRule }
     *     
     */
    public ERoadLanesLaneSectionLrLaneAccessRule getRule() {
        return rule;
    }

    /**
     * Sets the value of the rule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadLanesLaneSectionLrLaneAccessRule }
     *     
     */
    public void setRule(ERoadLanesLaneSectionLrLaneAccessRule value) {
        this.rule = value;
    }

    /**
     * Gets the value of the restriction property.
     * 
     * @return
     *     possible object is
     *     {@link EAccessRestrictionType }
     *     
     */
    public EAccessRestrictionType getRestriction() {
        return restriction;
    }

    /**
     * Sets the value of the restriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link EAccessRestrictionType }
     *     
     */
    public void setRestriction(EAccessRestrictionType value) {
        this.restriction = value;
    }

}
