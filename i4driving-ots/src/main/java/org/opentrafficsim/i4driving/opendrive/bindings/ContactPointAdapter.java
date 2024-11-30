package org.opentrafficsim.i4driving.opendrive.bindings;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opentrafficsim.i4driving.opendrive.generated.EContactPoint;

/**
 * Adapter to change string value in to EContactPoint as it should have been defined in XSD.
 * @author wjschakel
 */
public class ContactPointAdapter extends XmlAdapter<String, EContactPoint>
{

    @Override
    public EContactPoint unmarshal(final String v)
    {
        return EContactPoint.fromValue(v);
    }

    @Override
    public String marshal(final EContactPoint v)
    {
        return v.name();
    }

}
