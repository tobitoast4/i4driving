package org.opentrafficsim.i4driving.opendrive.bindings;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.opentrafficsim.i4driving.opendrive.generated.ERoadMarkType;
import org.opentrafficsim.road.network.lane.Stripe;
import org.opentrafficsim.road.network.lane.Stripe.Type;

/**
 * Adapter for road mark and OTS stripe type.
 * @author wjschakel
 */
public class StripeTypeAdapter extends XmlAdapter<ERoadMarkType, Stripe.Type>
{
    @Override
    public Type unmarshal(final ERoadMarkType v) throws UnsupportedOperationException
    {
        switch (v)
        {
            case BOTTS_DOTS:
            case BROKEN:
            case BROKEN_BROKEN:
                return Type.DASHED;
            case BROKEN_SOLID:
                return Type.RIGHT;
            case CURB:
            case EDGE:
            case SOLID:
                return Type.SOLID;
            case SOLID_BROKEN:
                return Type.LEFT;
            case SOLID_SOLID:
                return Type.DOUBLE;
            case CUSTOM:
            case GRASS:
            case NONE:
            default:
                throw new UnsupportedOperationException("Unsupported line type " + v);
        }
    }

    @Override
    public ERoadMarkType marshal(final Type v) throws UnsupportedOperationException
    {
        switch (v)
        {
            case BLOCK:
            case DASHED:
                return ERoadMarkType.BROKEN;
            case DOUBLE:
                return ERoadMarkType.SOLID_SOLID;
            case LEFT:
                return ERoadMarkType.SOLID_BROKEN;
            case RIGHT:
                return ERoadMarkType.BROKEN_SOLID;
            case SOLID:
                return ERoadMarkType.SOLID;
            default:
                throw new UnsupportedOperationException("Unsupported road mark " + v);
        }
    }
}
