package com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger;

import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

public class TaggerRequest extends TableOperationRequest {

    //TODO: extend with required fields

    public TaggerRequest(TableSpec targetTable, String runId, String trackingId) {
        super(targetTable, runId, trackingId);
    }
}
