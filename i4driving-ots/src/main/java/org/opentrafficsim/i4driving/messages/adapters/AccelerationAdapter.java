package org.opentrafficsim.i4driving.messages.adapters;

import java.io.IOException;

import org.djunits.value.vdouble.scalar.Acceleration;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Adapter to read and write {@code Acceleration} values in JSON files.
 * @author wjschakel
 */
public class AccelerationAdapter extends TypeAdapter<Acceleration>
{

    /** {@inheritDoc} */
    @Override
    public void write(final JsonWriter out, final Acceleration value) throws IOException
    {
        out.value(value.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Acceleration read(final JsonReader in) throws IOException
    {
        return Acceleration.valueOf(in.nextString());
    }

}
