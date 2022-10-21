package com.google.cloud.pso.bq_snapshot_manager.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class FallbackBackupPolicyTest {

    @Test
    public void testParsing() throws JsonProcessingException {

        String jsonPolicyStr = "{\n" +
                "  \"default_policy\": {\n" +
                "    \"backup_cron\": \"*****\",\n" +
                "    \"backup_method\": \"BigQuery Snapshot\",\n" +
                "    \"backup_time_travel_offset_days\": \"0\",\n" +
                "    \"bq_snapshot_expiration_days\": \"15\",\n" +
                "    \"bq_snapshot_storage_project\": \"project\",\n" +
                "    \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
                "    \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
                "    \"gcs_snapshot_format\": \"\",\n" +
                "    \"config_source\": \"SYSTEM\",\n" +
                "    \"last_backup_at\": \"\"\n" +
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
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
                "    },\n" +
                "    \"folder2\": {\n" +
                "      \"backup_cron\": \"*****\",\n" +
                "      \"backup_method\": \"BigQuery Snapshot\",\n" +
                "      \"backup_time_travel_offset_days\": \"0\",\n" +
                "      \"bq_snapshot_expiration_days\": \"15\",\n" +
                "      \"bq_snapshot_storage_project\": \"project\",\n" +
                "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
                "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
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
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
                "    },\n" +
                "    \"project2\": {\n" +
                "      \"backup_cron\": \"*****\",\n" +
                "      \"backup_method\": \"BigQuery Snapshot\",\n" +
                "      \"backup_time_travel_offset_days\": \"0\",\n" +
                "      \"bq_snapshot_expiration_days\": \"15\",\n" +
                "      \"bq_snapshot_storage_project\": \"project\",\n" +
                "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
                "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
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
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
                "    },\n" +
                "    \"dataset2\": {\n" +
                "      \"backup_cron\": \"*****\",\n" +
                "      \"backup_method\": \"BigQuery Snapshot\",\n" +
                "      \"backup_time_travel_offset_days\": \"0\",\n" +
                "      \"bq_snapshot_expiration_days\": \"15\",\n" +
                "      \"bq_snapshot_storage_project\": \"project\",\n" +
                "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
                "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
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
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
                "    },\n" +
                "    \"table2\": {\n" +
                "      \"backup_cron\": \"*****\",\n" +
                "      \"backup_method\": \"BigQuery Snapshot\",\n" +
                "      \"backup_time_travel_offset_days\": \"0\",\n" +
                "      \"bq_snapshot_expiration_days\": \"15\",\n" +
                "      \"bq_snapshot_storage_project\": \"project\",\n" +
                "      \"bq_snapshot_storage_dataset\": \"dataset\",\n" +
                "      \"gcs_snapshot_storage_location\": \"gs://bla/\",\n" +
                "      \"gcs_snapshot_format\": \"\",\n" +
                "      \"config_source\": \"SYSTEM\",\n" +
                "      \"last_backup_at\": \"\"\n" +
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
                null,
                BackupConfigSource.SYSTEM,
                Timestamp.MIN_VALUE,
                "",
                ""
        );

        FallbackBackupPolicy expected = new FallbackBackupPolicy(
                testPolicy,
                Stream.of(
                        new AbstractMap.SimpleEntry<>("folder1", testPolicy),
                        new AbstractMap.SimpleEntry<>("folder2", testPolicy))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                Stream.of(
                        new AbstractMap.SimpleEntry<>("project1", testPolicy),
                        new AbstractMap.SimpleEntry<>("project2", testPolicy))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                Stream.of(
                        new AbstractMap.SimpleEntry<>("dataset1", testPolicy),
                        new AbstractMap.SimpleEntry<>("dataset2", testPolicy))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                Stream.of(
                        new AbstractMap.SimpleEntry<>("table1", testPolicy),
                        new AbstractMap.SimpleEntry<>("table2", testPolicy))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        FallbackBackupPolicy actual = FallbackBackupPolicy.fromJson(jsonPolicyStr);

        assertEquals(expected, actual);
    }
}
