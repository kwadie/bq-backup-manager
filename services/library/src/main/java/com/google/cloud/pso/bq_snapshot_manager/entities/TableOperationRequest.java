package com.google.cloud.pso.bq_snapshot_manager.entities;

public class TableOperationRequest extends JsonMessage{

    private TableSpec targetTable;
    private String runId;
    private String trackingId;

    public TableOperationRequest(TableSpec targetTable, String runId, String trackingId) {
        this.targetTable = targetTable;
        this.runId = runId;
        this.trackingId = trackingId;
    }

    public TableSpec getTargetTable() {
        return targetTable;
    }

    public String getRunId() {
        return runId;
    }

    public String getTrackingId() {
        return trackingId;
    }
}
