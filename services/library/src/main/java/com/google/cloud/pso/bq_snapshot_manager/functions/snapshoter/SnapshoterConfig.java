/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pso.bq_snapshot_manager.functions.snapshoter;

public class SnapshoterConfig {


    private String projectId;
    private String regionId;

    public SnapshoterConfig(String projectId, String regionId, String bqResultsDataset, String bqResultsTable, String dlpNotificationTopic, String minLikelihood, Integer maxFindings, Integer samplingMethod, String dlpInspectionTemplateId, String tableScanLimitsJsonConfig) {
        this.projectId = projectId;
        this.regionId = regionId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getRegionId() {
        return regionId;
    }


    @Override
    public String toString() {
        return "InspectorConfig{" +
                "projectId='" + projectId + '\'' +
                ", regionId='" + regionId + '\'' +
                '}';
    }
}
