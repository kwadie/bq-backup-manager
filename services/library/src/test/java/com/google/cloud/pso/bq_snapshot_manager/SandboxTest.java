package com.google.cloud.pso.bq_snapshot_manager;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryServiceImpl;
import org.junit.Test;

import java.io.IOException;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class SandboxTest {
    @Test
    public void test() throws IOException, InterruptedException {

        String trackingID = TrackingHelper.generateHeartBeatRunId();
        System.out.println(trackingID);

        BigQueryServiceImpl bq = new BigQueryServiceImpl("bqsm-data-1");
        bq.createSnapshot(
                new TableSpec("bqsm-data-1", "london", "fake_data"),
                new TableSpec("bqsm-data-1", "london", "fake_data_backup_" + trackingID),
                Timestamp.parseTimestamp("2023-10-06T12:00:00Z"),
                trackingID
        );
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
