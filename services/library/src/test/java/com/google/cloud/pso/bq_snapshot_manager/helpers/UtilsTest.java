/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pso.bq_snapshot_manager.helpers;

import com.google.cloud.Timestamp;
import com.google.cloud.datacatalog.v1.TagField;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupConfigSource;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogServiceImpl;
import org.junit.Test;
import org.springframework.scheduling.support.CronExpression;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void testParseBackupTagTemplateMap() throws IOException, IllegalArgumentException {

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
        tagMap.put("last_backup_at", "");

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
                Timestamp.MIN_VALUE
        );

        BackupPolicy actual = Utils.parseBackupTagTemplateMap(tagMap);

        assertEquals(expected, actual);
    }


    @Test(expected = IllegalArgumentException.class)
    public void getConfigFromEnv_Required() {
        Utils.getConfigFromEnv("NA_VAR", true);
    }

    @Test
    public void getConfigFromEnv_NotRequired() {
        // should not fail because the VAR is not required
        Utils.getConfigFromEnv("NA_VAR", false);
    }
}