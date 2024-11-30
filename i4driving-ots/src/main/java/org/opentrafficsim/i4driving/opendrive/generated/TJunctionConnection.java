
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.opentrafficsim.i4driving.opendrive.bindings.ContactPointAdapter;


/**
 * Provides information about a single connection within a junction.
 * 
 * <p>Java class for t_junction_connection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_junction_connection"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="predecessor" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_junction_predecessorSuccessor" minOccurs="0"/&gt;
 *         &lt;element name="successor" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_junction_predecessorSuccessor" minOccurs="0"/&gt;
 *         &lt;element name="laneLink" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_junction_connection_laneLink" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_junction_type" /&gt;
 *       &lt;attribute name="incomingRoad" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="connectingRoad" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="contactPoint" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_junction_connection", propOrder = {
    "predecessor",
    "successor",
    "laneLink"
})
@SuppressWarnings("all") public class TJunctionConnection
    extends OpenDriveElement
{

    protected TJunctionPredecessorSuccessor predecessor;
    protected TJunctionPredecessorSuccessor successor;
    protected List<TJunctionConnectionLaneLink> laneLink;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "type")
    protected EJunctionType type;
    @XmlAttribute(name = "incomingRoad")
    protected String incomingRoad;
    @XmlAttribute(name = "connectingRoad")
    protected String connectingRoad;
    @XmlAttribute(name = "contactPoint")
    @XmlJavaTypeAdapter(ContactPointAdapter.class)
    protected EContactPoint contactPoint;

    /**
     * Gets the value of the predecessor property.
     * 
     * @return
     *     possible object is
     *     {@link TJunctionPredecessorSuccessor }
     *     
     */
    public TJunctionPredecessorSuccessor getPredecessor() {
        return predecessor;
    }

    /**
     * Sets the value of the predecessor property.
     * 
     * @param value
     *     allowed object is
     *     {@link TJunctionPredecessorSuccessor }
     *     
     */
    public void setPredecessor(TJunctionPredecessorSuccessor value) {
        this.predecessor = value;
    }

    /**
     * Gets the value of the successor property.
     * 
     * @return
     *     possible object is
     *     {@link TJunctionPredecessorSuccessor }
     *     
     */
    public TJunctionPredecessorSuccessor getSuccessor() {
        return successor;
    }

    /**
     * Sets the value of the successor property.
     * 
     * @param value
     *     allowed object is
     *     {@link TJunctionPredecessorSuccessor }
     *     
     */
    public void setSuccessor(TJunctionPredecessorSuccessor value) {
        this.successor = value;
    }

    /**
     * Gets the value of the laneLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the laneLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLaneLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TJunctionConnectionLaneLink }
     * 
     * 
     */
    public List<TJunctionConnectionLaneLink> getLaneLink() {
        if (laneLink == null) {
            laneLink = new ArrayList<TJunctionConnectionLaneLink>();
        }
        return this.laneLink;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EJunctionType }
     *     
     */
    public EJunctionType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EJunctionType }
     *     
     */
    public void setType(EJunctionType value) {
        this.type = value;
    }

    /**
     * Gets the value of the incomingRoad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncomingRoad() {
        return incomingRoad;
    }

    /**
     * Sets the value of the incomingRoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncomingRoad(String value) {
        this.incomingRoad = value;
    }

    /**
     * Gets the value of the connectingRoad property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConnectingRoad() {
        return connectingRoad;
    }

    /**
     * Sets the value of the connectingRoad property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConnectingRoad(String value) {
        this.connectingRoad = value;
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

}
