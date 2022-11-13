package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

public class ConfiguratorRequest extends TableOperationRequestResponse {

    private boolean isForceRun;

    public ConfiguratorRequest(TableSpec targetTable, String runId, String trackingId, boolean isDryRun, boolean isForceRun) {
        super(targetTable, runId, trackingId, isDryRun);
        this.isForceRun = isForceRun;
    }

    public boolean isForceRun() {
        return isForceRun;
    }

    @Override
    public String toString() {
        return "ConfiguratorRequest{" +
                "isForceRun=" + isForceRun +
                "} " + super.toString();
    }
}
