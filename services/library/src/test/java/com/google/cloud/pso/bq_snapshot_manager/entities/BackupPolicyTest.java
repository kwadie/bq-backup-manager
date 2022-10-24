package com.google.cloud.pso.bq_snapshot_manager.entities;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BackupPolicyTest {

    @Test
    public void testFromJson(){

        String jsonPolicyStr = "{\n" +
                "    \"backup_cron\": \"*****\",\n" +
                "    \"backup_method\": \"BigQuery Snapshot\",\n" +
                "    \"backup_time_travel_offset_days\": \"0\",\n" +
                "    \"bq_snapshot_expiration_days\": \"15\",\n" +
                "    \"bq_snapshot_storage_project\": \"project\",\n" +
                "    \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
                "    \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
                "    \"gcs_snapshot_format\": \"AVRO\",\n" +
                "    \"config_source\": \"SYSTEM\",\n" +
                "    \"last_backup_at\": \"\"\n" +
                "  }";

        BackupPolicy expected = new BackupPolicy(
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

        BackupPolicy actual = BackupPolicy.fromJson(jsonPolicyStr);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() throws IllegalArgumentException {

        String jsonPolicyStr = "{\n" +
                "    \"backup_cron\": \"*****\",\n" +
                "    \"backup_method\": \"INVALID METHOD\",\n" +
                "    \"backup_time_travel_offset_days\": \"0\",\n" +
                "    \"bq_snapshot_expiration_days\": \"15\",\n" +
                "    \"bq_snapshot_storage_project\": \"project\",\n" +
                "    \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
                "    \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
                "    \"gcs_snapshot_format\": \"\",\n" +
                "    \"config_source\": \"SYSTEM\"\n" +
                "  }";

        // should fail because of backup_method = "INVALID METHOD"
        BackupPolicy.fromJson(jsonPolicyStr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissing() throws IllegalArgumentException {

        String jsonPolicyStr = "{\n" +
                "    \"backup_time_travel_offset_days\": \"0\",\n" +
                "    \"bq_snapshot_expiration_days\": \"15\" \n" +
                "  }";

        // should fail because of missing configurations
        BackupPolicy.fromJson(jsonPolicyStr);
    }

    @Test
    public void testFromMapFromFallbackFalse() throws IOException, IllegalArgumentException {

        Map<String, String> tagMap = new HashMap<>();

        tagMap.put("backup_cron", "test-cron");
        tagMap.put("backup_method", "BigQuery Snapshot");
        tagMap.put("config_source", "System");
        tagMap.put("backup_time_travel_offset_days", "0");
        tagMap.put("bq_snapshot_storage_project", "test-project");
        tagMap.put("bq_snapshot_storage_dataset", "test-dataset");
        tagMap.put("bq_snapshot_expiration_days", "0.0");
        tagMap.put("gcs_snapshot_storage_location", "test-bucket");
        tagMap.put("gcs_snapshot_format", "");
        tagMap.put("last_backup_at", Timestamp.MAX_VALUE.toString());
        tagMap.put("last_bq_snapshot_storage_uri", "last bq uri");
        tagMap.put("last_gcs_snapshot_storage_uri", "last gcs uri");


        BackupPolicy expected = new BackupPolicy(
                "test-cron",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                0.0,
                "test-project",
                "test-dataset",
                "test-bucket",
                null,
                BackupConfigSource.SYSTEM,
                Timestamp.MAX_VALUE,
                "last bq uri",
                "last gcs uri"
        );

        BackupPolicy actual = BackupPolicy.fromMap(tagMap, false);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromMapFromFallbackFalse_exception()  {

        Map<String, String> tagMap = new HashMap<>();

        //3 missing fields (last_backup, last_bq, last_gcs) should throw an exception
        tagMap.put("backup_cron", "test-cron");
        tagMap.put("backup_method", "BigQuery Snapshot");
        tagMap.put("config_source", "System");
        tagMap.put("backup_time_travel_offset_days", "0");
        tagMap.put("bq_snapshot_storage_project", "test-project");
        tagMap.put("bq_snapshot_storage_dataset", "test-dataset");
        tagMap.put("bq_snapshot_expiration_days", "0.0");
        tagMap.put("gcs_snapshot_storage_location", "test-bucket");
        tagMap.put("gcs_snapshot_format", "");

        BackupPolicy expected = new BackupPolicy(
                "test-cron",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                0.0,
                "test-project",
                "test-dataset",
                "test-bucket",
                null,
                BackupConfigSource.SYSTEM,
                Timestamp.MAX_VALUE,
                "last bq uri",
                "last gcs uri"
        );

        BackupPolicy actual = BackupPolicy.fromMap(tagMap, false);

        assertEquals(expected, actual);
    }

    @Test
    public void testFromMapFromFallbackTrue() throws IOException, IllegalArgumentException {

        Map<String, String> tagMap = new HashMap<>();

        tagMap.put("backup_cron", "test-cron");
        tagMap.put("backup_method", "BigQuery Snapshot");
        tagMap.put("config_source", "System");
        tagMap.put("backup_time_travel_offset_days", "0");
        tagMap.put("bq_snapshot_storage_project", "test-project");
        tagMap.put("bq_snapshot_storage_dataset", "test-dataset");
        tagMap.put("bq_snapshot_expiration_days", "0.0");
        tagMap.put("gcs_snapshot_storage_location", "test-bucket");
        tagMap.put("gcs_snapshot_format", "");

        // this is not required when using fromFallbackConfig='True' and should have no effect
        tagMap.put("last_gcs_snapshot_storage_uri", "last gcs uri");


        BackupPolicy expected = new BackupPolicy(
                "test-cron",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                0.0,
                "test-project",
                "test-dataset",
                "test-bucket",
                null,
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                "",
                "" // should be empty string
        );

        BackupPolicy actual = BackupPolicy.fromMap(tagMap, true);

        assertEquals(expected, actual);
    }
}
