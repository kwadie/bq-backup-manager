package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;

public class BigQuerySnapshoterResponse extends TableOperationRequestResponse {

    private final Timestamp operationTs;
    private final TableSpec computedSourceTable;
    private final TableSpec computedSnapshotTable;
    private final TaggerRequest outputTaggerRequest;
    private final PubSubPublishResults pubSubPublishResults;

    public BigQuerySnapshoterResponse(TableSpec targetTable, String runId, String trackingId, Timestamp operationTs, TableSpec computedSourceTable, TableSpec computedSnapshotTable, TaggerRequest outputTaggerRequest, PubSubPublishResults pubSubPublishResults) {
        super(targetTable, runId, trackingId);
        this.operationTs = operationTs;
        this.computedSourceTable = computedSourceTable;
        this.computedSnapshotTable = computedSnapshotTable;
        this.outputTaggerRequest = outputTaggerRequest;
        this.pubSubPublishResults = pubSubPublishResults;
    }

    public Timestamp getOperationTs() {
        return operationTs;
    }

    public TableSpec getComputedSourceTable() {
        return computedSourceTable;
    }

    public TableSpec getComputedSnapshotTable() {
        return computedSnapshotTable;
    }

    public TaggerRequest getOutputTaggerRequest() {
        return outputTaggerRequest;
    }

    public PubSubPublishResults getPubSubPublishResults() {
        return pubSubPublishResults;
    }
}
