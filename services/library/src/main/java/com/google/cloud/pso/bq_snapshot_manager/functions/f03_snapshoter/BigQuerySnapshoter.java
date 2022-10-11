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


import com.google.cloud.bigquery.TableId;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryService;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.FailedPubSubMessage;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubService;
import com.google.cloud.pso.bq_snapshot_manager.services.set.PersistentSet;

import java.io.IOException;
import java.util.Arrays;

public class BigQuerySnapshoter {

    private final LoggingHelper logger;

    private final Integer functionNumber;

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
        this.functionNumber = functionNumber;

        logger = new LoggingHelper(
                BigQuerySnapshoter.class.getSimpleName(),
                functionNumber,
                config.getProjectId()
        );
    }

    public void execute(BigQuerySnapshoterRequest request, String trackingId, String pubSubMessageId) throws IOException, NonRetryableApplicationException, InterruptedException {

        logger.logFunctionStart(trackingId);
        logger.logInfoWithTracker(trackingId, String.format("Request : %s", request.toString()));

        /**
         *  Check if we already processed this pubSubMessageId before to avoid submitting BQ queries
         *  in case we have unexpected errors with PubSub re-sending the message. This is an extra measure to avoid unnecessary cost.
         *  We do that by keeping simple flag files in GCS with the pubSubMessageId as file name.
         */
        String flagFileName = String.format("%s/%s", persistentSetObjectPrefix, pubSubMessageId);
        if (persistentSet.contains(flagFileName)) {
            // log error and ACK and return
            String msg = String.format("PubSub message ID '%s' has been processed before by %s. The message should be ACK to PubSub to stop retries. Please investigate further why the message was retried in the first place.",
                    pubSubMessageId,
                    this.getClass().getSimpleName()
            );
            throw new NonRetryableApplicationException(msg);
        }

        // 3. Perform the Snapshot operation using the BigQuery service

        String tableDecorator = request.getTargetTable().getTable();
        if(request.getTimeTravelOffsetMs() > 0) {
            tableDecorator = tableDecorator + "@" + request.getTimeTravelOffsetMs().toString();
        }

        TableId sourceTableId = TableId.of(
                request.getTargetTable().getProject(),
                request.getTargetTable().getDataset(),
                request.getTargetTable().getTable());

        TableId destinationTableId = TableId.of(
                request.getSnapshotStorageProjectID(),
                request.getSnapshotStorageDataset(),
                //TODO: Construct a snapshot table name that doesn't collide with other snapshots
                request.getTargetTable().getTable()
                );

        // TODO: use the table decorator to get the snapshot time travel
        bqService.createSnapshot(sourceTableId, destinationTableId, request.getSnapshotExpirationMs());
        //TODO: wait for job result and proceed based on output


        // Create a Tagger request and send it to the Tagger PubSub topic


//        // Construct and Send a request to the Tagger via PubSub
//        TaggerRequest taggerOperation = new TaggerRequest(
//                request.getTargetTable(),
//                request.getRunId(),
//                request.getTrackingId()
//        );
//
//        // Publish the list of tagging requests to PubSub
//        PubSubPublishResults publishResults = pubSubService.publishTableOperationRequests(
//                config.getProjectId(),
//                config.getOutputTopic(),
//                Arrays.asList(taggerOperation)
//        );
//        for (FailedPubSubMessage msg : publishResults.getFailedMessages()) {
//            String logMsg = String.format("Failed to publish this messages %s", msg.toString());
//            logger.logWarnWithTracker(request.getTrackingId(), logMsg);
//        }

        // Add a flag key marking that we already completed this request and no additional runs
        // are required in case PubSub is in a loop of retrying due to ACK timeout while the service has already processed the request
        // This is an extra measure to avoid unnecessary cost due to config issues.
        logger.logInfoWithTracker(trackingId, String.format("Persisting processing key for PubSub message ID %s", pubSubMessageId));
        persistentSet.add(flagFileName);

        logger.logFunctionEnd(trackingId);
    }
}
