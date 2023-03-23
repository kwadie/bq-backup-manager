package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

public class ConfiguratorRequest extends TableOperationRequestResponse {

    private boolean isForceRun;
    // reference point to apply CRON checks
    private Timestamp refTimestamp;

    public ConfiguratorRequest(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, boolean isForceRun, Timestamp refTimestamp) {
        super(targetTable, runId, trackingId, isDryRun);
        this.isForceRun = isForceRun;
        this.refTimestamp = refTimestamp;
    }

    public boolean isForceRun() {
        return isForceRun;
    }

    public Timestamp getRefTimestamp() {
        return refTimestamp;
    }

    @Override
    public String toString() {
        return "ConfiguratorRequest{" +
                "isForceRun=" + isForceRun +
                ", refTimestamp=" + refTimestamp +
                "} " + super.toString();
    }
}
