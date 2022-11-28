package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.Timestamp;
import com.google.cloud.Tuple;
import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupMethod;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.GCSSnapshotFormat;
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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;

public class GCSSnapshoter {

    private final LoggingHelper logger;

    private final SnapshoterConfig config;
    private final BigQueryService bqService;
    private final PubSubService pubSubService;
    private final PersistentSet persistentSet;
    private final String persistentSetObjectPrefix;


    public GCSSnapshoter(SnapshoterConfig config,
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
                GCSSnapshoter.class.getSimpleName(),
                functionNumber,
                config.getProjectId()
        );
    }

    public static void validateRequest(SnapshoterRequest request){
        // validate required params
        if (! (request.getBackupPolicy().getMethod().equals(BackupMethod.GCS_SNAPSHOT) ||
                request.getBackupPolicy().getMethod().equals(BackupMethod.BOTH))) {
            throw new IllegalArgumentException(String.format("BackupMethod must be GCS_SNAPSHOT or BOTH. Received %s",
                    request.getBackupPolicy().getMethod()));
        }
        if (request.getBackupPolicy().getGcsExportFormat() == null) {
            throw new IllegalArgumentException(String.format("GCSExportFormat is missing in the BackupPolicy %s",
                    request.getBackupPolicy()));
        }
        if (request.getBackupPolicy().getGcsSnapshotStorageLocation() == null) {
            throw new IllegalArgumentException(String.format("GcsSnapshotStorageLocation is missing in the BackupPolicy %s",
                    request.getBackupPolicy()));
        }
    }

    public GCSSnapshoterResponse execute(SnapshoterRequest request, Timestamp operationTs, String pubSubMessageId) throws IOException, NonRetryableApplicationException, InterruptedException {

        // run common service start logging and checks
        Utils.runServiceStartRoutines(
                logger,
                request,
                persistentSet,
                persistentSetObjectPrefix,
                pubSubMessageId
        );


        // validate required input
        validateRequest(request);

        // Perform the Snapshot operation using the BigQuery service

        // time travel is calculated relative to the operation time
        Tuple<TableSpec, Long> sourceTableWithTimeTravelTuple = Utils.getTableSpecWithTimeTravel(
                request.getTargetTable(),
                request.getBackupPolicy().getTimeTravelOffsetDays(),
                operationTs
        );

        // construct the backup folder for this run in the format project/dataset/table/trackingid/timetravelstamp
        String backupFolder = String.format("%s/%s/%s/%s/%s/%s",
                request.getTargetTable().getProject(),
                request.getTargetTable().getDataset(),
                request.getTargetTable().getTable(),
                request.getTrackingId(),
                sourceTableWithTimeTravelTuple.y(), // include the time travel millisecond for transparency
                request.getBackupPolicy().getGcsExportFormat()
                );

        String gcsDestinationUri = prepareGcsUriForMultiFileExport(
                request.getBackupPolicy().getGcsSnapshotStorageLocation(),
                backupFolder
                );


        Timestamp timeTravelTs = Timestamp.ofTimeSecondsAndNanos(sourceTableWithTimeTravelTuple.y() / 1000, 0);
        logger.logInfoWithTracker(
                request.isDryRun(),
                request.getTrackingId(),
                request.getTargetTable(),
                String.format("Will take a GCS Snapshot for '%s' to '%s' with time travel timestamp '%s' (%s days)",
                        request.getTargetTable().toSqlString(),
                        gcsDestinationUri,
                        timeTravelTs,
                        request.getBackupPolicy().getTimeTravelOffsetDays().getText()
                )
        );

        if(!request.isDryRun()){
            // API Call
            bqService.exportToGCS(
                    sourceTableWithTimeTravelTuple.x(),
                    gcsDestinationUri,
                    request.getBackupPolicy().getGcsExportFormat(),
                    null,
                    null,
                    null,
                    request.getTrackingId()
            );
        }


        logger.logInfoWithTracker(
                request.isDryRun(),
                request.getTrackingId(),
                request.getTargetTable(),
                String.format("BigQuery GCS export completed for table %s to %s",
                        request.getTargetTable().toSqlString(),
                        gcsDestinationUri
                )
        );

        // Create a Tagger request and send it to the Tagger PubSub topic
        TaggerRequest taggerRequest = new TaggerRequest(
                request.getTargetTable(),
                request.getRunId(),
                request.getTrackingId(),
                request.isDryRun(),
                request.getBackupPolicy(),
                BackupMethod.GCS_SNAPSHOT,
                null,
                gcsDestinationUri,
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
            logger.logWarnWithTracker(request.isDryRun(),request.getTrackingId(), request.getTargetTable(), logMsg);
        }

        for (SuccessPubSubMessage msg : publishResults.getSuccessMessages()) {
            String logMsg = String.format("Published this message %s", msg.toString());
            logger.logInfoWithTracker(request.isDryRun(),request.getTrackingId(), request.getTargetTable(), logMsg);
        }

        // run common service end logging and adding pubsub message to processed list
        Utils.runServiceEndRoutines(
                logger,
                request,
                persistentSet,
                persistentSetObjectPrefix,
                pubSubMessageId
        );

        return new GCSSnapshoterResponse(
                request.getTargetTable(),
                request.getRunId(),
                request.getTrackingId(),
                request.isDryRun(),
                operationTs,
                sourceTableWithTimeTravelTuple.x(),
                taggerRequest,
                publishResults
        );
    }


    public static String prepareGcsUriForMultiFileExport(String gcsUri, String folderName) {
        // when exporting multiple files the uri should be gs://path/*
        String cleanUri = Utils.trimSlashes(gcsUri);
        String cleanFolder = Utils.trimSlashes(folderName);

        return String.format("%s/%s/*", cleanUri, cleanFolder);
    }


}
