package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

public class SnapshoterRequest extends TableOperationRequest {

    //TODO: extend with required fields

    private String snapshotStorageProjectID;

    private String snapshotStorageDataset;

    private Integer snapshotExpirationMs;

    private Integer timeTravelOffsetMs;


    public SnapshoterRequest(TableSpec targetTable, String runId, String trackingId, String snapshotStorageProjectID, String snapshotStorageDataset, Integer snapshotExpirationMs, Integer timeTravelOffsetMs) {
        super(targetTable, runId, trackingId);
        this.snapshotStorageProjectID = snapshotStorageProjectID;
        this.snapshotStorageDataset = snapshotStorageDataset;
        this.snapshotExpirationMs = snapshotExpirationMs;
        this.timeTravelOffsetMs = timeTravelOffsetMs;
    }

    public String getSnapshotStorageProjectID() {
        return snapshotStorageProjectID;
    }

    public String getSnapshotStorageDataset() {
        return snapshotStorageDataset;
    }

    public Integer getSnapshotExpirationMs() {
        return snapshotExpirationMs;
    }

    public Integer getTimeTravelOffsetMs() {
        return timeTravelOffsetMs;
    }
}
