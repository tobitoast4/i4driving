
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="header" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_header"/&gt;
 *         &lt;element name="road" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road" maxOccurs="unbounded"/&gt;
 *         &lt;element name="controller" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_controller" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="junction" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_junction" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="junctionGroup" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_junctionGroup" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="station" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_station" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "header",
    "road",
    "controller",
    "junction",
    "junctionGroup",
    "station",
    "gAdditionalData"
})
@XmlRootElement(name = "OpenDRIVE")
@SuppressWarnings("all") public class OpenDRIVE {

    @XmlElement(required = true)
    protected THeader header;
    @XmlElement(required = true)
    protected List<TRoad> road;
    protected List<TController> controller;
    protected List<TJunction> junction;
    protected List<TJunctionGroup> junctionGroup;
    protected List<TStation> station;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link THeader }
     *     
     */
    public THeader getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link THeader }
     *     
     */
    public void setHeader(THeader value) {
        this.header = value;
    }

    /**
     * Gets the value of the road property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the road property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoad().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoad }
     * 
     * 
     */
    public List<TRoad> getRoad() {
        if (road == null) {
            road = new ArrayList<TRoad>();
        }
        return this.road;
    }

    /**
     * Gets the value of the controller property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the controller property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getController().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TController }
     * 
     * 
     */
    public List<TController> getController() {
        if (controller == null) {
            controller = new ArrayList<TController>();
        }
        return this.controller;
    }

    /**
     * Gets the value of the junction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the junction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJunction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TJunction }
     * 
     * 
     */
    public List<TJunction> getJunction() {
        if (junction == null) {
            junction = new ArrayList<TJunction>();
        }
        return this.junction;
    }

    /**
     * Gets the value of the junctionGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the junctionGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJunctionGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TJunctionGroup }
     * 
     * 
     */
    public List<TJunctionGroup> getJunctionGroup() {
        if (junctionGroup == null) {
            junctionGroup = new ArrayList<TJunctionGroup>();
        }
        return this.junctionGroup;
    }

    /**
     * Gets the value of the station property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the station property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TStation }
     * 
     * 
     */
    public List<TStation> getStation() {
        if (station == null) {
            station = new ArrayList<TStation>();
        }
        return this.station;
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
