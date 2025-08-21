package org.opentrafficsim.i4driving.sim0mq;

import java.time.Instant;

public class GtuDataAV {
    private String gtuId;
    private String mergingNode;
    private boolean mergingNodePassed;

    public GtuDataAV(String gtuId, String mergingNode) {
        this.gtuId = gtuId;
        this.mergingNode = mergingNode;
        this.mergingNodePassed = false;
    }

    public String getGtuId() {
        return gtuId;
    }

    public String getMergingNode() {
        return mergingNode;
    }

    public boolean isMergingNodePassed() {
        return mergingNodePassed;
    }

    public void setMergingNodePassed(boolean mergingNodePassed) {
        this.mergingNodePassed = mergingNodePassed;
    }
}
