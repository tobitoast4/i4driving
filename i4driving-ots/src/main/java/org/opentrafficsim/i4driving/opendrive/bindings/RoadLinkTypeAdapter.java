package org.opentrafficsim.i4driving.opendrive.bindings;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opentrafficsim.i4driving.opendrive.generated.ERoadLinkElementType;

/**
 * Adapter to change string value in to ERoadLinkElementType as it should have been defined in XSD.
 * @author wjschakel
 */
public class RoadLinkTypeAdapter extends XmlAdapter<String, ERoadLinkElementType>
{

    @Override
    public ERoadLinkElementType unmarshal(final String v)
    {
        return ERoadLinkElementType.fromValue(v);
    }

    @Override
    public String marshal(final ERoadLinkElementType v)
    {
        return v.name();
    }

}
