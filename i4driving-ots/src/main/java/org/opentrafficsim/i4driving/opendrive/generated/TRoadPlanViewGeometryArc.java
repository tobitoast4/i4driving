
package org.opentrafficsim.i4driving.opendrive.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * An arc describes a road reference line with constant curvature. In OpenDRIVE, an arc is represented by an <arc> element within the <geometry> element.
 * 
 * <p>Java class for t_road_planView_geometry_arc complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_planView_geometry_arc"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://code.asam.net/simulation/standard/opendrive_schema}_OpenDriveElement"&gt;
 *       &lt;sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="curvature" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_planView_geometry_arc")
@SuppressWarnings("all") public class TRoadPlanViewGeometryArc
    extends OpenDriveElement
{

    @XmlAttribute(name = "curvature", required = true)
    protected double curvature;

    /**
     * Gets the value of the curvature property.
     * 
     */
    public double getCurvature() {
        return curvature;
    }

    /**
     * Sets the value of the curvature property.
     * 
     */
    public void setCurvature(double value) {
        this.curvature = value;
    }

}
