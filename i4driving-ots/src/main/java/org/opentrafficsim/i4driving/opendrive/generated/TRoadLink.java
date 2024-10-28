
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Follows the road header if the road is linked to a successor, a predecessor, or a neighbor. Isolated roads may omit this element.
 * 
 * <p>Java class for t_road_link complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_link"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="predecessor" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_link_predecessorSuccessor" minOccurs="0"/&gt;
 *         &lt;element name="successor" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_link_predecessorSuccessor" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_link", propOrder = {
    "predecessor",
    "successor",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadLink
    extends OpenDriveElement
{

    protected TRoadLinkPredecessorSuccessor predecessor;
    protected TRoadLinkPredecessorSuccessor successor;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;

    /**
     * Gets the value of the predecessor property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLinkPredecessorSuccessor }
     *     
     */
    public TRoadLinkPredecessorSuccessor getPredecessor() {
        return predecessor;
    }

    /**
     * Sets the value of the predecessor property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLinkPredecessorSuccessor }
     *     
     */
    public void setPredecessor(TRoadLinkPredecessorSuccessor value) {
        this.predecessor = value;
    }

    /**
     * Gets the value of the successor property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLinkPredecessorSuccessor }
     *     
     */
    public TRoadLinkPredecessorSuccessor getSuccessor() {
        return successor;
    }

    /**
     * Sets the value of the successor property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLinkPredecessorSuccessor }
     *     
     */
    public void setSuccessor(TRoadLinkPredecessorSuccessor value) {
        this.successor = value;
    }

    /**
     * Gets the value of the gAdditionalData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gAdditionalData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGAdditionalData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TDataQuality }
     * {@link TInclude }
     * {@link TUserData }
     * 
     * 
     */
    public List<Object> getGAdditionalData() {
        if (gAdditionalData == null) {
            gAdditionalData = new ArrayList<Object>();
        }
        return this.gAdditionalData;
    }

}
