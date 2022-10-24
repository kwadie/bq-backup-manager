package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;

import java.util.Objects;

public class BigQuerySnapshoterRequest extends TableOperationRequest {

    private final BackupPolicy backupPolicy;

    public BigQuerySnapshoterRequest(TableSpec targetTable, String runId, String trackingId, BackupPolicy backupPolicy) {
        super(targetTable, runId, trackingId);
        this.backupPolicy = backupPolicy;
    }

    public BackupPolicy getBackupPolicy() {
        return backupPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BigQuerySnapshoterRequest)) return false;
        if (!super.equals(o)) return false;
        BigQuerySnapshoterRequest that = (BigQuerySnapshoterRequest) o;
        return getBackupPolicy().equals(that.getBackupPolicy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBackupPolicy());
    }

    @Override
    public String toString() {
        return "BigQuerySnapshoterRequest{" +
                "backupPolicy=" + backupPolicy +
                '}';
    }
}
