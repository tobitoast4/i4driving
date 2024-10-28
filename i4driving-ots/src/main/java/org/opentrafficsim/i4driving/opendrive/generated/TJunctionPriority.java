
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * If an incoming road is linked to an outgoing road with multiple connection roads to represent several possible lane connections, then one of these connections may be prioritized. Assigning a priority is only required if the application is unable to derive priorities from signals before or inside a junction or from the lanes leading to a junction. At least one attribute must be given.
 * 
 * <p>Java class for t_junction_priority complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_junction_priority"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="high" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="low" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_junction_priority")
@SuppressWarnings("all") public class TJunctionPriority
    extends OpenDriveElement
{

    @XmlAttribute(name = "high")
    protected String high;
    @XmlAttribute(name = "low")
    protected String low;

    /**
     * Gets the value of the high property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHigh() {
        return high;
    }

    /**
     * Sets the value of the high property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHigh(String value) {
        this.high = value;
    }

    /**
     * Gets the value of the low property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLow() {
        return low;
    }

    /**
     * Sets the value of the low property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLow(String value) {
        this.low = value;
    }

}
