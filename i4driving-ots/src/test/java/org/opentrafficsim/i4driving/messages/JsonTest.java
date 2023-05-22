package org.opentrafficsim.i4driving.messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonTest
{

    @Test
    public void testGson() throws JsonSyntaxException, IOException
    {
        Locale.setDefault(Locale.US);

        Gson gson = DefaultGsonBuilder.get();
        Commands commands =
                gson.fromJson(Files.readString(Path.of("./src/main/resources/vehicle1.json")), DefaultGsonBuilder.COMMANDS);
        System.out.println(gson.toJson(commands));

    }

}
