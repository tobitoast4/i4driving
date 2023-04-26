package org.opentrafficsim.i4driving.messages.adapters;

import java.io.IOException;

import org.opentrafficsim.i4driving.messages.Commands.CommandType;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Adapter to read and write {@code CommandType} values in JSON files.
 * @author wjschakel
 */
public class CommandTypeAdapter extends TypeAdapter<CommandType>
{

    /** {@inheritDoc} */
    @Override
    public void write(final JsonWriter out, final CommandType value) throws IOException
    {
        out.value(value.printValue());
        
    }

    /** {@inheritDoc} */
    @Override
    public CommandType read(final JsonReader in) throws IOException
    {
        String value = in.nextString();
        for (CommandType commandType : CommandType.values())
        {
            if (commandType.printValue().equals(value))
            {
                return commandType;
            }
        }
        throw new IOException("Value " + value + " is not a valid CommandType.");
    }

}
