package com.campus.userservice.dto.algorithm;

import java.util.List;

public class BulkStatusRequest {

    private List<ApplicationStatusUpdate> updates;

    public BulkStatusRequest() {}

    public BulkStatusRequest(List<ApplicationStatusUpdate> updates) {
        this.updates = updates;
    }

    public List<ApplicationStatusUpdate> getUpdates() { return updates; }
    public void setUpdates(List<ApplicationStatusUpdate> updates) { this.updates = updates; }
}
