package org.opentrafficsim.i4driving.messages;

import java.lang.reflect.Type;

import org.djunits.value.vdouble.scalar.Acceleration;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djunits.value.vdouble.scalar.Speed;
import org.djunits.value.vdouble.scalar.Time;
import org.opentrafficsim.i4driving.Settings;
import org.opentrafficsim.i4driving.messages.Commands.CommandType;
import org.opentrafficsim.i4driving.messages.adapters.AccelerationAdapter;
import org.opentrafficsim.i4driving.messages.adapters.CommandTypeAdapter;
import org.opentrafficsim.i4driving.messages.adapters.DurationAdapter;
import org.opentrafficsim.i4driving.messages.adapters.LengthAdapter;
import org.opentrafficsim.i4driving.messages.adapters.SpeedAdapter;
import org.opentrafficsim.i4driving.messages.adapters.TimeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Static utility returning a {@code Gson} with all adapters registered.
 * @author wjschakel
 */
public class DefaultGsonBuilder
{

    /** Commands type for GSON. */
    public static Type COMMANDS = new TypeToken<Commands>()
    {
    }.getType();
    
    /** Settings type for GSON. */
    public static Type SETTINGS = new TypeToken<Settings>()
    {
    }.getType();
    
    /**
     * Returns a {@code Gson} with all adapters registered.
     * @return Gson; with all adapters registered.
     */
    public static Gson get()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Acceleration.class, new AccelerationAdapter());
        builder.registerTypeAdapter(CommandType.class, new CommandTypeAdapter());
        builder.registerTypeAdapter(Duration.class, new DurationAdapter());
        builder.registerTypeAdapter(Length.class, new LengthAdapter());
        builder.registerTypeAdapter(Speed.class, new SpeedAdapter());
        builder.registerTypeAdapter(Time.class, new TimeAdapter());
        return builder.create();
    }

}
