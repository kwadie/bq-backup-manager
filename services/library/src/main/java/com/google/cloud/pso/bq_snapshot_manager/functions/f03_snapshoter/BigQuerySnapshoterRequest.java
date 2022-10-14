package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;

import java.util.Objects;

public class BigQuerySnapshoterRequest extends TableOperationRequest {

    private final String snapshotStorageProjectID;

    private final String snapshotStorageDataset;

    private final Long snapshotExpirationMs;

    private final TimeTravelOffsetDays timeTravelOffsetDays;


    public BigQuerySnapshoterRequest(TableSpec targetTable, String runId, String trackingId, String snapshotStorageProjectID, String snapshotStorageDataset, Long snapshotExpirationMs, TimeTravelOffsetDays timeTravelOffsetDays) {
        super(targetTable, runId, trackingId);
        this.snapshotStorageProjectID = snapshotStorageProjectID;
        this.snapshotStorageDataset = snapshotStorageDataset;
        this.snapshotExpirationMs = snapshotExpirationMs;
        this.timeTravelOffsetDays = timeTravelOffsetDays;
    }

    public String getSnapshotStorageProjectID() {
        return snapshotStorageProjectID;
    }

    public String getSnapshotStorageDataset() {
        return snapshotStorageDataset;
    }

    public Long getSnapshotExpirationMs() {
        return snapshotExpirationMs;
    }

    public TimeTravelOffsetDays getTimeTravelOffsetDays() {
        return timeTravelOffsetDays;
    }

    @Override
    public String toString() {
        return "BigQuerySnapshoterRequest{" +
                "snapshotStorageProjectID='" + snapshotStorageProjectID + '\'' +
                ", snapshotStorageDataset='" + snapshotStorageDataset + '\'' +
                ", snapshotExpirationMs=" + snapshotExpirationMs +
                ", timeTravelOffsetDays=" + timeTravelOffsetDays +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigQuerySnapshoterRequest that = (BigQuerySnapshoterRequest) o;
        return getSnapshotStorageProjectID().equals(that.getSnapshotStorageProjectID()) &&
                getSnapshotStorageDataset().equals(that.getSnapshotStorageDataset()) &&
                getSnapshotExpirationMs().equals(that.getSnapshotExpirationMs()) &&
                getTimeTravelOffsetDays().equals(that.getTimeTravelOffsetDays());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSnapshotStorageProjectID(), getSnapshotStorageDataset(), getSnapshotExpirationMs(), getTimeTravelOffsetDays());
    }
}
