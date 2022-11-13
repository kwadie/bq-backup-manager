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
package com.google.cloud.pso.bq_snapshot_manager.tagger;

import com.google.cloud.Tuple;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.PubSubEvent;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.Tagger;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerResponse;
import com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger.TaggerRequest;
import com.google.cloud.pso.bq_snapshot_manager.helpers.ControllerExceptionHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.TrackingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogServiceImpl;
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
public class TaggerController {

    private final LoggingHelper logger;
    private static final Integer functionNumber = 4;
    private Gson gson;
    Environment environment;

    public TaggerController() {

        gson = new Gson();
        environment = new Environment();
        logger = new LoggingHelper(
                TaggerController.class.getSimpleName(),
                functionNumber,
                environment.getProjectId()
        );
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity receiveMessage(@RequestBody PubSubEvent requestBody) {

        String trackingId = TrackingHelper.MIN_RUN_ID;
        DataCatalogServiceImpl dataCatalogService = null;

        // These values will be updated based on the execution flow and logged at the end
        ResponseEntity responseEntity;
        TaggerRequest taggerRequest = null;
        TaggerResponse taggerResponse = null;
        boolean isSuccess;
        Exception error = null;
        boolean isRetryableError= false;

        try {

            if (requestBody == null || requestBody.getMessage() == null) {
                String msg = "Bad Request: invalid message format";
                logger.logSevereWithTracker(trackingId, null, msg);
                throw new NonRetryableApplicationException("Request body or message is Null.");
            }

            String requestJsonString = requestBody.getMessage().dataToUtf8String();

            // remove any escape characters (e.g. from Terraform
            requestJsonString = requestJsonString.replace("\\", "");

            logger.logInfoWithTracker(trackingId, null, String.format("Received payload: %s", requestJsonString));

            taggerRequest = gson.fromJson(requestJsonString, TaggerRequest.class);

            trackingId = taggerRequest.getTrackingId();

            logger.logInfoWithTracker(taggerRequest.isDryRun(), trackingId, taggerRequest.getTargetTable(), String.format("Parsed Request: %s", taggerRequest.toString()));

            dataCatalogService = new DataCatalogServiceImpl();
            Tagger tagger = new Tagger(
                    new LoggingHelper(Tagger.class.getSimpleName(), functionNumber, environment.getProjectId()),
                    environment.toConfig(),
                    dataCatalogService,
                    new GCSPersistentSetImpl(environment.getGcsFlagsBucket()),
                    "tagger-flags"
            );

            taggerResponse = tagger.execute(
                    taggerRequest,
                    requestBody.getMessage().getMessageId()
            );

            responseEntity = new ResponseEntity("Process completed successfully.", HttpStatus.OK);
            isSuccess = true;
        } catch (Exception e) {

            Tuple<ResponseEntity, Boolean> handlingResults  = ControllerExceptionHelper.handleException(e,
                    logger,
                    trackingId,
                    taggerRequest == null? null: taggerRequest.getTargetTable()
                    );
            isSuccess = false;
            responseEntity = handlingResults.x();
            isRetryableError = handlingResults.y();
            error = e;

        }finally {
            if(dataCatalogService != null){
                dataCatalogService.shutdown();
            }
        }

        logger.logUnified(
                taggerRequest == null? null: taggerRequest.isDryRun(),
                functionNumber.toString(),
                taggerRequest == null? null: taggerRequest.getRunId(),
                taggerRequest == null? null: taggerRequest.getTrackingId(),
                taggerRequest == null? null : taggerRequest.getTargetTable(),
                taggerRequest,
                taggerResponse,
                isSuccess,
                error,
                isRetryableError
        );

        return responseEntity;
    }

    public static void main(String[] args) {
        SpringApplication.run(TaggerController.class, args);
    }
}
