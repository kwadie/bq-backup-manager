package com.google.cloud.pso.bq_snapshot_manager.functions.f01_dispatcher;

import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
import com.google.gson.Gson;

public class DispatcherRequest extends JsonMessage {

    // take table backup in this run regardless of the cron check
    private boolean isForceRun;

    // no backup or tagging operations, only logging
    private boolean isDryRun;
    private BigQueryScope bigQueryScope;

    public DispatcherRequest(BigQueryScope bigQueryScope, boolean isForceRun, boolean isDryRun) {
        this.isForceRun = isForceRun;
        this.isDryRun = isDryRun;
        this.bigQueryScope = bigQueryScope;
    }

    public boolean isForceRun() {
        return isForceRun;
    }

    public boolean isDryRun() {
        return isDryRun;
    }

    public BigQueryScope getBigQueryScope() {
        return bigQueryScope;
    }

    @Override
    public String toString() {
        return "DispatcherRequest{" +
                "isForceRun=" + isForceRun +
                "isDryRun=" + isDryRun +
                ", bigQueryScope=" + bigQueryScope +
                '}';
    }
}
