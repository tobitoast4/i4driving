package org.opentrafficsim.i4driving.messages.adapters;

import java.io.IOException;

import org.djunits.value.vdouble.scalar.Time;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Adapter to read and write {@code Time} values in JSON files.
 * @author wjschakel
 */
public class TimeAdapter extends TypeAdapter<Time>
{

    /** {@inheritDoc} */
    @Override
    public void write(final JsonWriter out, final Time value) throws IOException
    {
        out.value(value.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Time read(final JsonReader in) throws IOException
    {
        return Time.valueOf(in.nextString());
    }

}
