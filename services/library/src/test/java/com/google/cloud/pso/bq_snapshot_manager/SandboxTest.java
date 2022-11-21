package com.google.cloud.pso.bq_snapshot_manager;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.RetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.Tagger;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerConfig;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerResponse;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryServiceImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogServiceImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.set.GCSPersistentSetImpl;
import org.junit.Test;

import java.io.IOException;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class SandboxTest {
    @Test
    public void test() throws IOException, InterruptedException, RetryableApplicationException, NonRetryableApplicationException {

        TableSpec tableSpec = new TableSpec("bqsc-dwh-v1", "stress_testing_3000", "stress_test_2707");

        TableSpec snapshot = new TableSpec("bqsc-dwh-v1", "test_backups", "stress_test_2707_snapshot");

        BackupMethod method = BackupMethod.GCS_SNAPSHOT;

        BackupPolicy policy = new BackupPolicy.BackupPolicyBuilder(
                "0 0 */4 * * *",
                method,
                TimeTravelOffsetDays.DAYS_0,
                BackupConfigSource.SYSTEM,
                "bqsc-dwh-v1"
        ).setBigQuerySnapshotStorageDataset("stress_testing_backups")
                .setBigQuerySnapshotExpirationDays(1.0)
                .setGcsSnapshotStorageLocation("gs://bqsc-dwh-v1-backups/stress-tests/")
                .setGcsExportFormat(GCSSnapshotFormat.AVRO_SNAPPY)
                .setGcsUseAvroLogicalTypes(true)
                .build();

        Tagger tagger = new Tagger(
                new LoggingHelper("test",4, "bqsm-host"),
                new TaggerConfig("bqsm-host","projects/bqsm-host/locations/eu/tagTemplates/bq_backup_manager_template", false),
                new DataCatalogServiceImpl(),
                new GCSPersistentSetImpl("bqsm-host-bq-snapshot-mgr-flags"),
                "unittest/tagger/"
        );

        TaggerResponse response = tagger.execute(
                new TaggerRequest(
                        tableSpec,
                        TrackingHelper.generateForcedRunId(),
                        TrackingHelper.generateForcedRunId()+"-ffe905dc-23e7-4f83-b444-5c172df509a0",
                        false,
                        policy,
                        method,
                        snapshot,
                        "gs://testbackuploc",
                        Timestamp.now()
                        ),
                "testpubsubmessageid"+ Math.random()
        );

        System.out.println(response);
    }

    @Test
    public void test2() throws IOException, InterruptedException {

        BigQueryServiceImpl bq = new BigQueryServiceImpl("bqsm-host");
        GCSSnapshotFormat [] formats = {
                GCSSnapshotFormat.CSV,
                GCSSnapshotFormat.CSV_GZIP,
                GCSSnapshotFormat.AVRO,
                GCSSnapshotFormat.AVRO_DEFLATE,
                GCSSnapshotFormat.AVRO_SNAPPY,
                GCSSnapshotFormat.PARQUET,
                GCSSnapshotFormat.PARQUET_SNAPPY,
                GCSSnapshotFormat.PARQUET_GZIP,
                GCSSnapshotFormat.JSON,
                GCSSnapshotFormat.JSON_GZIP

        };

        for(int i=0; i< formats.length; i++){
            GCSSnapshotFormat format = formats[i];
            System.out.println("Backup "+ format+" ..");
            bq.exportToGCS(
                    new TableSpec("bqsm-data-1", "europe", "fake_data"),
                    String.format("gs://bqsm-standard/test/%s/*", format),
                    format,
                    format.equals(GCSSnapshotFormat.CSV) || format.equals(GCSSnapshotFormat.CSV_GZIP)? "|" : null,
                    true,
                    null,
                    "testTrackingg"+i
            );

        }

    }
}
