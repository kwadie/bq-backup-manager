package com.google.cloud.pso.bq_snapshot_manager.services.backup_policy;

import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

import javax.annotation.Nullable;
import java.io.IOException;

public interface BackupPolicyService {

    void createOrUpdateBackupPolicyForTable(TableSpec tableSpec, BackupPolicy backupPolicy) throws IOException;

    @Nullable BackupPolicy getBackupPolicyForTable(TableSpec tableSpec) throws IOException, IllegalArgumentException;

     void shutdown();
}
