
package org.opentrafficsim.i4driving.opendrive.generated;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * The <header> element is the very first element within the <OpenDRIVE> element.
 * 
 * <p>Java class for t_header complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_header"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="geoReference" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_header_GeoReference" minOccurs="0"/&gt;
 *         &lt;element name="offset" type="{http://code.asam.net/simulation/standard/opendrive_schema}t_header_Offset" minOccurs="0"/&gt;
 *         &lt;group ref="{http://code.asam.net/simulation/standard/opendrive_schema}g_additionalData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="revMajor" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" fixed="1" /&gt;
 *       &lt;attribute name="revMinor" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="north" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="south" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="east" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="west" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="vendor" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_header", propOrder = {
    "geoReference",
    "offset",
    "gAdditionalData"
})
@SuppressWarnings("all") public class THeader
    extends OpenDriveElement
{

    protected THeaderGeoReference geoReference;
    protected THeaderOffset offset;
    @XmlElements({
        @XmlElement(name = "include", type = TInclude.class),
        @XmlElement(name = "userData", type = TUserData.class),
        @XmlElement(name = "dataQuality", type = TDataQuality.class)
    })
    protected List<Object> gAdditionalData;
    @XmlAttribute(name = "revMajor", required = true)
    protected BigInteger revMajor;
    @XmlAttribute(name = "revMinor", required = true)
    protected BigInteger revMinor;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "version")
    protected String version;
    @XmlAttribute(name = "date")
    protected String date;
    @XmlAttribute(name = "north")
    protected Double north;
    @XmlAttribute(name = "south")
    protected Double south;
    @XmlAttribute(name = "east")
    protected Double east;
    @XmlAttribute(name = "west")
    protected Double west;
    @XmlAttribute(name = "vendor")
    protected String vendor;

    /**
     * Gets the value of the geoReference property.
     * 
     * @return
     *     possible object is
     *     {@link THeaderGeoReference }
     *     
     */
    public THeaderGeoReference getGeoReference() {
        return geoReference;
    }

    /**
     * Sets the value of the geoReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link THeaderGeoReference }
     *     
     */
    public void setGeoReference(THeaderGeoReference value) {
        this.geoReference = value;
    }

    /**
     * Gets the value of the offset property.
     * 
     * @return
     *     possible object is
     *     {@link THeaderOffset }
     *     
     */
    public THeaderOffset getOffset() {
        return offset;
    }

    /**
     * Sets the value of the offset property.
     * 
     * @param value
     *     allowed object is
     *     {@link THeaderOffset }
     *     
     */
    public void setOffset(THeaderOffset value) {
        this.offset = value;
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
     * Gets the value of the revMajor property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRevMajor() {
        if (revMajor == null) {
            return new BigInteger("1");
        } else {
            return revMajor;
        }
    }

    /**
     * Sets the value of the revMajor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRevMajor(BigInteger value) {
        this.revMajor = value;
    }

    /**
     * Gets the value of the revMinor property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRevMinor() {
        return revMinor;
    }

    /**
     * Sets the value of the revMinor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRevMinor(BigInteger value) {
        this.revMinor = value;
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
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDate(String value) {
        this.date = value;
    }

    /**
     * Gets the value of the north property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getNorth() {
        return north;
    }

    /**
     * Sets the value of the north property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setNorth(Double value) {
        this.north = value;
    }

    /**
     * Gets the value of the south property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSouth() {
        return south;
    }

    /**
     * Sets the value of the south property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSouth(Double value) {
        this.south = value;
    }

    /**
     * Gets the value of the east property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getEast() {
        return east;
    }

    /**
     * Sets the value of the east property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setEast(Double value) {
        this.east = value;
    }

    /**
     * Gets the value of the west property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWest() {
        return west;
    }

    /**
     * Sets the value of the west property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWest(Double value) {
        this.west = value;
    }

    /**
     * Gets the value of the vendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

}
