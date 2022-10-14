package com.google.cloud.pso.bq_snapshot_manager;

import com.google.cloud.Timestamp;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryServiceImpl;
import org.junit.Test;

import java.io.IOException;

public class SandboxTest {
    @Test
    public void test() throws IOException, InterruptedException {

        String trackingID = TrackingHelper.generateHeartBeatRunId();
        System.out.println(trackingID);

        BigQueryServiceImpl bq = new BigQueryServiceImpl("bqsm-data-1");
        bq.createSnapshot(
                new TableSpec("bqsm-data-1","london","fake_data"),
                new TableSpec("bqsm-data-1","london","fake_data_backup_"+ trackingID),
                Timestamp.parseTimestamp("2023-10-06T12:00:00Z"),
                trackingID
        );
    }

    @Test
    public void test2(){
//        Long currct = System.currentTimeMillis();
//        System.out.println(Timestamp.ofTimeSecondsAndNanos(currct/1000, 0)); // UTC
//        System.out.println(currct);
//        System.out.println(System.currentTimeMillis() - 86400000L);


        Long refPointMs = 1665665921023L;
        Long seconds = refPointMs / 1000;
        Long nano = refPointMs - seconds;
        Long x = seconds + nano;

        System.out.println(refPointMs);
        System.out.println(x);
    }
}
