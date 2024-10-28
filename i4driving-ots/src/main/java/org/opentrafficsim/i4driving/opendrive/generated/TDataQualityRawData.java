
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Some basic metadata containing information about raw data included in OpenDRIVE is described by the <rawData> element within the <dataQuality> element. 
 * 
 * <p>Java class for t_dataQuality_RawData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_dataQuality_RawData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="date" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="source" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_dataQuality_RawData_Source" /&gt;
 *       &lt;attribute name="sourceComment" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="postProcessing" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_dataQuality_RawData_PostProcessing" /&gt;
 *       &lt;attribute name="postProcessingComment" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_dataQuality_RawData")
@SuppressWarnings("all") public class TDataQualityRawData {

    @XmlAttribute(name = "date", required = true)
    protected String date;
    @XmlAttribute(name = "source", required = true)
    protected EDataQualityRawDataSource source;
    @XmlAttribute(name = "sourceComment")
    protected String sourceComment;
    @XmlAttribute(name = "postProcessing", required = true)
    protected EDataQualityRawDataPostProcessing postProcessing;
    @XmlAttribute(name = "postProcessingComment")
    protected String postProcessingComment;

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
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link EDataQualityRawDataSource }
     *     
     */
    public EDataQualityRawDataSource getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDataQualityRawDataSource }
     *     
     */
    public void setSource(EDataQualityRawDataSource value) {
        this.source = value;
    }

    /**
     * Gets the value of the sourceComment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceComment() {
        return sourceComment;
    }

    /**
     * Sets the value of the sourceComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceComment(String value) {
        this.sourceComment = value;
    }

    /**
     * Gets the value of the postProcessing property.
     * 
     * @return
     *     possible object is
     *     {@link EDataQualityRawDataPostProcessing }
     *     
     */
    public EDataQualityRawDataPostProcessing getPostProcessing() {
        return postProcessing;
    }

    /**
     * Sets the value of the postProcessing property.
     * 
     * @param value
     *     allowed object is
     *     {@link EDataQualityRawDataPostProcessing }
     *     
     */
    public void setPostProcessing(EDataQualityRawDataPostProcessing value) {
        this.postProcessing = value;
    }

    /**
     * Gets the value of the postProcessingComment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostProcessingComment() {
        return postProcessingComment;
    }

    /**
     * Sets the value of the postProcessingComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostProcessingComment(String value) {
        this.postProcessingComment = value;
    }

}
