
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * In OpenDRIVE, parametric cubic curves are represented by <paramPoly3> elements within the <geometry> element.
 * 
 * <p>Java class for t_road_planView_geometry_paramPoly3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_planView_geometry_paramPoly3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="aU" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="bU" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="cU" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="dU" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="aV" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="bV" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="cV" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="dV" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="pRange" use="required" type="{http://code.asam.net/simulation/standard/opendrive_schema}e_paramPoly3_pRange" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_planView_geometry_paramPoly3")
@SuppressWarnings("all") public class TRoadPlanViewGeometryParamPoly3
    extends OpenDriveElement
{

    @XmlAttribute(name = "aU", required = true)
    protected double au;
    @XmlAttribute(name = "bU", required = true)
    protected double bu;
    @XmlAttribute(name = "cU", required = true)
    protected double cu;
    @XmlAttribute(name = "dU", required = true)
    protected double du;
    @XmlAttribute(name = "aV", required = true)
    protected double av;
    @XmlAttribute(name = "bV", required = true)
    protected double bv;
    @XmlAttribute(name = "cV", required = true)
    protected double cv;
    @XmlAttribute(name = "dV", required = true)
    protected double dv;
    @XmlAttribute(name = "pRange", required = true)
    protected EParamPoly3PRange pRange;

    /**
     * Gets the value of the au property.
     * 
     */
    public double getAU() {
        return au;
    }

    /**
     * Sets the value of the au property.
     * 
     */
    public void setAU(double value) {
        this.au = value;
    }

    /**
     * Gets the value of the bu property.
     * 
     */
    public double getBU() {
        return bu;
    }

    /**
     * Sets the value of the bu property.
     * 
     */
    public void setBU(double value) {
        this.bu = value;
    }

    /**
     * Gets the value of the cu property.
     * 
     */
    public double getCU() {
        return cu;
    }

    /**
     * Sets the value of the cu property.
     * 
     */
    public void setCU(double value) {
        this.cu = value;
    }

    /**
     * Gets the value of the du property.
     * 
     */
    public double getDU() {
        return du;
    }

    /**
     * Sets the value of the du property.
     * 
     */
    public void setDU(double value) {
        this.du = value;
    }

    /**
     * Gets the value of the av property.
     * 
     */
    public double getAV() {
        return av;
    }

    /**
     * Sets the value of the av property.
     * 
     */
    public void setAV(double value) {
        this.av = value;
    }

    /**
     * Gets the value of the bv property.
     * 
     */
    public double getBV() {
        return bv;
    }

    /**
     * Sets the value of the bv property.
     * 
     */
    public void setBV(double value) {
        this.bv = value;
    }

    /**
     * Gets the value of the cv property.
     * 
     */
    public double getCV() {
        return cv;
    }

    /**
     * Sets the value of the cv property.
     * 
     */
    public void setCV(double value) {
        this.cv = value;
    }

    /**
     * Gets the value of the dv property.
     * 
     */
    public double getDV() {
        return dv;
    }

    /**
     * Sets the value of the dv property.
     * 
     */
    public void setDV(double value) {
        this.dv = value;
    }

    /**
     * Gets the value of the pRange property.
     * 
     * @return
     *     possible object is
     *     {@link EParamPoly3PRange }
     *     
     */
    public EParamPoly3PRange getPRange() {
        return pRange;
    }

    /**
     * Sets the value of the pRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link EParamPoly3PRange }
     *     
     */
    public void setPRange(EParamPoly3PRange value) {
        this.pRange = value;
    }

}
