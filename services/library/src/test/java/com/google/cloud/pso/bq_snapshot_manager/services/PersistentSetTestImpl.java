package com.google.cloud.pso.bq_snapshot_manager.services;

import com.google.cloud.pso.bq_snapshot_manager.services.set.PersistentSet;

import java.util.HashSet;
import java.util.Set;

public class PersistentSetTestImpl implements PersistentSet {

    Set<String> set;

    public PersistentSetTestImpl(){
        set = new HashSet<>();
    }

    @Override
    public void add(String key) {
        set.add(key);
    }

    @Override
    public boolean contains(String key) {
        return set.contains(key);
    }
}
