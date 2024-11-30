
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.djunits.value.vdouble.scalar.Length;
import org.opentrafficsim.i4driving.opendrive.bindings.LaneKeepingPolicyAdapter;
import org.opentrafficsim.i4driving.opendrive.bindings.LengthAdapter;
import org.opentrafficsim.road.network.lane.changing.LaneKeepingPolicy;


/**
 * In OpenDRIVE, the road network is represented by <road> elements. Each road runs along one road reference line. A road shall have at least one lane with a width larger than 0.
 * OpenDRIVE roads may be roads in the real road network or artificial road network created for application use. Each road is described by one or more <road> elements. One <road> element may cover a long stretch of a road, shorter stretches between junctions, or even several roads. A new <road> element should only start if the properties of the road cannot be described within the previous <road> element or if a junction is required.
 * 
 * <p>Java class for t_road complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="link" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_link" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="planView" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_planView"/&gt;
 *         &lt;element name="elevationProfile" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_elevationProfile" minOccurs="0"/&gt;
 *         &lt;element name="lateralProfile" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lateralProfile" minOccurs="0"/&gt;
 *         &lt;element name="lanes" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes"/&gt;
 *         &lt;element name="objects" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_objects" minOccurs="0"/&gt;
 *         &lt;element name="signals" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_signals" minOccurs="0"/&gt;
 *         &lt;element name="surface" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_surface" minOccurs="0"/&gt;
 *         &lt;element name="railroad" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_railroad" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="length" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="junction" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="rule" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_trafficRule" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road", propOrder = {
    "link",
    "type",
    "planView",
    "elevationProfile",
    "lateralProfile",
    "lanes",
    "objects",
    "signals",
    "surface",
    "railroad",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoad
    extends OpenDriveElement
{

    protected TRoadLink link;
    protected List<TRoadType> type;
    @XmlElement(required = true)
    protected TRoadPlanView planView;
    protected TRoadElevationProfile elevationProfile;
    protected TRoadLateralProfile lateralProfile;
    @XmlElement(required = true)
    protected TRoadLanes lanes;
    protected TRoadObjects objects;
    protected TRoadSignals signals;
    protected TRoadSurface surface;
    protected TRoadRailroad railroad;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "length", required = true)
    @XmlJavaTypeAdapter(LengthAdapter.class)
    protected Length length;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "junction", required = true)
    protected String junction;
    @XmlAttribute(name = "rule")
    @XmlJavaTypeAdapter(LaneKeepingPolicyAdapter.class)
    protected LaneKeepingPolicy rule;

    /**
     * Gets the value of the link property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLink }
     *     
     */
    public TRoadLink getLink() {
        return link;
    }

    /**
     * Sets the value of the link property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLink }
     *     
     */
    public void setLink(TRoadLink value) {
        this.link = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the type property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadType }
     * 
     * 
     */
    public List<TRoadType> getType() {
        if (type == null) {
            type = new ArrayList<TRoadType>();
        }
        return this.type;
    }

    /**
     * Gets the value of the planView property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadPlanView }
     *     
     */
    public TRoadPlanView getPlanView() {
        return planView;
    }

    /**
     * Sets the value of the planView property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadPlanView }
     *     
     */
    public void setPlanView(TRoadPlanView value) {
        this.planView = value;
    }

    /**
     * Gets the value of the elevationProfile property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadElevationProfile }
     *     
     */
    public TRoadElevationProfile getElevationProfile() {
        return elevationProfile;
    }

    /**
     * Sets the value of the elevationProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadElevationProfile }
     *     
     */
    public void setElevationProfile(TRoadElevationProfile value) {
        this.elevationProfile = value;
    }

    /**
     * Gets the value of the lateralProfile property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLateralProfile }
     *     
     */
    public TRoadLateralProfile getLateralProfile() {
        return lateralProfile;
    }

    /**
     * Sets the value of the lateralProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLateralProfile }
     *     
     */
    public void setLateralProfile(TRoadLateralProfile value) {
        this.lateralProfile = value;
    }

    /**
     * Gets the value of the lanes property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLanes }
     *     
     */
    public TRoadLanes getLanes() {
        return lanes;
    }

    /**
     * Sets the value of the lanes property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLanes }
     *     
     */
    public void setLanes(TRoadLanes value) {
        this.lanes = value;
    }

    /**
     * Gets the value of the objects property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjects }
     *     
     */
    public TRoadObjects getObjects() {
        return objects;
    }

    /**
     * Sets the value of the objects property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjects }
     *     
     */
    public void setObjects(TRoadObjects value) {
        this.objects = value;
    }

    /**
     * Gets the value of the signals property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadSignals }
     *     
     */
    public TRoadSignals getSignals() {
        return signals;
    }

    /**
     * Sets the value of the signals property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadSignals }
     *     
     */
    public void setSignals(TRoadSignals value) {
        this.signals = value;
    }

    /**
     * Gets the value of the surface property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadSurface }
     *     
     */
    public TRoadSurface getSurface() {
        return surface;
    }

    /**
     * Sets the value of the surface property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadSurface }
     *     
     */
    public void setSurface(TRoadSurface value) {
        this.surface = value;
    }

    /**
     * Gets the value of the railroad property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadRailroad }
     *     
     */
    public TRoadRailroad getRailroad() {
        return railroad;
    }

    /**
     * Sets the value of the railroad property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadRailroad }
     *     
     */
    public void setRailroad(TRoadRailroad value) {
        this.railroad = value;
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
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Length getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLength(Length value) {
        this.length = value;
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

    /**
     * Gets the value of the rule property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LaneKeepingPolicy getRule() {
        return rule;
    }

    /**
     * Sets the value of the rule property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRule(LaneKeepingPolicy value) {
        this.rule = value;
    }

}
