package com.google.cloud.pso.bq_snapshot_manager.services;

import com.google.api.gax.rpc.UnimplementedException;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.services.scan.ResourceScanner;
import com.google.cloud.pso.bq_snapshot_manager.services.scan.ResourceScannerImpl;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResourceScannerTestImpl implements ResourceScanner {
    @Override
    public List<String> listProjects(Long folderId) throws NonRetryableApplicationException, GeneralSecurityException, IOException {

        switch (folderId.toString()){
            case "1": return Arrays.asList("p1","p2");
            case "2": return Arrays.asList("p3","p4");
            default: return new ArrayList<>();
        }
    }

    @Override
    public List<String> listDatasets(String project) throws NonRetryableApplicationException, InterruptedException {

        switch (project){
            case "p1": return Arrays.asList("p1.d1","p1.d2");
            case "p2": return Arrays.asList("p2.d1","p2.d2");
            case "p3": return Arrays.asList("p3.d1");
            case "p4": return Arrays.asList("p4.d1");
            default: return new ArrayList<>();
        }
    }

    @Override
    public List<String> listTables(String project, String dataset) throws InterruptedException, NonRetryableApplicationException {

        String projectDataset = String.format("%s.%s", project, dataset);

        switch (projectDataset){
            case "p1.d1": return Arrays.asList("p1.d1.t1","p1.d1.t2");
            case "p1.d2": return Arrays.asList("p1.d2.t1","p1.d2.t2");
            case "p2.d1": return Arrays.asList("p2.d1.t1","p2.d1.t2");
            case "p2.d2": return Arrays.asList("p2.d2.t1","p2.d2.t2");
            case "p3.d1": return Arrays.asList("p3.d1.t1");
            case "p4.d1": return Arrays.asList("p4.d1.t1");
            default: return new ArrayList<>();
        }
    }

    @Override
    public String getParentFolderId(String project, String runId) throws IOException {
        switch(project){
            case "p1": return "500";
            case "p2": return "600";
            case "p3":
            case "p4":
                return "700";
            default: return null;
        }
    }
}
