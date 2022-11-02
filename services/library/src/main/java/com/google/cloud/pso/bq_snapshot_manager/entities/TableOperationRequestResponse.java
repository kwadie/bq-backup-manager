package com.google.cloud.pso.bq_snapshot_manager.entities;

import java.util.Objects;

public class TableOperationRequestResponse extends JsonMessage{

    private TableSpec targetTable;
    private String runId;
    private String trackingId;

    public TableOperationRequestResponse(TableSpec targetTable, String runId, String trackingId) {
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

    @Override
    public String toString() {
        return "TableOperationRequest{" +
                "targetTable=" + targetTable +
                ", runId='" + runId + '\'' +
                ", trackingId='" + trackingId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableOperationRequestResponse)) return false;
        TableOperationRequestResponse that = (TableOperationRequestResponse) o;
        return getTargetTable().equals(that.getTargetTable()) &&
                getRunId().equals(that.getRunId()) &&
                getTrackingId().equals(that.getTrackingId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargetTable(), getRunId(), getTrackingId());
    }
}
