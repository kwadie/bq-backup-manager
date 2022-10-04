package com.google.cloud.pso.bq_snapshot_manager.entities;

import com.google.gson.Gson;

public class JsonMessage {

    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
