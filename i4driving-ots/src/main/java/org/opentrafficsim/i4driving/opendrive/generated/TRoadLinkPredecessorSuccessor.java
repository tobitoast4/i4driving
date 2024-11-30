
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.opentrafficsim.i4driving.opendrive.bindings.ContactPointAdapter;
import org.opentrafficsim.i4driving.opendrive.bindings.RoadLinkTypeAdapter;


/**
 * For virtual and regular junctions, different attribute sets shall be used. @contactPoint shall be used for regular junctions; @elementS and @elementDir shall be used for virtual junctions.
 * 
 * <p>Java class for t_road_link_predecessorSuccessor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_link_predecessorSuccessor"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="elementId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="elementType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="contactPoint" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="elementS" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="elementDir" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_link_predecessorSuccessor")
@SuppressWarnings("all") public class TRoadLinkPredecessorSuccessor
    extends OpenDriveElement
{

    @XmlAttribute(name = "elementId", required = true)
    protected String elementId;
    @XmlAttribute(name = "elementType")
    @XmlJavaTypeAdapter(RoadLinkTypeAdapter.class)
    protected ERoadLinkElementType elementType;
    @XmlAttribute(name = "contactPoint")
    @XmlJavaTypeAdapter(ContactPointAdapter.class)
    protected EContactPoint contactPoint;
    @XmlAttribute(name = "elementS")
    protected Double elementS;
    @XmlAttribute(name = "elementDir")
    protected String elementDir;

    /**
     * Gets the value of the elementId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * Sets the value of the elementId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementId(String value) {
        this.elementId = value;
    }

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public ERoadLinkElementType getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementType(ERoadLinkElementType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the contactPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public EContactPoint getContactPoint() {
        return contactPoint;
    }

    /**
     * Sets the value of the contactPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPoint(EContactPoint value) {
        this.contactPoint = value;
    }

    /**
     * Gets the value of the elementS property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getElementS() {
        return elementS;
    }

    /**
     * Sets the value of the elementS property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setElementS(Double value) {
        this.elementS = value;
    }

    /**
     * Gets the value of the elementDir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementDir() {
        return elementDir;
    }

    /**
     * Sets the value of the elementDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementDir(String value) {
        this.elementDir = value;
    }

}
