package org.opentrafficsim.i4driving.messages.adapters;

import java.io.IOException;

import org.djunits.value.vdouble.scalar.Length;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Adapter to read and write {@code Length} values in JSON files.
 * @author wjschakel
 */
public class LengthAdapter extends TypeAdapter<Length>
{

    /** {@inheritDoc} */
    @Override
    public void write(final JsonWriter out, final Length value) throws IOException
    {
        out.value(value.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Length read(final JsonReader in) throws IOException
    {
        return Length.valueOf(in.nextString());
    }

}
