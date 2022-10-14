package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.Tuple;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BigQuerySnapshoterTest {

    @Test
    public void testGetSnapshotTableSpec(){

        TableSpec actual = BigQuerySnapshoter.getSnapshotTableSpec(
                TableSpec.fromSqlString("p.d.t"),
                "sProject",
                "sDataset",
                "123",
                100L
        );

        TableSpec expected = new TableSpec("sProject", "sDataset", "p_d_t_123_100");

        assertEquals(expected, actual);
    }

    @Test
    public void testGetTableSpecWithTimeTravel(){
        Timestamp refPoint = Timestamp.parseTimestamp("2022-10-13T12:58:41Z"); // == 1665665921023L

        // test with 0 days (lower bound)
        Tuple<TableSpec, Long> actualWithZero = BigQuerySnapshoter.getTableSpecWithTimeTravel(
                TableSpec.fromSqlString("p.d.t"),
                TimeTravelOffsetDays.DAYS_0,
                refPoint
        );

        // time travel will be trimmed to seconds
        assertEquals(TableSpec.fromSqlString("p.d.t@1665665921000"), actualWithZero.x());
        assertEquals(1665665921000L, actualWithZero.y().longValue());

        // test with 7 days (upped bound)
        Tuple<TableSpec, Long> actualWith7 = BigQuerySnapshoter.getTableSpecWithTimeTravel(
                TableSpec.fromSqlString("p.d.t"),
                TimeTravelOffsetDays.DAYS_7,
                refPoint
        );

        Long expectedMs7Days = 1665665921000L - (7 * 86400000) - 5000;
        assertEquals(TableSpec.fromSqlString("p.d.t@"+expectedMs7Days.toString()), actualWith7.x());
        assertEquals(expectedMs7Days, actualWith7.y());

        // test with 5 days (with bounds)
        Tuple<TableSpec, Long> actualWith5 = BigQuerySnapshoter.getTableSpecWithTimeTravel(
                TableSpec.fromSqlString("p.d.t"),
                TimeTravelOffsetDays.DAYS_5,
                refPoint
        );

        Long expectedMs5Days = 1665665921000L - (5 * 86400000);
        assertEquals(TableSpec.fromSqlString("p.d.t@"+expectedMs5Days.toString()), actualWith5.x());
        assertEquals(expectedMs5Days, actualWith5.y());

    }
}
