package com.google.cloud.pso.bq_snapshot_manager.services.catalog;

import com.google.cloud.datacatalog.v1.Entry;
import com.google.cloud.datacatalog.v1.LookupEntryRequest;
import com.google.cloud.datacatalog.v1.Tag;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

import java.io.IOException;

public interface DataCatalogService {

    Tag createOrUpdateBackupPolicyTag(TableSpec tableSpec, BackupPolicy backupPolicy, String backupPolicyTagTemplateId);
//    void createBackupPolicyTag(TableSpec tableSpec, BackupPolicy backupPolicy, String backupPolicyTagTemplateId);
//    void updateBackupPolicyTag(TableSpec tableSpec, BackupPolicy backupPolicy, String backupPolicyTagTemplateId);
    BackupPolicy getBackupPolicyTag(TableSpec tableSpec, String backupPolicyTagTemplateId) throws IOException, IllegalArgumentException;
}
