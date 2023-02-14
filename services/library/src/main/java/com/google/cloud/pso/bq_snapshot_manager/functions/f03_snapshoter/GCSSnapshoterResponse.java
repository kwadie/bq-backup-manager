package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

public class GCSSnapshoterResponse extends TableOperationRequestResponse {

    private final Timestamp operationTs;
    private final TableSpec computedSourceTable;

    public GCSSnapshoterResponse(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, Timestamp operationTs, TableSpec computedSourceTable) {
        super(targetTable, runId, trackingId, isDryRun);
        this.operationTs = operationTs;
        this.computedSourceTable = computedSourceTable;
    }

    public Timestamp getOperationTs() {
        return operationTs;
    }

    public TableSpec getComputedSourceTable() {
        return computedSourceTable;
    }

}
