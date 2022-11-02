package com.google.cloud.pso.bq_snapshot_manager;

import com.google.cloud.Timestamp;
import com.google.cloud.datacatalog.v1.Tag;
import com.google.cloud.datacatalog.v1.TagField;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter.BigQuerySnapshoterRequest;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryServiceImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogServiceImpl;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

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
    public void test2() throws IOException {

        try{
            throw new Exception("yay");
        }catch (Exception ex){
            String str = ExceptionUtils.getStackTrace(ex);
            System.out.println(str);
        }
    }
}
