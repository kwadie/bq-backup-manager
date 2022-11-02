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

package com.google.cloud.pso.bq_snapshot_manager.functions.f04_tagger;

import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogService;
import com.google.cloud.pso.bq_snapshot_manager.services.set.PersistentSet;

public class Tagger {

    private final LoggingHelper logger;

    private final TaggerConfig config;
    private final DataCatalogService dataCatalogService;
    private final PersistentSet persistentSet;
    private final String persistentSetObjectPrefix;

    public Tagger(LoggingHelper logger, TaggerConfig config, DataCatalogService dataCatalogService, PersistentSet persistentSet, String persistentSetObjectPrefix) {
        this.logger = logger;
        this.config = config;
        this.dataCatalogService = dataCatalogService;
        this.persistentSet = persistentSet;
        this.persistentSetObjectPrefix = persistentSetObjectPrefix;
    }

    /**
     *
     * @param request
     * @param pubSubMessageId
     * @return The backup policy attached to the target table
     * @throws NonRetryableApplicationException
     */
    public TaggerResponse execute(
            TaggerRequest request,
            String pubSubMessageId
    ) throws NonRetryableApplicationException {

        // run common service start logging and checks
        Utils.runServiceStartRoutines(
                logger,
                request,
                persistentSet,
                persistentSetObjectPrefix,
                pubSubMessageId
        );

        logger.logInfoWithTracker(
                request.getTrackingId(),
                request.getTargetTable(),
                String.format("Will process %s", request)
        );

        // prepare the backup policy tag
        BackupPolicy backupPolicy = request.getBackupPolicy();

        // set the last_xyz fields
        backupPolicy.setLastBackupAt(request.getLastBackUpAt());

        if(request.getAppliedBackupMethod().equals(BackupMethod.BIGQUERY_SNAPSHOT)){
            backupPolicy.setLastBqSnapshotStorageUri(request.getBigQuerySnapshotTableSpec().toResourceUrl());
        }

        if(request.getAppliedBackupMethod().equals(BackupMethod.GCS_SNAPSHOT)){
            backupPolicy.setLastGcsSnapshotStorageUri(request.getGcsSnapshotUri());
        }

        // update the tag
        // API Calls
        dataCatalogService.createOrUpdateBackupPolicyTag(
                request.getTargetTable(),
                backupPolicy,
                config.getTagTemplateId()
        );

        // run common service end logging and adding pubsub message to processed list
        Utils.runServiceEndRoutines(
                logger,
                request,
                persistentSet,
                persistentSetObjectPrefix,
                pubSubMessageId
        );

        return new TaggerResponse(
                request.getTargetTable(),
                request.getRunId(),
                request.getTrackingId(),
                backupPolicy
        );
    }
}