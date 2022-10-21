package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

import com.google.cloud.Timestamp;
import com.google.cloud.Tuple;
import com.google.cloud.datacatalog.v1.Tag;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.GCSSnapshotFormat;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter.BigQuerySnapshoterRequest;
import com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter.GCSSnapshoterRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.PersistentSetTestImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.PubSubServiceTestImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogService;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ConfiguratorTest {

    LoggingHelper testLogger = new LoggingHelper(
            ConfiguratorTest.class.getSimpleName(),
            2,
            "testProject"
    );

    String jsonPolicyStr = "{\n" +
            "  \"default_policy\": {\n" +
            "    \"backup_cron\": \"*****\",\n" +
            "    \"backup_method\": \"BigQuery Snapshot\",\n" +
            "    \"backup_time_travel_offset_days\": \"0\",\n" +
            "    \"bq_snapshot_expiration_days\": \"15\",\n" +
            "    \"bq_snapshot_storage_project\": \"project\",\n" +
            "    \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "    \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "    \"gcs_snapshot_format\": \"\"\n" +
            "  },\n" +
            "  \"folder_overrides\": {\n" +
            "    \"folder1\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    },\n" +
            "    \"folder2\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"project_overrides\": {\n" +
            "    \"project1\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    },\n" +
            "    \"project2\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"dataset_overrides\": {\n" +
            "    \"dataset1\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    },\n" +
            "    \"dataset2\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"table_overrides\": {\n" +
            "    \"table1\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    },\n" +
            "    \"table2\": {\n" +
            "      \"backup_cron\": \"*****\",\n" +
            "      \"backup_method\": \"BigQuery Snapshot\",\n" +
            "      \"backup_time_travel_offset_days\": \"0\",\n" +
            "      \"bq_snapshot_expiration_days\": \"15\",\n" +
            "      \"bq_snapshot_storage_project\": \"project\",\n" +
            "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
            "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
            "      \"gcs_snapshot_format\": \"\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

    BackupPolicy testPolicy = new BackupPolicy(
            "*****",
            BackupMethod.BIGQUERY_SNAPSHOT,
            TimeTravelOffsetDays.DAYS_0,
            15.0,
            "project",
            "dataset",
            "gs://bla/",
            GCSSnapshotFormat.AVRO,
            BackupConfigSource.SYSTEM,
            Timestamp.MIN_VALUE,
            "",
            ""
    );

    FallbackBackupPolicy fallbackBackupPolicy = new FallbackBackupPolicy(
            testPolicy,
            // folder level
            Stream.of(
                    new AbstractMap.SimpleEntry<>("folder1", testPolicy))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
            // project level
            Stream.of(
                    new AbstractMap.SimpleEntry<>("p2", testPolicy))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
            // dataset level
            Stream.of(
                    new AbstractMap.SimpleEntry<>("p1.d2", testPolicy))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
            // table level
            Stream.of(new AbstractMap.SimpleEntry<>("p1.d1.t1", testPolicy))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
    );

    @Test
    public void testFindFallbackBackupPolicy() {

        // test table level
        Tuple<String, BackupPolicy> tableLevel = Configurator.findFallbackBackupPolicy(
                fallbackBackupPolicy,
                TableSpec.fromSqlString("p1.d1.t1")
        );

        assertEquals("table", tableLevel.x());
        assertEquals(testPolicy, tableLevel.y());

        // test dataset level
        Tuple<String, BackupPolicy> datasetLevel = Configurator.findFallbackBackupPolicy(
                fallbackBackupPolicy,
                TableSpec.fromSqlString("p1.d2.t1")
        );

        assertEquals("dataset", datasetLevel.x());
        assertEquals(testPolicy, datasetLevel.y());

        // test project level
        Tuple<String, BackupPolicy> projectLevel = Configurator.findFallbackBackupPolicy(
                fallbackBackupPolicy,
                TableSpec.fromSqlString("p2.d1.t1")
        );

        assertEquals("project", projectLevel.x());
        assertEquals(testPolicy, projectLevel.y());

        // test default level
        Tuple<String, BackupPolicy> defaultLevel = Configurator.findFallbackBackupPolicy(
                fallbackBackupPolicy,
                TableSpec.fromSqlString("p9.d1.t1")
        );

        assertEquals("global", defaultLevel.x());
        assertEquals(testPolicy, defaultLevel.y());
    }

    @Test
    public void testGetCronNextTrigger() {

        Tuple<Boolean, LocalDateTime> testFalse = Configurator.getCronNextTrigger(
                "0 0 13 * * *", // daily at 1 PM
                Timestamp.parseTimestamp("2022-10-06T14:00:00Z"), // last backup
                Timestamp.parseTimestamp("2022-10-07T12:00:00Z") // now
        );

        Tuple<Boolean, LocalDateTime> testTrue = Configurator.getCronNextTrigger(
                "0 0 13 * * *", // daily at 1 PM
                Timestamp.parseTimestamp("2022-10-06T14:00:00Z"), // last backup
                Timestamp.parseTimestamp("2022-10-07T14:00:00Z") // now
        );

        assertEquals(false, testFalse.x());
        assertEquals(
                LocalDateTime.of(2022, 10, 7, 13, 0)
                , testFalse.y());

        assertEquals(true, testTrue.x());
        assertEquals(
                LocalDateTime.of(2022, 10, 7, 13, 0)
                , testFalse.y());
    }

    @Test
    public void testIsBackupTime_case1() {

        boolean actual = Configurator.isBackupTime(
                true,
                TableSpec.fromSqlString("p.d.t"),
                "******",
                TrackingHelper.parseRunIdAsTimestamp("1641034800000-T"),
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                testLogger,
                "testTrackingId"
        );

        // true because isForceRun == true
        assertEquals(true, actual);
    }

    @Test
    public void testIsBackupTime_case2() {

        boolean actual = Configurator.isBackupTime(
                false,
                TableSpec.fromSqlString("p.d.t"),
                "******",
                TrackingHelper.parseRunIdAsTimestamp("1641034800000-T"),
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                testLogger,
                "testTrackingId"
        );

        // true because source = SYSTEM & lastBackup = MIN_VALUE
        assertEquals(true, actual);
    }

    @Test
    public void testIsBackupTime_case3() {

        boolean actual = Configurator.isBackupTime(
                false,
                TableSpec.fromSqlString("p.d.t"),
                "******",
                TrackingHelper.parseRunIdAsTimestamp("1641034800000-T"),
                BackupConfigSource.MANUAL,
                Timestamp.MIN_VALUE,
                testLogger,
                "testTrackingId"
        );

        // true because source = MANUAL & lastBackup = MIN_VALUE
        assertEquals(true, actual);
    }

    @Test
    public void testIsBackupTime_case4_true() {

        boolean actual = Configurator.isBackupTime(
                false,
                TableSpec.fromSqlString("p.d.t"),
                "0 0 13 * * *", // daily at 1 PM
                TrackingHelper.parseRunIdAsTimestamp("1665064800000-T"), // 2022-10-06 14:00:00
                BackupConfigSource.MANUAL,
                Timestamp.parseTimestamp("2022-10-06T12:00:00Z"),
                testLogger,
                "testTrackingId"
        );

        // true because lastBackup != MIN_VALUE and ref point > next cron trigger
        assertEquals(true, actual);
    }

    @Test
    public void testIsBackupTime_case4_false() {

        boolean actual = Configurator.isBackupTime(
                false,
                TableSpec.fromSqlString("p.d.t"),
                "0 0 13 * * *", // daily at 1 PM
                TrackingHelper.parseRunIdAsTimestamp("1665064800000-T"), // 2022-10-06 14:00:00
                BackupConfigSource.MANUAL,
                Timestamp.parseTimestamp("2022-10-06T15:00:00Z"),
                testLogger,
                "testTrackingId"
        );

        // true because lastBackup != MIN_VALUE and ref point < next cron trigger
        assertEquals(false, actual);
    }

    private Tuple<PubSubPublishResults, PubSubPublishResults> executeConfigurator(
            TableSpec targetTable,
            String runId,
            String trackingId,
            BackupPolicy testBackupPolicy) throws NonRetryableApplicationException, InterruptedException, IOException {

        ConfiguratorConfig config = new ConfiguratorConfig(
                "test-project",
                "test-bqSnapshoterTopic",
                "test-gcsSnapshoterTopic",
                "test-templateId"
        );

        Configurator configurator = new Configurator(
                config,
                new DataCatalogService() {

                    @Override
                    public Tag createOrUpdateBackupPolicyTag(TableSpec tableSpec, BackupPolicy backupPolicy, String backupPolicyTagTemplateId) {

                        return null;
                    }

                    @Override
                    public BackupPolicy getBackupPolicyTag(TableSpec tableSpec, String backupPolicyTagTemplateId) throws IOException, IllegalArgumentException {
                        return testBackupPolicy;
                    }
                },
                new PubSubServiceTestImpl(),
                new PersistentSetTestImpl(),
                FallbackBackupPolicy.fromJson(jsonPolicyStr), // has no effect since we return a static BackUpPolicy from DC stub
                "test-prefix",
                2
        );

        return configurator.execute(
                new ConfiguratorRequest(
                        false,
                        targetTable,
                        runId,
                        trackingId
                ),
                "pubsubmessageid"
        );
    }

    @Test
    public void testConfiguratorWithBqSnapshots() throws IOException, NonRetryableApplicationException, InterruptedException {

        BackupPolicy backupPolicy = new BackupPolicy(
                "*****",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_7,
                15.0,
                "snapshotProject",
                "snapshotDataset",
                "gs://bla/",
                GCSSnapshotFormat.AVRO,
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                "",
                ""
        );

        TableSpec targetTable = TableSpec.fromSqlString("testProject.testDataset.testTable");

        Tuple<PubSubPublishResults, PubSubPublishResults> publishResults = executeConfigurator(
                targetTable,
                "1665734583289-T",
                "1665734583289-T-xyz",
                backupPolicy);

        // this run returns a static BackupPolicy. Assert expectations based on it
        PubSubPublishResults bigQueryPublishResults = publishResults.x();
        PubSubPublishResults gcsQueryPublishResults = publishResults.y();

        // there shouldn't be any GCS snapshot requests sent to PubSub
        assertEquals(0, gcsQueryPublishResults.getSuccessMessages().size());
        assertEquals(0, gcsQueryPublishResults.getFailedMessages().size());

        // there should be exactly one bigQuery Snapshot request sent to PubSub
        assertEquals(1, bigQueryPublishResults.getSuccessMessages().size());
        BigQuerySnapshoterRequest actualBigQuerySnapshoterRequest = (BigQuerySnapshoterRequest) bigQueryPublishResults
                .getSuccessMessages()
                .get(0)
                .getMsg();

        BigQuerySnapshoterRequest expectedBigQuerySnapshoterRequest = new BigQuerySnapshoterRequest(
                targetTable,
                "1665734583289-T",
                "1665734583289-T-xyz",
                backupPolicy
        );

        assertEquals(expectedBigQuerySnapshoterRequest, actualBigQuerySnapshoterRequest);
    }

    @Test
    public void testConfiguratorWithGCSSnapshots() throws IOException, NonRetryableApplicationException, InterruptedException {

        BackupPolicy backupPolicy = new BackupPolicy(
                "*****",
                BackupMethod.GCS_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                15.0,
                "snapshotProject",
                "snapshotDataset",
                "gs://bucket/folder",
                GCSSnapshotFormat.AVRO,
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                "",
                ""
        );

        TableSpec targetTable = TableSpec.fromSqlString("testProject.testDataset.testTable");

        Tuple<PubSubPublishResults, PubSubPublishResults> publishResults = executeConfigurator(
                targetTable,
                "1665734583289-T",
                "1665734583289-T-xyz",
                backupPolicy);

        // this run returns a static BackupPolicy. Assert expectations based on it
        PubSubPublishResults bigQueryPublishResults = publishResults.x();
        PubSubPublishResults gcsPublishResults = publishResults.y();

        // there shouldn't be any BigQuery snapshot requests sent to PubSub
        assertEquals(0, bigQueryPublishResults.getSuccessMessages().size());
        assertEquals(0, bigQueryPublishResults.getFailedMessages().size());

        // there should be exactly one GCS Snapshot request sent to PubSub
        assertEquals(1, gcsPublishResults.getSuccessMessages().size());
        GCSSnapshoterRequest actualGCSSnapshoterRequest = (GCSSnapshoterRequest) gcsPublishResults
                .getSuccessMessages()
                .get(0)
                .getMsg();

        GCSSnapshoterRequest expectedGCSSnapshoterRequest = new GCSSnapshoterRequest(
                targetTable,
                "1665734583289-T",
                "1665734583289-T-xyz",
                "gs://bucket/folder",
                GCSSnapshotFormat.AVRO
        );

        assertEquals(expectedGCSSnapshoterRequest, actualGCSSnapshoterRequest);
    }

    @Test
    public void testConfiguratorWithBothSnapshots() throws IOException, NonRetryableApplicationException, InterruptedException {

        BackupPolicy backupPolicy = new BackupPolicy(
                "*****",
                BackupMethod.BOTH,
                TimeTravelOffsetDays.DAYS_7,
                15.0,
                "snapshotProject",
                "snapshotDataset",
                "gs://bucket/folder",
                GCSSnapshotFormat.AVRO,
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                "",
                ""
        );

        TableSpec targetTable = TableSpec.fromSqlString("testProject.testDataset.testTable");

        Tuple<PubSubPublishResults, PubSubPublishResults> publishResults = executeConfigurator(
                targetTable,
                "1665734583289-T",
                "1665734583289-T-xyz",
                backupPolicy);

        // this run returns a static BackupPolicy. Assert expectations based on it
        PubSubPublishResults bigQueryPublishResults = publishResults.x();
        PubSubPublishResults gcsPublishResults = publishResults.y();

        // there should one GCS Snapshot request sent to PubSub
        assertEquals(1, gcsPublishResults.getSuccessMessages().size());
        GCSSnapshoterRequest actualGCSSnapshoterRequest = (GCSSnapshoterRequest) gcsPublishResults
                .getSuccessMessages()
                .get(0)
                .getMsg();

        GCSSnapshoterRequest expectedGCSSnapshoterRequest = new GCSSnapshoterRequest(
                targetTable,
                "1665734583289-T",
                "1665734583289-T-xyz",
                "gs://bucket/folder",
                GCSSnapshotFormat.AVRO
        );

        assertEquals(expectedGCSSnapshoterRequest, actualGCSSnapshoterRequest);

        // there should be one bigQuery Snapshot request sent to PubSub
        assertEquals(1, bigQueryPublishResults.getSuccessMessages().size());
        BigQuerySnapshoterRequest actualBigQuerySnapshoterRequest = (BigQuerySnapshoterRequest) bigQueryPublishResults
                .getSuccessMessages()
                .get(0)
                .getMsg();

        BigQuerySnapshoterRequest expectedBigQuerySnapshoterRequest = new BigQuerySnapshoterRequest(
                targetTable,
                "1665734583289-T",
                "1665734583289-T-xyz",
                backupPolicy
        );

        assertEquals(expectedBigQuerySnapshoterRequest, actualBigQuerySnapshoterRequest);
    }


}
