package com.google.cloud.pso.bq_snapshot_manager.entities;

public class TaggerOperation extends Operation{

    private long snapshotTimeStamp;
    //TODO: add required fields


    public TaggerOperation(String entityKey, String runId, String trackingId, long snapshotTimeStamp) {
        super(entityKey, runId, trackingId);
        this.snapshotTimeStamp = snapshotTimeStamp;
    }

    public long getSnapshotTimeStamp() {
        return snapshotTimeStamp;
    }
}
