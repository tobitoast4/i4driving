package org.opentrafficsim.i4driving.messages.adapters;

import java.io.IOException;

import org.djunits.value.vdouble.scalar.Speed;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Adapter to read and write {@code Speed} values in JSON files.
 * @author wjschakel
 */
public class SpeedAdapter extends TypeAdapter<Speed>
{

    /** {@inheritDoc} */
    @Override
    public void write(final JsonWriter out, final Speed value) throws IOException
    {
        out.value(value.toString());
    }

    /** {@inheritDoc} */
    @Override
    public Speed read(final JsonReader in) throws IOException
    {
        return Speed.valueOf(in.nextString());
    }

}
