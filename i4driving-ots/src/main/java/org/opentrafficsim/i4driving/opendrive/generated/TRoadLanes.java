
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Contains a series of lane section elements that define the characteristics of the road cross sections with respect to the lanes along the reference line.
 * 
 * <p>Java class for t_road_lanes complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="laneOffset" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneOffset" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="laneSection" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "t_road_lanes", propOrder = {
    "laneOffset",
    "laneSection",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadLanes
    extends OpenDriveElement
{

    protected List<TRoadLanesLaneOffset> laneOffset;
    @XmlElement(required = true)
    protected List<TRoadLanesLaneSection> laneSection;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;

    /**
     * Gets the value of the laneOffset property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the laneOffset property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLaneOffset().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneOffset }
     * 
     * 
     */
    public List<TRoadLanesLaneOffset> getLaneOffset() {
        if (laneOffset == null) {
            laneOffset = new ArrayList<TRoadLanesLaneOffset>();
        }
        return this.laneOffset;
    }

    /**
     * Gets the value of the laneSection property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the laneSection property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLaneSection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSection }
     * 
     * 
     */
    public List<TRoadLanesLaneSection> getLaneSection() {
        if (laneSection == null) {
            laneSection = new ArrayList<TRoadLanesLaneSection>();
        }
        return this.laneSection;
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
