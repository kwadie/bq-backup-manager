package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;

public class GCSSnapshoterResponse extends TableOperationRequestResponse {

    private final Timestamp operationTs;
    private final TableSpec computedSourceTable;
    private final TaggerRequest outputTaggerRequest;
    private final PubSubPublishResults pubSubPublishResults;

    public GCSSnapshoterResponse(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, Timestamp operationTs, TableSpec computedSourceTable, TaggerRequest outputTaggerRequest, PubSubPublishResults pubSubPublishResults) {
        super(targetTable, runId, trackingId, isDryRun);
        this.operationTs = operationTs;
        this.computedSourceTable = computedSourceTable;
        this.outputTaggerRequest = outputTaggerRequest;
        this.pubSubPublishResults = pubSubPublishResults;
    }

    public Timestamp getOperationTs() {
        return operationTs;
    }

    public TableSpec getComputedSourceTable() {
        return computedSourceTable;
    }

    public TaggerRequest getOutputTaggerRequest() {
        return outputTaggerRequest;
    }

    public PubSubPublishResults getPubSubPublishResults() {
        return pubSubPublishResults;
    }
}
