package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.common.base.Objects;

public class SnapshoterRequest extends TableOperationRequestResponse {

    private final BackupPolicy backupPolicy;

    public SnapshoterRequest(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, BackupPolicy backupPolicy) {
        super(targetTable, runId, trackingId, isDryRun);
        this.backupPolicy = backupPolicy;
    }

    public BackupPolicy getBackupPolicy() {
        return backupPolicy;
    }

    @Override
    public String toString() {
        return "SnapshoterRequest{" +
                "backupPolicy=" + backupPolicy +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SnapshoterRequest that = (SnapshoterRequest) o;
        return Objects.equal(backupPolicy, that.backupPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), backupPolicy);
    }
}
