package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupConfigSource;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SnapshoterRequestTest {

    @Test
    public void testComputeBackupOperationProject1() {

        // test with backupOperationProject set

        BackupPolicy policy = new BackupPolicy.BackupPolicyBuilder("test-cron",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                BackupConfigSource.SYSTEM,
                "storage-project")
                .setBackupOperationProject("operation-project")
                .setBigQuerySnapshotExpirationDays(0.0)
                .setBigQuerySnapshotStorageDataset("test-dataset")
                .setGcsSnapshotStorageLocation("test-bucket")
                .setLastBackupAt(Timestamp.MAX_VALUE)
                .setLastBqSnapshotStorageUri("last bq uri")
                .setLastGcsSnapshotStorageUri("last gcs uri")
                .build();

        SnapshoterRequest request = new SnapshoterRequest(
                TableSpec.fromSqlString("source_project.dataset.table"),
                "run-id",
                "tracking-id",
                false,
                policy
        );

        assertEquals("operation-project", request.computeBackupOperationProject());
    }

    public void testComputeBackupOperationProject2() {

        // test with backupOperationProject missing

        BackupPolicy policy = new BackupPolicy.BackupPolicyBuilder("test-cron",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                BackupConfigSource.SYSTEM,
                "storage-project")
                .setBigQuerySnapshotExpirationDays(0.0)
                .setBigQuerySnapshotStorageDataset("test-dataset")
                .setGcsSnapshotStorageLocation("test-bucket")
                .setLastBackupAt(Timestamp.MAX_VALUE)
                .setLastBqSnapshotStorageUri("last bq uri")
                .setLastGcsSnapshotStorageUri("last gcs uri")
                .build();

        SnapshoterRequest request = new SnapshoterRequest(
                TableSpec.fromSqlString("source_project.dataset.table"),
                "run-id",
                "tracking-id",
                false,
                policy
        );

        assertEquals("source_project", request.computeBackupOperationProject());
    }
}
