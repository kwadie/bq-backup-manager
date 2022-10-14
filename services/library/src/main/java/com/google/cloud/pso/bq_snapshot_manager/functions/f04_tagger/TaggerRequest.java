package com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;

import java.util.Objects;

public class TaggerRequest extends TableOperationRequest {

    private final BackupMethod backupMethod;
    private final String bigQuerySnapshotLocation;
    private final Timestamp bigQuerySnapshotAt;
    private final String gcsSnapshotLocation;
    private final Timestamp gcsSnapshotAt;

    public TaggerRequest(TableSpec targetTable, String runId, String trackingId, BackupMethod backupMethod, String bigQuerySnapshotLocation, Timestamp bigQuerySnapshotAt, String gcsSnapshotLocation, Timestamp gcsSnapshotAt) {
        super(targetTable, runId, trackingId);
        this.backupMethod = backupMethod;
        this.bigQuerySnapshotLocation = bigQuerySnapshotLocation;
        this.bigQuerySnapshotAt = bigQuerySnapshotAt;
        this.gcsSnapshotLocation = gcsSnapshotLocation;
        this.gcsSnapshotAt = gcsSnapshotAt;
    }

    public BackupMethod getBackupMethod() {
        return backupMethod;
    }

    public String getBigQuerySnapshotLocation() {
        return bigQuerySnapshotLocation;
    }

    public Timestamp getBigQuerySnapshotAt() {
        return bigQuerySnapshotAt;
    }

    public String getGcsSnapshotLocation() {
        return gcsSnapshotLocation;
    }

    public Timestamp getGcsSnapshotAt() {
        return gcsSnapshotAt;
    }

    @Override
    public String toString() {
        return "TaggerRequest{" +
                "backupMethod=" + backupMethod +
                ", bigQuerySnapshotLocation='" + bigQuerySnapshotLocation + '\'' +
                ", bigQuerySnapshotAt=" + bigQuerySnapshotAt +
                ", gcsSnapshotLocation='" + gcsSnapshotLocation + '\'' +
                ", gcsSnapshotAt=" + gcsSnapshotAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaggerRequest)) return false;
        if (!super.equals(o)) return false;
        TaggerRequest that = (TaggerRequest) o;
        return getBackupMethod() == that.getBackupMethod() &&
                getBigQuerySnapshotLocation().equals(that.getBigQuerySnapshotLocation()) &&
                getBigQuerySnapshotAt().equals(that.getBigQuerySnapshotAt()) &&
                getGcsSnapshotLocation().equals(that.getGcsSnapshotLocation()) &&
                getGcsSnapshotAt().equals(that.getGcsSnapshotAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBackupMethod(), getBigQuerySnapshotLocation(), getBigQuerySnapshotAt(), getGcsSnapshotLocation(), getGcsSnapshotAt());
    }
}
