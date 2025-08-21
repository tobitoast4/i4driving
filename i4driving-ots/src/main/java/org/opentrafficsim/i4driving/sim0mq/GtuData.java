package org.opentrafficsim.i4driving.sim0mq;

import org.opentrafficsim.core.gtu.Gtu;

import java.time.Instant;

public class GtuData {
    private String gtuId;
    private long lastSeen;

    public GtuData(String gtuId) {
        this.gtuId = gtuId;
        this.lastSeen = Instant.now().toEpochMilli();
    }

    public String getGtuId() {
        return gtuId;
    }

    public boolean isAV() {
        if (this.gtuId.startsWith("AV")) {
            return true;
        }
        return false;
    }

    public void updateLastSeen() {
        this.lastSeen = Instant.now().toEpochMilli();
    }

    public boolean wasLongTimeNotSeen() {
        // Returns true, if not seen for mor than 500 ms
        long currentMillis = Instant.now().toEpochMilli();
        if (currentMillis > this.lastSeen + 500) {
            return true;
        }
        return false;
    }
}
