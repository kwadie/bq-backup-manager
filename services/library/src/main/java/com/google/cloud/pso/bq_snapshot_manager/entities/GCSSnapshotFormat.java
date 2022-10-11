package com.google.cloud.pso.bq_snapshot_manager.entities;

import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupConfigSource;

import java.util.Arrays;

public enum GCSSnapshotFormat {
    CSV,
    CSV_GZIP,
    JSON,
    JSON_GZIP,
    AVRO,
    AVRO_DEFLATE,
    AVRO_SNAPPY,
    PARQUET,
    PARQUET_SNAPPY,
    PARQUET_GZIP
}
