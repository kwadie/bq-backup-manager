package com.google.cloud.pso.bq_snapshot_manager.entities;

import com.google.common.base.Objects;

public class TableOperationRequestResponse extends JsonMessage{

    private TableSpec targetTable;
    private String runId;
    private String trackingId;

    private boolean isDryRun;

    public TableOperationRequestResponse(TableSpec targetTable, String runId, String trackingId, boolean isDryRun) {
        this.targetTable = targetTable;
        this.runId = runId;
        this.trackingId = trackingId;
        this.isDryRun = isDryRun;
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

    public boolean isDryRun() {
        return isDryRun;
    }

    @Override
    public String toString() {
        return "TableOperationRequest{" +
                "targetTable=" + targetTable +
                ", runId='" + runId + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", isDryRun='" + isDryRun + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableOperationRequestResponse that = (TableOperationRequestResponse) o;
        return isDryRun == that.isDryRun && Objects.equal(targetTable, that.targetTable) && Objects.equal(runId, that.runId) && Objects.equal(trackingId, that.trackingId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetTable, runId, trackingId, isDryRun);
    }
}
