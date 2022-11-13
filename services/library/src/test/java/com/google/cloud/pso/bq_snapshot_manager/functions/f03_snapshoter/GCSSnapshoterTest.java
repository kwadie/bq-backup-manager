package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.services.PersistentSetTestImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.PubSubServiceTestImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryService;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.SuccessPubSubMessage;
import org.junit.Test;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class GCSSnapshoterTest {

    @Test
    public void testPrepareGcsUriForMultiFileExport() {

        assertEquals(
                "gs://backups/a/b/c/*",
                GCSSnapshoter.prepareGcsUriForMultiFileExport("gs://backups/", "a/b/c")
        );

        assertEquals(
                "gs://backups/a/b/c/*",
                GCSSnapshoter.prepareGcsUriForMultiFileExport("gs://backups", "/a/b/c/")
        );
    }

    @Test
    public void testExecute() throws NonRetryableApplicationException, IOException, InterruptedException {

        GCSSnapshoter gcsSnapshoter = new GCSSnapshoter(
                new SnapshoterConfig("host-project", false, "data-region"),
                new BigQueryService() {
                    @Override
                    public void createSnapshot(TableSpec sourceTable, TableSpec destinationId, Timestamp snapshotExpirationTs, String trackingId) throws InterruptedException {
                    }

                    @Override
                    public void exportToGCS(TableSpec sourceTable, String gcsDestinationUri, GCSSnapshotFormat exportFormat, @Nullable String csvFieldDelimiter, @Nullable Boolean csvPrintHeader, @Nullable Boolean useAvroLogicalTypes, String trackingId) throws InterruptedException {
                    }
                },
                new PubSubServiceTestImpl(),
                new PersistentSetTestImpl(),
                "test-prefix",
                -3
        );

        BackupPolicy backupPolicy = new BackupPolicy.BackupPolicyBuilder("test-cron",
                BackupMethod.GCS_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_3,
                BackupConfigSource.SYSTEM,
                "project")
                .setGcsSnapshotStorageLocation("gs://backups")
                .setGcsExportFormat(GCSSnapshotFormat.AVRO_SNAPPY)
                .setGcsUseAvroLogicalTypes(true)
                .build();

        TableSpec sourceTable = TableSpec.fromSqlString("project.dataset.table");
        Timestamp operationTime = Timestamp.ofTimeSecondsAndNanos(1667478075L, 0);
        Long timeTravelMilis = (operationTime.getSeconds() - (3* 86400))*1000;
        TableSpec expectedSourceTable = TableSpec.fromSqlString("project.dataset.table@"+timeTravelMilis);

        GCSSnapshoterResponse actualResponse = gcsSnapshoter.execute(
                new SnapshoterRequest(
                        sourceTable,
                        "runId",
                        "trackingId",
                        false,
                        backupPolicy
                ),
                operationTime,
                "pubsub-message-id");


        TaggerRequest expectedTaggerRequest = new TaggerRequest(
                sourceTable,
                "runId",
                "trackingId",
                false,
                backupPolicy,
                BackupMethod.GCS_SNAPSHOT,
                null,
                String.format("gs://backups/project/dataset/table/trackingId/%s/AVRO_SNAPPY/*", timeTravelMilis),
                operationTime
        );

        assertEquals(expectedTaggerRequest, actualResponse.getOutputTaggerRequest());
        assertEquals(expectedSourceTable, actualResponse.getComputedSourceTable());
        assertEquals(operationTime, actualResponse.getOperationTs());

    }
}
