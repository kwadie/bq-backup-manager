package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

import com.google.cloud.Tuple;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ConfiguratorTest {

    BackupPolicy testPolicy = new BackupPolicy(
            "*****",
            BackupMethod.BIGQUERY_SNAPSHOT,
            TimeTravelOffsetDays.DAYS_0,
            15.0,
            "project",
            "dataset",
            "gs://bla/",
            BackupConfigSource.SYSTEM
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
}
