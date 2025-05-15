package org.opentrafficsim.i4driving.messages.adapters;

import java.io.IOException;

import org.djunits.value.vdouble.scalar.Frequency;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Adapter to read and write {@code Frequency} values in JSON files.
 * @author wjschakel
 */
public class FrequencyAdapter extends TypeAdapter<Frequency>
{

    /** {@inheritDoc} */
    @Override
    public void write(final JsonWriter out, final Frequency value) throws IOException
    {
        out.value(value.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Frequency read(final JsonReader in) throws IOException
    {
        return Frequency.valueOf(in.nextString());
    }

}
