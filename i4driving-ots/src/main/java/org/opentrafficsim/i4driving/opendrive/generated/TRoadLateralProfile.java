
package org.opentrafficsim.i4driving.opendrive.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Contains a series of superelevation elements that define the characteristics of the road surface's banking along the reference line.
 * 
 * <p>Java class for t_road_lateralProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lateralProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="superelevation" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lateralProfile_superelevation" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="shape" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_road_lateralProfile_shape" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "t_road_lateralProfile", propOrder = {
    "superelevation",
    "shape",
    "gAdditionalData"
})
@SuppressWarnings("all") public class TRoadLateralProfile
    extends OpenDriveElement
{

    protected List<TRoadLateralProfileSuperelevation> superelevation;
    protected List<TRoadLateralProfileShape> shape;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;

    /**
     * Gets the value of the superelevation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the superelevation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSuperelevation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLateralProfileSuperelevation }
     * 
     * 
     */
    public List<TRoadLateralProfileSuperelevation> getSuperelevation() {
        if (superelevation == null) {
            superelevation = new ArrayList<TRoadLateralProfileSuperelevation>();
        }
        return this.superelevation;
    }

    /**
     * Gets the value of the shape property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shape property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShape().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLateralProfileShape }
     * 
     * 
     */
    public List<TRoadLateralProfileShape> getShape() {
        if (shape == null) {
            shape = new ArrayList<TRoadLateralProfileShape>();
        }
        return this.shape;
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
