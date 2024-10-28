
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * In OpenDRIVE, a spiral is represented by a <spiral> element within the <geometry> element.
 * 
 * <p>Java class for t_road_planView_geometry_spiral complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_planView_geometry_spiral"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="curvStart" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="curvEnd" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_planView_geometry_spiral")
@SuppressWarnings("all") public class TRoadPlanViewGeometrySpiral
    extends OpenDriveElement
{

    @XmlAttribute(name = "curvStart", required = true)
    protected double curvStart;
    @XmlAttribute(name = "curvEnd", required = true)
    protected double curvEnd;

    /**
     * Gets the value of the curvStart property.
     * 
     */
    public double getCurvStart() {
        return curvStart;
    }

    /**
     * Sets the value of the curvStart property.
     * 
     */
    public void setCurvStart(double value) {
        this.curvStart = value;
    }

    /**
     * Gets the value of the curvEnd property.
     * 
     */
    public double getCurvEnd() {
        return curvEnd;
    }

    /**
     * Sets the value of the curvEnd property.
     * 
     */
    public void setCurvEnd(double value) {
        this.curvEnd = value;
    }

}
