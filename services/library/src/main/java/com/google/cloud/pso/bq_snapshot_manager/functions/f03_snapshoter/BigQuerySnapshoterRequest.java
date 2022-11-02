package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;

import java.util.Objects;

public class BigQuerySnapshoterRequest extends TableOperationRequestResponse {

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
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BigQuerySnapshoterRequest that = (BigQuerySnapshoterRequest) o;
        return backupPolicy.equals(that.backupPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), backupPolicy);
    }

    @Override
    public String toString() {
        return "BigQuerySnapshoterRequest{" +
                "backupPolicy=" + backupPolicy +
                '}';
    }
}
