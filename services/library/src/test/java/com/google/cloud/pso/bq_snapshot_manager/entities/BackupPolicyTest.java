package com.google.cloud.pso.bq_snapshot_manager.entities;

import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupConfigSource;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import org.junit.Test;

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
                "    \"config_source\": \"SYSTEM\"\n" +
                "  }";

        BackupPolicy expected = new BackupPolicy(
                "*****",
                BackupMethod.BIGQUERY_SNAPSHOT,
                TimeTravelOffsetDays.DAYS_0,
                15.0,
                "project",
                "dataset",
                "gs://bla/",
                BackupConfigSource.SYSTEM
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
}
