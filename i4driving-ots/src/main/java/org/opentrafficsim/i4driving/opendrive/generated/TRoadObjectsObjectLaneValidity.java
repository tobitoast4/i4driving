
package org.opentrafficsim.i4driving.opendrive.generated;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * May replace the default validity with explicit validity information for an object. Multiple validity elements may be defined per object.
 * 
 * <p>Java class for t_road_objects_object_laneValidity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_laneValidity"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="fromLane" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="toLane" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_laneValidity")
@SuppressWarnings("all") public class TRoadObjectsObjectLaneValidity
    extends OpenDriveElement
{

    @XmlAttribute(name = "fromLane", required = true)
    protected BigInteger fromLane;
    @XmlAttribute(name = "toLane", required = true)
    protected BigInteger toLane;

    /**
     * Gets the value of the fromLane property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFromLane() {
        return fromLane;
    }

    /**
     * Sets the value of the fromLane property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFromLane(BigInteger value) {
        this.fromLane = value;
    }

    /**
     * Gets the value of the toLane property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getToLane() {
        return toLane;
    }

    /**
     * Sets the value of the toLane property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setToLane(BigInteger value) {
        this.toLane = value;
    }

}
