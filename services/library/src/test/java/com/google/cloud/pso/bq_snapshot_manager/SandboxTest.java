package com.google.cloud.pso.bq_snapshot_manager;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.GCSSnapshotFormat;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupConfigSource;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryServiceImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogServiceImpl;
import org.junit.Test;

import java.io.IOException;

public class SandboxTest {
    @Test
    public void test() throws IOException, InterruptedException {

        String trackingID = TrackingHelper.generateHeartBeatRunId();
        System.out.println(trackingID);

        BigQueryServiceImpl bq = new BigQueryServiceImpl("bqsm-data-1");
        bq.createSnapshot(
                new TableSpec("bqsm-data-1","london","fake_data"),
                new TableSpec("bqsm-data-1","london","fake_data_backup_"+ trackingID),
                Timestamp.parseTimestamp("2023-10-06T12:00:00Z"),
                trackingID
        );
    }

    @Test
    public void test2() throws IOException {

        BackupPolicy policy = new BackupPolicy(
                "*****",
                BackupMethod.GCS_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                15.0,
                "project",
                "dataset",
                "gs://bla/5",
                GCSSnapshotFormat.AVRO,
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                "",
                ""
        );

        new DataCatalogServiceImpl().createOrUpdateBackupPolicyTag(
                TableSpec.fromSqlString("bqsm-data-1.europe.fake_data"),
                policy,
                "projects/bqsm-host/locations/eu/tagTemplates/bq_backup_manager_template"
                );
    }
}
