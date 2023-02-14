package com.google.cloud.pso.bq_snapshot_manager.services.map;

public interface PersistentMap {

    void put(String key,  String value);
    String get(String key);
}
