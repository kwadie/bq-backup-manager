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


<<<<<<< HEAD:services/library/src/main/java/com/google/cloud/pso/bq_snapshot_manager/functions/snapshoter/Snapshoter.java
import com.google.api.services.bigquery.model.SnapshotDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.pso.bq_snapshot_manager.entities.JsonMessage;
=======
>>>>>>> master:services/library/src/main/java/com/google/cloud/pso/bq_snapshot_manager/functions/f03_snapshoter/Snapshoter.java
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryService;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.FailedPubSubMessage;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubService;
import com.google.cloud.pso.bq_snapshot_manager.services.set.PersistentSet;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryServiceImpl;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.cloud.bigquery.*;

import java.io.IOException;
import java.util.Arrays;

public class Snapshoter {

    private final LoggingHelper logger;

    private Integer functionNumber;

    private SnapshoterConfig config;
    private BigQueryService bqService;
    private PubSubService pubSubService;
    private PersistentSet persistentSet;
    private String persistentSetObjectPrefix;


    public Snapshoter(SnapshoterConfig config,
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
                Snapshoter.class.getSimpleName(),
                functionNumber,
                config.getProjectId()
        );
    }

    public void execute(SnapshoterRequest request, String trackingId, String pubSubMessageId) throws IOException, NonRetryableApplicationException, InterruptedException {

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

        //TODO: add service logic here

        // TODO: 1. Parse snapshot policy to JSON object. There is an example from the PII solution (https://github.com/GoogleCloudPlatform/bq-pii-classifier/blob/24e711f856fadd46b4de96a8ca0c19c9bb30e0d5/services/library/src/main/java/com/google/cloud/pso/bq_pii_classifier/entities/TableScanLimitsConfig.java#L27)
        // String requestJsonString = request.toJsonString();
        // JsonElement requestJson = JsonParser.parseString(requestJsonString).getAsJsonObject();

        // TODO: 2. Determine the snapshot config for the target table from the policy
        // commenting below code as now expecting values from request.
/*        JsonElement snapshotConfigJson = JsonParser.parseString(this.config.getSnapshotPolicyJson()).getAsJsonObject();
        String snapshotStorageDataset = snapshotConfigJson.getAsJsonObject().get("snapshot_storage_dataset").getAsString();
        Integer snapshotExpirationMs = snapshotConfigJson.getAsJsonObject().get("snapshot_expiration_ms").getAsInt();
        Integer timeTravelOffsetMs = snapshotConfigJson.getAsJsonObject().get("time_travel_offset_ms").getAsInt();
*/

        // As per new implementation, I am expecting below values from Snapshoter request.
        String snapshotStorageProjectID = request.getSnapshotStorageProjectID();
        String snapshotStorageDataset = request.getSnapshotStorageDataset();
        Integer snapshotExpirationMs = request.getSnapshotExpirationMs();
        Integer timeTravelOffsetMs = request.getTimeTravelOffsetMs();

        // TODO: 3. Perform the Snapshot operation using the BigQuery service
        String[] targetTable = request.getTargetTable().split(".");
        String sourceProjectId = targetTable[0];
        String sourceDataset = targetTable[1];
        String sourceTable = targetTable[2];

        if(timeTravelOffsetMs > 0) {
            sourceTable = sourceTable + "@" + timeTravelOffsetMs.toString();
        }

/*        String[] destination = snapshotStorageDataset.split(".");
        String destinationProjectId = destination[0];
        String destinationDataset = destination[1];*/

        //not required
        //String destinationTable = destination[2];

        TableId sourceTableId = TableId.of(sourceProjectId, sourceDataset, sourceTable);
        TableId destinationTableId = TableId.of(snapshotStorageProjectID, snapshotStorageDataset); //, destinationTable);
        this.bqService.createSnapshot(sourceTableId, destinationTableId, snapshotExpirationMs);
        // wait for job result


        // TODO: 4. Create a Tagger request and send it to the Tagger PubSub topic


        // Construct and Send a request to the Tagger via PubSub
        TaggerRequest taggerOperation = new TaggerRequest(
                request.getTargetTable(),
                request.getRunId(),
                request.getTrackingId()
        );

        // Publish the list of tagging requests to PubSub
        PubSubPublishResults publishResults = pubSubService.publishTableOperationRequests(
                config.getProjectId(),
                config.getOutputTopic(),
                Arrays.asList(taggerOperation)
        );
        for (FailedPubSubMessage msg : publishResults.getFailedMessages()) {
            String logMsg = String.format("Failed to publish this messages %s", msg.toString());
            logger.logWarnWithTracker(request.getTrackingId(), logMsg);
        }

        // Add a flag key marking that we already completed this request and no additional runs
        // are required in case PubSub is in a loop of retrying due to ACK timeout while the service has already processed the request
        // This is an extra measure to avoid unnecessary cost due to config issues.
        logger.logInfoWithTracker(trackingId, String.format("Persisting processing key for PubSub message ID %s", pubSubMessageId));
        persistentSet.add(flagFileName);

        logger.logFunctionEnd(trackingId);
    }
}
