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
package com.google.cloud.pso.bq_snapshot_manager.snapshoter_bq;

import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.PubSubEvent;
import com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter.BigQuerySnapshoter;
import com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter.BigQuerySnapshoterRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.ControllerExceptionHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.bq.BigQueryServiceImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubServiceImpl;
import com.google.cloud.pso.bq_snapshot_manager.services.set.GCSPersistentSetImpl;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.google.cloud.pso.bq_snapshot_manager")
@RestController
public class BigQuerySnapshoterController {

    private final LoggingHelper logger;

    private static final Integer functionNumber = 3;

    private Gson gson;
    Environment environment;

    public BigQuerySnapshoterController() {

        gson = new Gson();
        environment = new Environment();
        logger = new LoggingHelper(
                BigQuerySnapshoterController.class.getSimpleName(),
                functionNumber,
                environment.getProjectId()
        );
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity receiveMessage(@RequestBody PubSubEvent requestBody) {

        String trackingId = TrackingHelper.MIN_RUN_ID;

        try {

            if (requestBody == null || requestBody.getMessage() == null) {
                String msg = "Bad Request: invalid message format";
                logger.logSevereWithTracker(trackingId, msg);
                throw new NonRetryableApplicationException("Request body or message is Null.");
            }

            String requestJsonString = requestBody.getMessage().dataToUtf8String();

            // remove any escape characters (e.g. from Terraform
            requestJsonString = requestJsonString.replace("\\", "");

            logger.logInfoWithTracker(trackingId, String.format("Received payload: %s", requestJsonString));

            BigQuerySnapshoterRequest request = gson.fromJson(requestJsonString, BigQuerySnapshoterRequest.class);

            trackingId = request.getTrackingId();

            logger.logInfoWithTracker(trackingId, String.format("Parsed Request: %s", request.toString()));

            BigQuerySnapshoter snapshoter = new BigQuerySnapshoter(
                    environment.toConfig(),
                    // run BQ snapshot jobs on the backup project
                    new BigQueryServiceImpl(request.getBackupPolicy().getBigQuerySnapshotStorageProject()),
                    new PubSubServiceImpl(),
                    new GCSPersistentSetImpl(environment.getGcsFlagsBucket()),
                    "snapshoter-bq-flags",
                    functionNumber);

            snapshoter.execute(request, requestBody.getMessage().getMessageId());

            return new ResponseEntity("Process completed successfully.", HttpStatus.OK);

        } catch (Exception e) {
            return ControllerExceptionHelper.handleException(e, logger, trackingId);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BigQuerySnapshoterController.class, args);
    }
}
