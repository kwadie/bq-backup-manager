package com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy;

public enum DataCatalogBackupPolicyTagFields {
    backup_cron,
    backup_method,
    backup_time_travel_offset_days,
    bq_snapshot_expiration_days,
    bq_snapshot_storage_dataset,
    bq_snapshot_storage_project,
    gcs_snapshot_storage_location,
    config_source,
    last_backup_at
}
