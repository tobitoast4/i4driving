
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * References to existing junction elements.
 * 
 * <p>Java class for t_junctionGroup_junctionReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_junctionGroup_junctionReference"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="junction" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_junctionGroup_junctionReference")
@SuppressWarnings("all") public class TJunctionGroupJunctionReference
    extends OpenDriveElement
{

    @XmlAttribute(name = "junction", required = true)
    protected String junction;

    /**
     * Gets the value of the junction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJunction() {
        return junction;
    }

    /**
     * Sets the value of the junction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJunction(String value) {
        this.junction = value;
    }

}
