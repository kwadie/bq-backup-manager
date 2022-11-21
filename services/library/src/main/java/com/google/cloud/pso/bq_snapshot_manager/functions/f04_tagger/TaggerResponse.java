package com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger;

import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;

import java.util.Objects;

public class TaggerResponse extends TableOperationRequestResponse {

    private final BackupPolicy updatedBackupPolicy;
    private final BackupPolicy originalBackupPolicy;

    public TaggerResponse(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, BackupPolicy updatedBackupPolicy) {
        super(targetTable, runId, trackingId, isDryRun);
        this.updatedBackupPolicy = updatedBackupPolicy;
        this.originalBackupPolicy = null;
    }

    public TaggerResponse(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, BackupPolicy updatedBackupPolicy, BackupPolicy originalBackupPolicy) {
        super(targetTable, runId, trackingId, isDryRun);
        this.updatedBackupPolicy = updatedBackupPolicy;
        this.originalBackupPolicy = originalBackupPolicy;
    }

    public BackupPolicy getUpdatedBackupPolicy() {
        return updatedBackupPolicy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TaggerResponse that = (TaggerResponse) o;
        return updatedBackupPolicy.equals(that.updatedBackupPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), updatedBackupPolicy);
    }

    @Override
    public String toString() {
        return "TaggerResponse{" +
                "originalBackupPolicy=" + updatedBackupPolicy + ","+
                "updatedBackupPolicy=" + updatedBackupPolicy +
                "} " + super.toString();
    }
}
