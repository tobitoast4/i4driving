
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for t_road_railroad_switch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_railroad_switch"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="mainTrack" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_railroad_switch_mainTrack"/&gt;
 *         &lt;element name="sideTrack" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_railroad_switch_sideTrack"/&gt;
 *         &lt;element name="partner" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_railroad_switch_partner" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="position" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_railroad_switch_position" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_railroad_switch", propOrder = {
    "mainTrack",
    "sideTrack",
    "partner",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadRailroadSwitch
    extends OpenDriveElement
{

    @XmlElement(required = true)
    protected TRoadRailroadSwitchMainTrack mainTrack;
    @XmlElement(required = true)
    protected TRoadRailroadSwitchSideTrack sideTrack;
    protected TRoadRailroadSwitchPartner partner;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "position", required = true)
    protected ERoadRailroadSwitchPosition position;

    /**
     * Gets the value of the mainTrack property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadRailroadSwitchMainTrack }
     *     
     */
    public TRoadRailroadSwitchMainTrack getMainTrack() {
        return mainTrack;
    }

    /**
     * Sets the value of the mainTrack property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadRailroadSwitchMainTrack }
     *     
     */
    public void setMainTrack(TRoadRailroadSwitchMainTrack value) {
        this.mainTrack = value;
    }

    /**
     * Gets the value of the sideTrack property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadRailroadSwitchSideTrack }
     *     
     */
    public TRoadRailroadSwitchSideTrack getSideTrack() {
        return sideTrack;
    }

    /**
     * Sets the value of the sideTrack property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadRailroadSwitchSideTrack }
     *     
     */
    public void setSideTrack(TRoadRailroadSwitchSideTrack value) {
        this.sideTrack = value;
    }

    /**
     * Gets the value of the partner property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadRailroadSwitchPartner }
     *     
     */
    public TRoadRailroadSwitchPartner getPartner() {
        return partner;
    }

    /**
     * Sets the value of the partner property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadRailroadSwitchPartner }
     *     
     */
    public void setPartner(TRoadRailroadSwitchPartner value) {
        this.partner = value;
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

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadRailroadSwitchPosition }
     *     
     */
    public ERoadRailroadSwitchPosition getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadRailroadSwitchPosition }
     *     
     */
    public void setPosition(ERoadRailroadSwitchPosition value) {
        this.position = value;
    }

}
