
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Provides detailed information about the predecessor / successor road of a virtual connection. Currently, only the @elementType “road” is allowed.
 * 
 * <p>Java class for t_junction_predecessorSuccessor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_junction_predecessorSuccessor"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="elementType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" fixed="road" /&gt;
 *       &lt;attribute name="elementId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="elementS" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grZero" /&gt;
 *       &lt;attribute name="elementDir" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_elementDir" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_junction_predecessorSuccessor")
@SuppressWarnings("all") public class TJunctionPredecessorSuccessor
    extends OpenDriveElement
{

    @XmlAttribute(name = "elementType", required = true)
    protected String elementType;
    @XmlAttribute(name = "elementId", required = true)
    protected String elementId;
    @XmlAttribute(name = "elementS", required = true)
    protected double elementS;
    @XmlAttribute(name = "elementDir", required = true)
    protected String elementDir;

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementType() {
        if (elementType == null) {
            return "road";
        } else {
            return elementType;
        }
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementType(String value) {
        this.elementType = value;
    }

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
     * Gets the value of the elementS property.
     * 
     */
    public double getElementS() {
        return elementS;
    }

    /**
     * Sets the value of the elementS property.
     * 
     */
    public void setElementS(double value) {
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
