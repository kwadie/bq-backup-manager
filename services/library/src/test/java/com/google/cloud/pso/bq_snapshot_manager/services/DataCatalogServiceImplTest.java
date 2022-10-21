package com.google.cloud.pso.bq_snapshot_manager.services;

import com.google.cloud.Timestamp;
import com.google.cloud.datacatalog.v1.TagField;
import com.google.cloud.pso.bq_snapshot_manager.entities.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupConfigSource;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogServiceImpl;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class DataCatalogServiceImplTest {

    @Test
    public void test() throws IOException, IllegalArgumentException {

        DataCatalogServiceImpl s = new DataCatalogServiceImpl();
        TableSpec tableSpec = new TableSpec("bqsm-data-1", "london", "fake_data");

//        Map<String, TagField> out = s.getTagTemplate(tableSpec, "projects/bqsm-host/locations/eu/tagTemplates/bq_backup_manager_template");
//        System.out.println(out);

        BackupPolicy tag = s.getBackupPolicyTag(tableSpec, "projects/bqsm-host/locations/eu/tagTemplates/bq_backup_manager_template");
        System.out.println(tag);
    }

    @Test
    public void testConvertTagFieldMapToStrMap() {

        Map<String, TagField> tagMap = new HashMap<>();

        tagMap.put("backup_cron",
                TagField.newBuilder()
                        .setStringValue("test-cron")
                        .build()
        );
        tagMap.put("backup_method",
                TagField.newBuilder()
                        .setEnumValue(TagField.EnumValue.newBuilder()
                                .setDisplayName("BigQuery Snapshot")
                                .build())
                        .build()
        );
        tagMap.put("config_source",
                TagField.newBuilder()
                        .setEnumValue(TagField.EnumValue.newBuilder()
                                .setDisplayName("System")
                                .build())
                        .build()
        );
        tagMap.put("backup_time_travel_offset_days",
                TagField.newBuilder()
                        .setEnumValue(TagField.EnumValue.newBuilder()
                                .setDisplayName("0")
                                .build())
                        .build()
        );
        tagMap.put("bq_snapshot_storage_project",
                TagField.newBuilder()
                        .setStringValue("test-project")
                        .build()
        );
        tagMap.put("bq_snapshot_storage_dataset",
                TagField.newBuilder()
                        .setStringValue("test-dataset")
                        .build()
        );
        tagMap.put("bq_snapshot_expiration_days",
                TagField.newBuilder()
                        .setDoubleValue(0)
                        .build()
        );
        tagMap.put("gcs_snapshot_storage_location",
                TagField.newBuilder()
                        .setStringValue("test-bucket")
                        .build()
        );

        Map<String, String> expected = new HashMap<>();
        expected.put("backup_cron","test-cron");
        expected.put("backup_method","BigQuery Snapshot");
        expected.put("config_source","System");
        expected.put("backup_time_travel_offset_days","0");
        expected.put("bq_snapshot_storage_project","test-project");
        expected.put("bq_snapshot_storage_dataset","test-dataset");
        expected.put("bq_snapshot_expiration_days","0.0");
        expected.put("gcs_snapshot_storage_location","test-bucket");

        Map<String, String> actual = DataCatalogServiceImpl.convertTagFieldMapToStrMap(tagMap);

        assertEquals(expected, actual);
    }


}
