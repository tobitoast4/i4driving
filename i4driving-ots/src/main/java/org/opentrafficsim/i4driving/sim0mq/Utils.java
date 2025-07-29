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

    public static double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    public static double angleDistance(double ta, double ca) {
        // All angles in radiant
        // if ta > ca (counter-clockwise rotation), result is + else -
        ta = normalizeAngle(ta);  // target angle   -> t=n+1
        ca = normalizeAngle(ca);  // current angle  -> t=n
        if (Math.abs(ta-ca) < Math.PI) {
            return ta - ca;
        } else if (Math.abs(ta-ca) > Math.PI) {
            if (ta > ca) {
                return - (Math.abs(2*Math.PI - ta) + ca);
            } else {
                return Math.abs(2*Math.PI - ca) + ta;
            }
        } else {
            // ta == ca
            return 0;
        }
    }

    public static double normalizeAngle(double angle) {
        // Normalizes angle to be 0 < angle < 2pi
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }
}
