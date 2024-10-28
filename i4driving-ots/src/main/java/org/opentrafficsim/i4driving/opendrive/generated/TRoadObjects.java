
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Container for all objects along a road.
 * 
 * <p>Java class for t_road_objects complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="object" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_object" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="objectReference" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_objectReference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="tunnel" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_tunnel" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="bridge" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects_bridge" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "t_road_objects", propOrder = {
    "object",
    "objectReference",
    "tunnel",
    "bridge",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadObjects
    extends OpenDriveElement
{

    protected List<TRoadObjectsObject> object;
    protected List<TRoadObjectsObjectReference> objectReference;
    protected List<TRoadObjectsTunnel> tunnel;
    protected List<TRoadObjectsBridge> bridge;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;

    /**
     * Gets the value of the object property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the object property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObject }
     * 
     * 
     */
    public List<TRoadObjectsObject> getObject() {
        if (object == null) {
            object = new ArrayList<TRoadObjectsObject>();
        }
        return this.object;
    }

    /**
     * Gets the value of the objectReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjectReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectReference }
     * 
     * 
     */
    public List<TRoadObjectsObjectReference> getObjectReference() {
        if (objectReference == null) {
            objectReference = new ArrayList<TRoadObjectsObjectReference>();
        }
        return this.objectReference;
    }

    /**
     * Gets the value of the tunnel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tunnel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTunnel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsTunnel }
     * 
     * 
     */
    public List<TRoadObjectsTunnel> getTunnel() {
        if (tunnel == null) {
            tunnel = new ArrayList<TRoadObjectsTunnel>();
        }
        return this.tunnel;
    }

    /**
     * Gets the value of the bridge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bridge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBridge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsBridge }
     * 
     * 
     */
    public List<TRoadObjectsBridge> getBridge() {
        if (bridge == null) {
            bridge = new ArrayList<TRoadObjectsBridge>();
        }
        return this.bridge;
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
