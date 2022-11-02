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

package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;


import com.google.cloud.Timestamp;
import com.google.cloud.Tuple;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.TimeTravelOffsetDays;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryService;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.FailedPubSubMessage;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubService;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.SuccessPubSubMessage;
import com.google.cloud.pso.bq_snapshot_manager.services.set.PersistentSet;

import java.io.IOException;
import java.util.Arrays;

public class BigQuerySnapshoter {

    private final LoggingHelper logger;

    private final SnapshoterConfig config;
    private final BigQueryService bqService;
    private final PubSubService pubSubService;
    private final PersistentSet persistentSet;
    private final String persistentSetObjectPrefix;


    public BigQuerySnapshoter(SnapshoterConfig config,
                              BigQueryService bqService,
                              PubSubService pubSubService,
                              PersistentSet persistentSet,
                              String persistentSetObjectPrefix,
                              Integer functionNumber
    ) {
        this.config = config;
        this.bqService = bqService;
        this.pubSubService = pubSubService;
        this.persistentSet = persistentSet;
        this.persistentSetObjectPrefix = persistentSetObjectPrefix;

        logger = new LoggingHelper(
                BigQuerySnapshoter.class.getSimpleName(),
                functionNumber,
                config.getProjectId()
        );
    }

    public BigQuerySnapshoterResponse execute(BigQuerySnapshoterRequest request, String pubSubMessageId) throws IOException, NonRetryableApplicationException, InterruptedException {

        // run common service start logging and checks
        Utils.runServiceStartRoutines(
                logger,
                request,
                persistentSet,
                persistentSetObjectPrefix,
                pubSubMessageId
        );

        // Perform the Snapshot operation using the BigQuery service


        // TODO: Should the operationTs be the runId?

        // Calculate expiration date starting from time of taking the snapshot
        Timestamp operationTs = Timestamp.now();

        // expiry date is calculated relative to the operation time
        Timestamp expiryTs = Timestamp.ofTimeSecondsAndNanos(
                operationTs.getSeconds() + (request.getBackupPolicy().getBigQuerySnapshotExpirationDays().longValue() * 86400L),
                0);

        // time travel is calculated relative to the operation time
        Tuple<TableSpec, Long> sourceTableWithTimeTravelTuple = getTableSpecWithTimeTravel(
                request.getTargetTable(),
                request.getBackupPolicy().getTimeTravelOffsetDays(),
                operationTs
        );

        // construct the snapshot table from the request params and calculated timetravel
        TableSpec snapshotTable = getSnapshotTableSpec(
                request.getTargetTable(),
                request.getBackupPolicy().getBigQuerySnapshotStorageProject(),
                request.getBackupPolicy().getBigQuerySnapshotStorageDataset(),
                request.getRunId(),
                sourceTableWithTimeTravelTuple.y()
        );

        Timestamp timeTravelTs = Timestamp.ofTimeSecondsAndNanos(sourceTableWithTimeTravelTuple.y()/1000, 0);
        logger.logInfoWithTracker(request.getTrackingId(),
                request.getTargetTable(),
                String.format("Will take a BQ Snapshot for '%s' to '%s' with time travel timestamp '%s' (%s days) expiring on '%s'",
                        request.getTargetTable().toSqlString(),
                        snapshotTable.toSqlString(),
                        timeTravelTs.toString(),
                        request.getBackupPolicy().getTimeTravelOffsetDays().getText(),
                        expiryTs.toString()
                )
        );

        // API Call
        bqService.createSnapshot(
                sourceTableWithTimeTravelTuple.x(),
                snapshotTable,
                expiryTs,
                request.getTrackingId()
        );

        // TODO: use a designated logger to track backup operations (audit log)
        logger.logInfoWithTracker(request.getTrackingId(),
                request.getTargetTable(),
                String.format("BigQuery snapshot completed for table %s to %s",
                        request.getTargetTable().toSqlString(),
                        snapshotTable.toSqlString()
                )
        );

        // Create a Tagger request and send it to the Tagger PubSub topic
        TaggerRequest taggerRequest = new TaggerRequest(
                request.getTargetTable(),
                request.getRunId(),
                request.getTrackingId(),
                request.getBackupPolicy(),
                BackupMethod.BIGQUERY_SNAPSHOT,
                snapshotTable,
                null,
                operationTs
        );

        // Publish the list of tagging requests to PubSub
        PubSubPublishResults publishResults = pubSubService.publishTableOperationRequests(
                config.getProjectId(),
                config.getOutputTopic(),
                Arrays.asList(taggerRequest)
        );

        for (FailedPubSubMessage msg : publishResults.getFailedMessages()) {
            String logMsg = String.format("Failed to publish this message %s", msg.toString());
            logger.logWarnWithTracker(request.getTrackingId(), request.getTargetTable(), logMsg);
        }

        for (SuccessPubSubMessage msg : publishResults.getSuccessMessages()) {
            String logMsg = String.format("Published this message %s", msg.toString());
            logger.logInfoWithTracker(request.getTrackingId(), request.getTargetTable(), logMsg);
        }

        // run common service end logging and adding pubsub message to processed list
        Utils.runServiceEndRoutines(
                logger,
                request,
                persistentSet,
                persistentSetObjectPrefix,
                pubSubMessageId
        );

        return new BigQuerySnapshoterResponse(
                request.getTargetTable(),
                request.getRunId(),
                request.getTrackingId(),
                operationTs,
                sourceTableWithTimeTravelTuple.x(),
                snapshotTable,
                taggerRequest,
                publishResults
        );
    }

    /**
     * return a Tuple (X, Y) where X is a TableSpec containing a table decorator with time travel applied and Y is the calculated
     * timestamp in milliseconds since epoch used for the decorator
     * @param table
     * @param timeTravelOffsetDays
     * @param referencePoint
     * @return
     */
    public static Tuple<TableSpec, Long> getTableSpecWithTimeTravel(TableSpec table, TimeTravelOffsetDays timeTravelOffsetDays, Timestamp referencePoint) {
        Long timeTravelMs;
        Long refPointMs = referencePoint.getSeconds()*1000;

        if (timeTravelOffsetDays.equals(TimeTravelOffsetDays.DAYS_0)) {
            // always use time travel for consistency and traceability
            timeTravelMs = refPointMs;
        }else{
            // use a buffer (milliseconds) to count for the operation time
            Long bufferMs = timeTravelOffsetDays.equals(TimeTravelOffsetDays.DAYS_7) ? 5000L : 0L;
            // milli seconds per day * number of days
            Long timeTravelOffsetMs = (86400000L * Long.parseLong(timeTravelOffsetDays.getText()));
            timeTravelMs = refPointMs - timeTravelOffsetMs - bufferMs;
        }

        return Tuple.of(new TableSpec(
                        table.getProject(),
                        table.getDataset(),
                        String.format("%s@%s", table.getTable(), timeTravelMs)),
                timeTravelMs
        );
    }

    public static TableSpec getSnapshotTableSpec(TableSpec sourceTable, String snapshotProject, String snapshotDataset, String runId, Long timeTravelMs){
        return new TableSpec(
                snapshotProject,
                snapshotDataset,
                // Construct a snapshot table name that
                // 1. doesn't collide with other snapshots from other datasets, projects or runs
                // 2. propagates the time travel used to take this snapshot (we don't use labels to avoid extra API calls)
                String.format("%s_%s_%s_%s_%s",
                        sourceTable.getProject(),
                        sourceTable.getDataset(),
                        sourceTable.getTable(),
                        runId,
                        timeTravelMs
                )
        );
    }
}
