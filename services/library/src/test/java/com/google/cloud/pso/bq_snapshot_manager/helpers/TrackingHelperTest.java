package com.google.cloud.pso.bq_snapshot_manager.helpers;

import com.google.cloud.Timestamp;
import org.junit.Test;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;

public class TrackingHelperTest {

    @Test
    public void parseAsTimestamp(){
        LocalDateTime now = LocalDateTime.ofEpochSecond(1641034800, 0, ZoneOffset.UTC);
        Timestamp fromRunId = TrackingHelper.parseRunIdAsTimestamp("1641034800000-H");

        assertEquals(1641034800, fromRunId.getSeconds());
    }
}
