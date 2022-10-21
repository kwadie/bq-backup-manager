package com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;

import java.util.Objects;

public class TaggerRequest extends TableOperationRequest {

    private final BackupPolicy backupPolicy;
    private final BackupMethod appliedBackupMethod;
    private final String bigQuerySnapshotUri;
    private final String gcsSnapshotUri;
    private final Timestamp lastBackUpAt;

    public TaggerRequest(TableSpec targetTable, String runId, String trackingId, BackupPolicy backupPolicy, BackupMethod appliedBackupMethod, String bigQuerySnapshotUri, String gcsSnapshotUri, Timestamp lastBackUpAt) {
        super(targetTable, runId, trackingId);
        this.backupPolicy = backupPolicy;
        this.appliedBackupMethod = appliedBackupMethod;
        this.bigQuerySnapshotUri = bigQuerySnapshotUri;
        this.gcsSnapshotUri = gcsSnapshotUri;
        this.lastBackUpAt = lastBackUpAt;
    }

    public BackupPolicy getBackupPolicy() {
        return backupPolicy;
    }

    public BackupMethod getAppliedBackupMethod() {
        return appliedBackupMethod;
    }

    public String getBigQuerySnapshotUri() {
        return bigQuerySnapshotUri;
    }

    public String getGcsSnapshotUri() {
        return gcsSnapshotUri;
    }

    public Timestamp getLastBackUpAt() {
        return lastBackUpAt;
    }

    @Override
    public String toString() {
        return "TaggerRequest{" +
                "backupPolicy=" + backupPolicy +
                ", appliedBackupMethod=" + appliedBackupMethod +
                ", bigQuerySnapshotUri='" + bigQuerySnapshotUri + '\'' +
                ", gcsSnapshotUri='" + gcsSnapshotUri + '\'' +
                ", lastBackUpAt=" + lastBackUpAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaggerRequest)) return false;
        if (!super.equals(o)) return false;
        TaggerRequest that = (TaggerRequest) o;
        return getBackupPolicy().equals(that.getBackupPolicy()) &&
                getAppliedBackupMethod() == that.getAppliedBackupMethod() &&
                getBigQuerySnapshotUri().equals(that.getBigQuerySnapshotUri()) &&
                getGcsSnapshotUri().equals(that.getGcsSnapshotUri()) &&
                getLastBackUpAt().equals(that.getLastBackUpAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBackupPolicy(), getAppliedBackupMethod(), getBigQuerySnapshotUri(), getGcsSnapshotUri(), getLastBackUpAt());
    }
}
