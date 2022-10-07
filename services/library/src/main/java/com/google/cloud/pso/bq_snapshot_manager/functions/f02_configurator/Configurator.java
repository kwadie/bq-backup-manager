package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

import com.google.cloud.Tuple;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.FallbackBackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.services.catalog.DataCatalogService;
import com.google.cloud.pso.bq_snapshot_manager.services.set.PersistentSet;

import java.io.IOException;

public class Configurator {

    private final LoggingHelper logger;
    private final Integer functionNumber;
    private final ConfiguratorConfig config;
    private final DataCatalogService dataCatalogService;
    private final PersistentSet persistentSet;
    private final FallbackBackupPolicy fallbackBackupPolicy;
    private final String persistentSetObjectPrefix;


    public Configurator(ConfiguratorConfig config,
                        DataCatalogService dataCatalogService,
                        PersistentSet persistentSet,
                        FallbackBackupPolicy fallbackBackupPolicy,
                        String persistentSetObjectPrefix,
                        Integer functionNumber) {
        this.config = config;
        this.dataCatalogService = dataCatalogService;
        this.persistentSet = persistentSet;
        this.fallbackBackupPolicy = fallbackBackupPolicy;
        this.persistentSetObjectPrefix = persistentSetObjectPrefix;
        this.functionNumber = functionNumber;

        logger = new LoggingHelper(
                Configurator.class.getSimpleName(),
                functionNumber,
                config.getProjectId()
        );
    }

    public void execute(ConfiguratorRequest request, String pubSubMessageId) throws IOException, NonRetryableApplicationException {

        logger.logFunctionStart(request.getTrackingId());
        logger.logInfoWithTracker(request.getTrackingId(),
                String.format("Request : %s", request.toString()));

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

        // Check if the table has a back policy attached to it in data catalog
        BackupPolicy backupPolicy = dataCatalogService.getBackupPolicyTag(
                request.getTargetTable(),
                config.getBackupTagTemplateId()
        );

        if (backupPolicy != null) {
            // use table backup policy
            logger.logInfoWithTracker(request.getTrackingId(),
                    String.format("Will use attached tag template backup policy for table '%s'",
                            request.getTargetTable().toSqlString()
                    )
            );
        } else {
            // find and use default backup policy

            // find the most granular fallback policy table > dataset > project
            Tuple<String, BackupPolicy> fallbackTuple = findFallbackBackupPolicy(fallbackBackupPolicy, request.getTargetTable());

            //TODO: log this into another logger for reporting on table backup configurations
            logger.logInfoWithTracker(request.getTrackingId(),
                    String.format("Will use %s-level fallback backup policy for table %s",
                            fallbackTuple.x(), request.getTargetTable()));

            backupPolicy = fallbackTuple.y();
        }

        //TODO: log this into another logger for reporting on table backup configurations
        logger.logInfoWithTracker(request.getTrackingId(),
                String.format("Backup policy for table %s is %s",
                        request.getTargetTable().toSqlString(),
                        backupPolicy.toString()));

        // TODO: use the backup policy to call the next service

        // Add a flag key marking that we already completed this request and no additional runs
        // are required in case PubSub is in a loop of retrying due to ACK timeout while the service has already processed the request
        // This is an extra measure to avoid unnecessary cost due to config issues.
        logger.logInfoWithTracker(request.getTrackingId(), String.format("Persisting processing key for PubSub message ID %s", pubSubMessageId));
        persistentSet.add(flagFileName);

        logger.logFunctionEnd(request.getTrackingId());
    }

    public static Tuple<String, BackupPolicy> findFallbackBackupPolicy(FallbackBackupPolicy fallbackBackupPolicy,
                                                                       TableSpec tableSpec) {

        BackupPolicy tableLevel = fallbackBackupPolicy.getTableOverrides().get(tableSpec.toSqlString());
        if (tableLevel != null) {
            return Tuple.of("table", tableLevel);
        }

        BackupPolicy datasetLevel = fallbackBackupPolicy.getDatasetOverrides().get(
                String.format("%s.%s", tableSpec.getProject(), tableSpec.getDataset())
        );
        if (datasetLevel != null) {
            return Tuple.of("dataset", datasetLevel);
        }

        BackupPolicy projectLevel = fallbackBackupPolicy.getProjectOverrides().get(
                tableSpec.getProject()
        );
        if (projectLevel != null) {
            return Tuple.of("project", projectLevel);
        }

        //TODO: check for folder level by getting the folder id of a project from the API

        // else return the global default policy
        return Tuple.of("global", fallbackBackupPolicy.getDefaultPolicy());
    }
}
