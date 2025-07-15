package org.opentrafficsim.i4driving.sim0mq;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    public static String tryGetString(JSONObject jsonObject, String key, String defaultValue) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static double tryGetDouble(JSONObject jsonObject, String key, double defaultValue) {
        try {
            return jsonObject.getDouble(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static JSONObject tryGetJSONObject(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONObject(key);
        } catch (JSONException e) {
            return null;
        }
    }
}
