package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

public class ConfiguratorRequest extends TableOperationRequestResponse {

    private boolean isForceRun;

    public ConfiguratorRequest( boolean isForceRun, TableSpec targetTable, String runId, String trackingId) {
        super(targetTable, runId, trackingId);
        this.isForceRun = isForceRun;
    }

    public boolean isForceRun() {
        return isForceRun;
    }

    @Override
    public String toString() {
        return "ConfiguratorRequest{" +
                "isForceRun=" + isForceRun() +
                ", targetTable=" + getTargetTable() +
                ", runId='" + getRunId() + '\'' +
                ", trackingId='" + getTrackingId() + '\'' +
                '}';
    }
}
