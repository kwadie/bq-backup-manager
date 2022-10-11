package com.google.cloud.pso.bq_snapshot_manager.entities;

import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BackupMethodTest {

    @Test
    public void backupMethodTest() throws IllegalArgumentException {

        BackupMethod bqSnapshotMethod = BackupMethod.fromString("BigQuery Snapshot");
        BackupMethod gcsSnapshotMethod = BackupMethod.fromString("GCS Snapshot");
        BackupMethod bothMethod = BackupMethod.fromString("Both");

        assertEquals(BackupMethod.BIGQUERY_SNAPSHOT, bqSnapshotMethod);
        assertEquals(BackupMethod.GCS_SNAPSHOT, gcsSnapshotMethod);
        assertEquals(BackupMethod.BOTH, bothMethod);
    }

    @Test(expected = IllegalArgumentException.class)
    public void backupMethodExceptionTest() throws IllegalArgumentException {
        BackupMethod method = BackupMethod.fromString("INVALID NAME");
    }
}
