package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

public class SnapshoterRequest extends TableOperationRequest {

    //TODO: extend with required fields

    public SnapshoterRequest(TableSpec targetTable, String runId, String trackingId) {
        super(targetTable, runId, trackingId);
    }
}
