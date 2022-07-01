package com.google.cloud.pso.bq_snapshot_manager.services.set;

public interface PersistentSet {

    void add(String key);
    boolean contains(String key);
}
