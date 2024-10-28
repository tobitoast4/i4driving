
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Each platform element is valid on one or more track segments. The <segment> element must be specified.
 * 
 * <p>Java class for t_station_platform_segment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_station_platform_segment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="roadId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="sStart" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="sEnd" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="side" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_station_platform_segment_side" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_station_platform_segment")
@SuppressWarnings("all") public class TStationPlatformSegment
    extends OpenDriveElement
{

    @XmlAttribute(name = "roadId", required = true)
    protected String roadId;
    @XmlAttribute(name = "sStart", required = true)
    protected double sStart;
    @XmlAttribute(name = "sEnd", required = true)
    protected double sEnd;
    @XmlAttribute(name = "side", required = true)
    protected EStationPlatformSegmentSide side;

    /**
     * Gets the value of the roadId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoadId() {
        return roadId;
    }

    /**
     * Sets the value of the roadId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoadId(String value) {
        this.roadId = value;
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
     * Gets the value of the side property.
     * 
     * @return
     *     possible object is
     *     {@link EStationPlatformSegmentSide }
     *     
     */
    public EStationPlatformSegmentSide getSide() {
        return side;
    }

    /**
     * Sets the value of the side property.
     * 
     * @param value
     *     allowed object is
     *     {@link EStationPlatformSegmentSide }
     *     
     */
    public void setSide(EStationPlatformSegmentSide value) {
        this.side = value;
    }

}
