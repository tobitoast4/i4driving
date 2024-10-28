
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Lane elements are included in left/center/right elements. Lane elements should represent the lanes from left to right, that is, with descending ID.
 * 
 * <p>Java class for t_road_lanes_laneSection_lr_lane complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lr_lane"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="link" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lcr_lane_link" minOccurs="0"/&gt;
 *         &lt;choice maxOccurs="unbounded"&gt;
 *           &lt;element name="border" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lr_lane_border" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="width" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lr_lane_width" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="roadMark" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lcr_lane_roadMark" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="material" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lr_lane_material" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="speed" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lr_lane_speed" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="access" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lr_lane_access" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="height" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lr_lane_height" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="rule" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lr_lane_rule" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="type" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_laneType" /&gt;
 *       &lt;attribute name="level" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_bool" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lr_lane", propOrder = {
    "link",
    "borderOrWidth",
    "roadMark",
    "material",
    "speed",
    "access",
    "height",
    "rule",
    "gAdditionalData"
})
@XmlSeeAlso({
    TRoadLanesLaneSectionCenterLane.class,
    TRoadLanesLaneSectionLeftLane.class,
    TRoadLanesLaneSectionRightLane.class
})
@SuppressWarnings("all") public class TRoadLanesLaneSectionLrLane
    extends OpenDriveElement
{

    protected TRoadLanesLaneSectionLcrLaneLink link;
    @XmlElements({
        @XmlElement(name = "border", type = TRoadLanesLaneSectionLrLaneBorder.class),
        @XmlElement(name = "width", type = TRoadLanesLaneSectionLrLaneWidth.class)
    })
    protected List<OpenDriveElement> borderOrWidth;
    protected List<TRoadLanesLaneSectionLcrLaneRoadMark> roadMark;
    protected List<TRoadLanesLaneSectionLrLaneMaterial> material;
    protected List<TRoadLanesLaneSectionLrLaneSpeed> speed;
    protected List<TRoadLanesLaneSectionLrLaneAccess> access;
    protected List<TRoadLanesLaneSectionLrLaneHeight> height;
    protected List<TRoadLanesLaneSectionLrLaneRule> rule;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "type", required = true)
    protected ELaneType type;
    @XmlAttribute(name = "level")
    protected TBool level;

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLanesLaneSectionLcrLaneLink }
     *     
     */
    public TRoadLanesLaneSectionLcrLaneLink getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLanesLaneSectionLcrLaneLink }
     *     
     */
    public void setLink(TRoadLanesLaneSectionLcrLaneLink value) {
        this.link = value;
    }

    /**
     * Gets the value of the borderOrWidth property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the borderOrWidth property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBorderOrWidth().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLrLaneBorder }
     * {@link TRoadLanesLaneSectionLrLaneWidth }
     * 
     * 
     */
    public List<OpenDriveElement> getBorderOrWidth() {
        if (borderOrWidth == null) {
            borderOrWidth = new ArrayList<OpenDriveElement>();
        }
        return this.borderOrWidth;
    }

    /**
     * Gets the value of the roadMark property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roadMark property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRoadMark().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLcrLaneRoadMark }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLcrLaneRoadMark> getRoadMark() {
        if (roadMark == null) {
            roadMark = new ArrayList<TRoadLanesLaneSectionLcrLaneRoadMark>();
        }
        return this.roadMark;
    }

    /**
     * Gets the value of the material property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the material property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterial().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLrLaneMaterial }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLrLaneMaterial> getMaterial() {
        if (material == null) {
            material = new ArrayList<TRoadLanesLaneSectionLrLaneMaterial>();
        }
        return this.material;
    }

    /**
     * Gets the value of the speed property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the speed property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpeed().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLrLaneSpeed }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLrLaneSpeed> getSpeed() {
        if (speed == null) {
            speed = new ArrayList<TRoadLanesLaneSectionLrLaneSpeed>();
        }
        return this.speed;
    }

    /**
     * Gets the value of the access property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the access property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAccess().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLrLaneAccess }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLrLaneAccess> getAccess() {
        if (access == null) {
            access = new ArrayList<TRoadLanesLaneSectionLrLaneAccess>();
        }
        return this.access;
    }

    /**
     * Gets the value of the height property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the height property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLrLaneHeight }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLrLaneHeight> getHeight() {
        if (height == null) {
            height = new ArrayList<TRoadLanesLaneSectionLrLaneHeight>();
        }
        return this.height;
    }

    /**
     * Gets the value of the rule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLrLaneRule }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLrLaneRule> getRule() {
        if (rule == null) {
            rule = new ArrayList<TRoadLanesLaneSectionLrLaneRule>();
        }
        return this.rule;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ELaneType }
     *     
     */
    public ELaneType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ELaneType }
     *     
     */
    public void setType(ELaneType value) {
        this.type = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link TBool }
     *     
     */
    public TBool getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link TBool }
     *     
     */
    public void setLevel(TBool value) {
        this.level = value;
    }

}
