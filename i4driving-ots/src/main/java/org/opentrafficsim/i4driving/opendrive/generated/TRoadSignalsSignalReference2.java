
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Provides a means to link a signal to a series of other elements (for example, objects and signals). 
 * 
 * <p>Java class for t_road_signals_signal_reference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_signals_signal_reference"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="elementType" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_signals_signal_reference_elementType" /&gt;
 *       &lt;attribute name="elementId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_signals_signal_reference")
@SuppressWarnings("all") public class TRoadSignalsSignalReference2
    extends OpenDriveElement
{

    @XmlAttribute(name = "elementType", required = true)
    protected ERoadSignalsSignalReferenceElementType elementType;
    @XmlAttribute(name = "elementId", required = true)
    protected String elementId;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadSignalsSignalReferenceElementType }
     *     
     */
    public ERoadSignalsSignalReferenceElementType getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadSignalsSignalReferenceElementType }
     *     
     */
    public void setElementType(ERoadSignalsSignalReferenceElementType value) {
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

}
