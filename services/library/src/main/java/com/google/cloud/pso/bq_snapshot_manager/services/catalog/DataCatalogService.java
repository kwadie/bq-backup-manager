package com.google.cloud.pso.bq_snapshot_manager.services.catalog;

import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

import java.io.IOException;

public interface DataCatalogService {

    BackupPolicy getBackupPolicyTag(TableSpec tableSpec, String backupPolicyTagTemplateId) throws IOException, IllegalArgumentException;
}
