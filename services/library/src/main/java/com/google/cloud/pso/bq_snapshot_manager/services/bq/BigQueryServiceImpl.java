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

package com.google.cloud.pso.bq_snapshot_manager.services.bq;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.bigquery.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.Globals;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BigQueryServiceImpl implements BigQueryService {

    private BigQuery bqAPIWrapper;

    public BigQueryServiceImpl(String projectId) throws IOException {
        bqAPIWrapper = BigQueryOptions
                .newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }


    public void createSnapshot(TableSpec sourceTable, TableSpec destinationTable, Timestamp snapshotExpirationTs, String trackingId) throws InterruptedException {
        CopyJobConfiguration copyJobConfiguration = CopyJobConfiguration
                .newBuilder(destinationTable.toTableId(), sourceTable.toTableId())
                .setWriteDisposition(JobInfo.WriteDisposition.WRITE_EMPTY)
                .setOperationType("SNAPSHOT")
                .setDestinationExpirationTime(snapshotExpirationTs.toString())
                .build();

        Job job = bqAPIWrapper.create(JobInfo
                .newBuilder(copyJobConfiguration)
                .setJobId(JobId.of(String.format("%s_%s", Globals.APPLICATION_NAME, trackingId)))
                .build());

        // wait for the job to complete
        job = job.waitFor();

        // if job finished with errors
        if (job.getStatus().getError() != null) {
            throw new RuntimeException(job.getStatus().getError().toString());
        }
    }
}