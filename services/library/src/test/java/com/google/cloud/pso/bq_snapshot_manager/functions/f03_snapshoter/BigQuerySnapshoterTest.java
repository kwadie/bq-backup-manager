package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.Tuple;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;
import com.google.cloud.pso.bq_snapshot_manager.services.PersistentSetTestImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.PubSubServiceTestImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryService;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BigQuerySnapshoterTest {

    @Test
    public void testGetSnapshotTableSpec(){

        TableSpec actual = BigQuerySnapshoter.getSnapshotTableSpec(
                TableSpec.fromSqlString("p.d.t"),
                "sProject",
                "sDataset",
                "123",
                100L
        );

        TableSpec expected = new TableSpec("sProject", "sDataset", "p_d_t_123_100");

        assertEquals(expected, actual);
    }

    @Test
    public void testExecute() throws NonRetryableApplicationException, IOException, InterruptedException {

        BigQuerySnapshoter snapshoter = new BigQuerySnapshoter(
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
                3
        );

        BackupPolicy backupPolicy = new BackupPolicy.BackupPolicyBuilder("test-cron",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_3,
                BackupConfigSource.SYSTEM,
                "backup-p")
                .setBigQuerySnapshotExpirationDays(15.0)
                .setBigQuerySnapshotStorageDataset("backup-d")
                .setGcsSnapshotStorageLocation("gs://bucket/folder")
                .build();

        TableSpec sourceTable = TableSpec.fromSqlString("project.dataset.table");
        Timestamp operationTime = Timestamp.ofTimeSecondsAndNanos(1667478075L, 0);
        Long timeTravelMilis = (operationTime.getSeconds() - (3* 86400))*1000;
        TableSpec expectedSourceTable = TableSpec.fromSqlString("project.dataset.table@"+timeTravelMilis);
        TableSpec expectedSnapshotTable = TableSpec.fromSqlString("backup-p.backup-d.project_dataset_table_runId_"+timeTravelMilis);

        BigQuerySnapshoterResponse actualResponse = snapshoter.execute(
                new SnapshoterRequest(
                        sourceTable,
                        "runId",
                        "trackingId",
                        backupPolicy
                ),
                operationTime,
                "pubsub-message-id");


        TaggerRequest expectedTaggerRequest = new TaggerRequest(
                sourceTable,
                "runId",
                "trackingId",
                backupPolicy,
                BackupMethod.BIGQUERY_SNAPSHOT,
                expectedSnapshotTable,
                null,
                operationTime
        );

        assertEquals(expectedTaggerRequest, actualResponse.getOutputTaggerRequest());
        assertEquals(expectedSourceTable, actualResponse.getComputedSourceTable());
        assertEquals(expectedSnapshotTable, actualResponse.getComputedSnapshotTable());
        assertEquals(operationTime, actualResponse.getOperationTs());

    }


}
