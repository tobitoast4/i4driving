
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Lane height shall be defined along the h-coordinate. Lane height may be used to elevate a lane independent from the road elevation. Lane height is used to implement small-scale elevation such as raising pedestrian walkways. Lane height is specified as offset from the road (including elevation, superelevation, shape) in z direction.
 * 
 * <p>Java class for t_road_lanes_laneSection_lr_lane_height complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lr_lane_height"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="sOffset" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="inner" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="outer" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lr_lane_height")
@SuppressWarnings("all") public class TRoadLanesLaneSectionLrLaneHeight
    extends OpenDriveElement
{

    @XmlAttribute(name = "sOffset", required = true)
    protected double sOffset;
    @XmlAttribute(name = "inner", required = true)
    protected double inner;
    @XmlAttribute(name = "outer", required = true)
    protected double outer;

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
     * Gets the value of the inner property.
     * 
     */
    public double getInner() {
        return inner;
    }

    /**
     * Sets the value of the inner property.
     * 
     */
    public void setInner(double value) {
        this.inner = value;
    }

    /**
     * Gets the value of the outer property.
     * 
     */
    public double getOuter() {
        return outer;
    }

    /**
     * Sets the value of the outer property.
     * 
     */
    public void setOuter(double value) {
        this.outer = value;
    }

}
