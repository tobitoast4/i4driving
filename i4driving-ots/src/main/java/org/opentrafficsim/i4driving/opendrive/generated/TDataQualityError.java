
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * The absolute or relative errors of road data are described by <error> elements within the <dataQuality> element.
 * 
 * <p>Java class for t_dataQuality_Error complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_dataQuality_Error"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="xyAbsolute" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zAbsolute" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="xyRelative" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="zRelative" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_dataQuality_Error")
@SuppressWarnings("all") public class TDataQualityError {

    @XmlAttribute(name = "xyAbsolute", required = true)
    protected double xyAbsolute;
    @XmlAttribute(name = "zAbsolute", required = true)
    protected double zAbsolute;
    @XmlAttribute(name = "xyRelative", required = true)
    protected double xyRelative;
    @XmlAttribute(name = "zRelative", required = true)
    protected double zRelative;

    /**
     * Gets the value of the xyAbsolute property.
     * 
     */
    public double getXyAbsolute() {
        return xyAbsolute;
    }

    /**
     * Sets the value of the xyAbsolute property.
     * 
     */
    public void setXyAbsolute(double value) {
        this.xyAbsolute = value;
    }

    /**
     * Gets the value of the zAbsolute property.
     * 
     */
    public double getZAbsolute() {
        return zAbsolute;
    }

    /**
     * Sets the value of the zAbsolute property.
     * 
     */
    public void setZAbsolute(double value) {
        this.zAbsolute = value;
    }

    /**
     * Gets the value of the xyRelative property.
     * 
     */
    public double getXyRelative() {
        return xyRelative;
    }

    /**
     * Sets the value of the xyRelative property.
     * 
     */
    public void setXyRelative(double value) {
        this.xyRelative = value;
    }

    /**
     * Gets the value of the zRelative property.
     * 
     */
    public double getZRelative() {
        return zRelative;
    }

    /**
     * Sets the value of the zRelative property.
     * 
     */
    public void setZRelative(double value) {
        this.zRelative = value;
    }

}
