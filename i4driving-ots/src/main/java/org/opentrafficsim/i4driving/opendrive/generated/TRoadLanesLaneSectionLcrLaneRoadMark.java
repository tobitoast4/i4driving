
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
import org.opentrafficsim.i4driving.opendrive.bindings.StripeTypeAdapter;
import org.opentrafficsim.road.network.lane.Stripe.Type;


/**
 * Defines the style of the line at the outer border of a lane. The style of the center line that separates left and right lanes is determined by the road mark element for the center lane.
 * 
 * <p>Java class for t_road_lanes_laneSection_lcr_lane_roadMark complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lcr_lane_roadMark"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sway" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lcr_lane_roadMark_sway" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lcr_lane_roadMark_type" minOccurs="0"/&gt;
 *         &lt;element name="explicit" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lanes_laneSection_lcr_lane_roadMark_explicit" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="sOffset" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="type" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_roadMarkType" /&gt;
 *       &lt;attribute name="weight" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_roadMarkWeight" /&gt;
 *       &lt;attribute name="color" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_roadMarkColor" /&gt;
 *       &lt;attribute name="material" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="width" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_grEqZero" /&gt;
 *       &lt;attribute name="laneChange" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_lanes_laneSection_lcr_lane_roadMark_laneChange" /&gt;
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lcr_lane_roadMark", propOrder = {
    "sway",
    "type",
    "explicit",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadLanesLaneSectionLcrLaneRoadMark
    extends OpenDriveElement
{

    protected List<TRoadLanesLaneSectionLcrLaneRoadMarkSway> sway;
    protected TRoadLanesLaneSectionLcrLaneRoadMarkType type;
    protected TRoadLanesLaneSectionLcrLaneRoadMarkExplicit explicit;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "sOffset", required = true)
    protected double sOffset;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(StripeTypeAdapter.class)
    protected Type roadMarkType;
    @XmlAttribute(name = "weight")
    protected ERoadMarkWeight weight;
    @XmlAttribute(name = "color", required = true)
    protected ERoadMarkColor color;
    @XmlAttribute(name = "material")
    protected String material;
    @XmlAttribute(name = "width")
    protected Double width;
    @XmlAttribute(name = "laneChange")
    protected ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange laneChange;
    @XmlAttribute(name = "height")
    protected Double height;

    /**
     * Gets the value of the sway property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sway property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSway().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLcrLaneRoadMarkSway }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLcrLaneRoadMarkSway> getSway() {
        if (sway == null) {
            sway = new ArrayList<TRoadLanesLaneSectionLcrLaneRoadMarkSway>();
        }
        return this.sway;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkType }
     *     
     */
    public TRoadLanesLaneSectionLcrLaneRoadMarkType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkType }
     *     
     */
    public void setType(TRoadLanesLaneSectionLcrLaneRoadMarkType value) {
        this.type = value;
    }

    /**
     * Gets the value of the explicit property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkExplicit }
     *     
     */
    public TRoadLanesLaneSectionLcrLaneRoadMarkExplicit getExplicit() {
        return explicit;
    }

    /**
     * Sets the value of the explicit property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkExplicit }
     *     
     */
    public void setExplicit(TRoadLanesLaneSectionLcrLaneRoadMarkExplicit value) {
        this.explicit = value;
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
     * Gets the value of the sOffset property.
     * 
     */
    public double getSOffset() {
        return sOffset;
    }

    /**
     * Sets the value of the sOffset property.
     * 
     */
    public void setSOffset(double value) {
        this.sOffset = value;
    }

    /**
     * Gets the value of the roadMarkType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Type getRoadMarkType() {
        return roadMarkType;
    }

    /**
     * Sets the value of the roadMarkType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoadMarkType(Type value) {
        this.roadMarkType = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkWeight }
     *     
     */
    public ERoadMarkWeight getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkWeight }
     *     
     */
    public void setWeight(ERoadMarkWeight value) {
        this.weight = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkColor }
     *     
     */
    public ERoadMarkColor getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkColor }
     *     
     */
    public void setColor(ERoadMarkColor value) {
        this.color = value;
    }

    /**
     * Gets the value of the material property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Sets the value of the material property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterial(String value) {
        this.material = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWidth(Double value) {
        this.width = value;
    }

    /**
     * Gets the value of the laneChange property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange }
     *     
     */
    public ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange getLaneChange() {
        return laneChange;
    }

    /**
     * Sets the value of the laneChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange }
     *     
     */
    public void setLaneChange(ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange value) {
        this.laneChange = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setHeight(Double value) {
        this.height = value;
    }

}
