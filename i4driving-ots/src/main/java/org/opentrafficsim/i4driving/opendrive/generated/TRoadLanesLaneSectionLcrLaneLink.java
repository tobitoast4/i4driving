
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * For links between lanes with an identical reference line, the lane predecessor and successor information provide the IDs of lanes on the preceding or following lane section. For links between lanes with different reference line,  the lane predecessor and successor information provide the IDs of lanes on the first or last lane section of the other reference line depending on the contact point of the road linkage. This element may only be omitted, if lanes end at a junction or have no physical link.
 * 
 * <p>Java class for t_road_lanes_laneSection_lcr_lane_link complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lcr_lane_link"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="predecessor" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lcr_lane_link_predecessorSuccessor" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="successor" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lcr_lane_link_predecessorSuccessor" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "t_road_lanes_laneSection_lcr_lane_link", propOrder = {
    "predecessor",
    "successor",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadLanesLaneSectionLcrLaneLink
    extends OpenDriveElement
{

    protected List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor> predecessor;
    protected List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor> successor;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;

    /**
     * Gets the value of the predecessor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the predecessor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPredecessor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor> getPredecessor() {
        if (predecessor == null) {
            predecessor = new ArrayList<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor>();
        }
        return this.predecessor;
    }

    /**
     * Gets the value of the successor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the successor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSuccessor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor> getSuccessor() {
        if (successor == null) {
            successor = new ArrayList<TRoadLanesLaneSectionLcrLaneLinkPredecessorSuccessor>();
        }
        return this.successor;
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
