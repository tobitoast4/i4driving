package org.opentrafficsim.i4driving;

import java.util.Map;

/**
 * This class stores the settings from a settings JSON file.
 * @author wjschakel
 */
public class Settings
{
    /** Map of settings. */
    private Map<String, Object> settings;

    /**
     * Returns the argument array as though they were given as command line arguments.
     * @return String[]; argument array as though they were given as command line arguments.
     */
    public String[] getArguments()
    {
        String[] args = new String[this.settings.size()];
        int index = 0;
        for (String key : this.settings.keySet())
        {
            args[index++] = "--" + key + "=" + this.settings.get(key).toString() + "";
        }
        return args;
    }
}
