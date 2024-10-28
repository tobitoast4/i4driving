
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Details for a parking space may be added to the object element.
 * 
 * <p>Java class for t_road_objects_object_parkingSpace complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_parkingSpace"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="access" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_road_objects_object_parkingSpace_access" /&gt;
 *       &lt;attribute name="restrictions" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_parkingSpace")
@SuppressWarnings("all") public class TRoadObjectsObjectParkingSpace
    extends OpenDriveElement
{

    @XmlAttribute(name = "access", required = true)
    protected ERoadObjectsObjectParkingSpaceAccess access;
    @XmlAttribute(name = "restrictions")
    protected String restrictions;

    /**
     * Gets the value of the access property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadObjectsObjectParkingSpaceAccess }
     *     
     */
    public ERoadObjectsObjectParkingSpaceAccess getAccess() {
        return access;
    }

    /**
     * Sets the value of the access property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadObjectsObjectParkingSpaceAccess }
     *     
     */
    public void setAccess(ERoadObjectsObjectParkingSpaceAccess value) {
        this.access = value;
    }

    /**
     * Gets the value of the restrictions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestrictions() {
        return restrictions;
    }

    /**
     * Sets the value of the restrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestrictions(String value) {
        this.restrictions = value;
    }

}
