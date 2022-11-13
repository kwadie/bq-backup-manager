package com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.common.base.Objects;

public class TaggerRequest extends TableOperationRequestResponse {

    private final BackupPolicy backupPolicy;
    private final BackupMethod appliedBackupMethod;
    private final TableSpec bigQuerySnapshotTableSpec;
    private final String gcsSnapshotUri;
    private final Timestamp lastBackUpAt;

    public TaggerRequest(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, BackupPolicy backupPolicy, BackupMethod appliedBackupMethod, TableSpec bigQuerySnapshotTableSpec, String gcsSnapshotUri, Timestamp lastBackUpAt) {
        super(targetTable, runId, trackingId, isDryRun);
        this.backupPolicy = backupPolicy;
        this.appliedBackupMethod = appliedBackupMethod;
        this.bigQuerySnapshotTableSpec = bigQuerySnapshotTableSpec;
        this.gcsSnapshotUri = gcsSnapshotUri;
        this.lastBackUpAt = lastBackUpAt;
    }

    public BackupPolicy getBackupPolicy() {
        return backupPolicy;
    }

    public BackupMethod getAppliedBackupMethod() {
        return appliedBackupMethod;
    }

    public TableSpec getBigQuerySnapshotTableSpec() {
        return bigQuerySnapshotTableSpec;
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
                ", bigQuerySnapshotTableSpec=" + bigQuerySnapshotTableSpec +
                ", gcsSnapshotUri='" + gcsSnapshotUri + '\'' +
                ", lastBackUpAt=" + lastBackUpAt +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TaggerRequest that = (TaggerRequest) o;
        return Objects.equal(backupPolicy, that.backupPolicy) && appliedBackupMethod == that.appliedBackupMethod && Objects.equal(bigQuerySnapshotTableSpec, that.bigQuerySnapshotTableSpec) && Objects.equal(gcsSnapshotUri, that.gcsSnapshotUri) && Objects.equal(lastBackUpAt, that.lastBackUpAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), backupPolicy, appliedBackupMethod, bigQuerySnapshotTableSpec, gcsSnapshotUri, lastBackUpAt);
    }
}
