package org.opentrafficsim.i4driving.messages.adapters;

import java.io.IOException;

import org.djunits.value.vdouble.scalar.Duration;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Adapter to read and write {@code Duration} values in JSON files.
 * @author wjschakel
 */
public class DurationAdapter extends TypeAdapter<Duration>
{

    /** {@inheritDoc} */
    @Override
    public void write(final JsonWriter out, final Duration value) throws IOException
    {
        out.value(value.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Duration read(final JsonReader in) throws IOException
    {
        return Duration.valueOf(in.nextString());
    }

}
