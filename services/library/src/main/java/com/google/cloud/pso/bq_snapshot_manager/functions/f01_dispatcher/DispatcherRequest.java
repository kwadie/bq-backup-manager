package com.google.cloud.pso.bq_snapshot_manager.functions.f01_dispatcher;

import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
import com.google.gson.Gson;

public class DispatcherRequest extends JsonMessage {

    // take table backup in this run regardless of the cron check
    private boolean isForceRun;
    private BigQueryScope bigQueryScope;

    public DispatcherRequest() {
        isForceRun = false;
    }

    public DispatcherRequest(boolean isForceRun, BigQueryScope bigQueryScope) {
        this.isForceRun = isForceRun;
        this.bigQueryScope = bigQueryScope;
    }

    public boolean isForceRun() {
        return isForceRun;
    }

    public BigQueryScope getBigQueryScope() {
        return bigQueryScope;
    }

    @Override
    public String toString() {
        return "DispatcherRequest{" +
                "isForceRun=" + isForceRun +
                ", bigQueryScope=" + bigQueryScope +
                '}';
    }
}
