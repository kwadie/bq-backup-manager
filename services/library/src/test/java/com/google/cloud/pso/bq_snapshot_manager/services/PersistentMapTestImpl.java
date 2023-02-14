package com.google.cloud.pso.bq_snapshot_manager.services;

import com.google.cloud.pso.bq_snapshot_manager.services.map.PersistentMap;

import java.util.HashMap;
import java.util.Map;

public class PersistentMapTestImpl implements PersistentMap {

    private Map<String, String> map;

    public PersistentMapTestImpl(){
        map = new HashMap<>();
    }

    @Override
    public void put(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }
}
