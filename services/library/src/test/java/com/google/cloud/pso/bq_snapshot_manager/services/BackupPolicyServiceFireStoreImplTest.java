package com.google.cloud.pso.bq_snapshot_manager.services;

import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupConfigSource;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.services.backup_policy.BackupPolicyServiceFireStoreImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BackupPolicyServiceFireStoreImplTest {

    @Test
    public void testWrite() {

        //TODO create unit test with Datastore emulator

//        BackupPolicy backupPolicy = new BackupPolicy.BackupPolicyBuilder("test-cron",
//                BackupMethod.BIGQUERY_SNAPSHOT,
//                TimeTravelOffsetDays.DAYS_0,
//                BackupConfigSource.MANUAL,
//                "storage-project"
//        )
//                .setBackupOperationProject("operation_project")
//                .setBigQuerySnapshotExpirationDays(0.0)
//                .setBigQuerySnapshotStorageDataset("test-dataset")
//                .build();
//
//        BackupPolicyServiceFireStoreImpl service = new BackupPolicyServiceFireStoreImpl();
//        service.createOrUpdateBackupPolicyForTable(
//                TableSpec.fromSqlString("test.test.test"),
//                backupPolicy
//        );
//
//        BackupPolicy actual = service.getBackupPolicyForTable(
//                TableSpec.fromSqlString("test.test.test")
//        );
//
//        assertEquals(backupPolicy, actual);
    }
}
