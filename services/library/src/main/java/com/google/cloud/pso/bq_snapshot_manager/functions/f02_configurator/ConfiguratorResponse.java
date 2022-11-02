package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequestResponse;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter.BigQuerySnapshoterRequest;
import com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter.GCSSnapshoterRequest;
import com.google.cloud.pso.bq_snapshot_manager.services.pubsub.PubSubPublishResults;

public class ConfiguratorResponse extends TableOperationRequestResponse {

    private final BackupPolicy backupPolicy;
    private final Timestamp refTs;
    private final boolean isBackupTime;
    private final BigQuerySnapshoterRequest bigQuerySnapshoterRequest;
    private final GCSSnapshoterRequest gcsSnapshoterRequest;
    private final PubSubPublishResults bigQueryBackupPublishingResults;
    private final PubSubPublishResults gcsBackupPublishingResults;

    public ConfiguratorResponse(TableSpec targetTable, String runId, String trackingId, BackupPolicy backupPolicy, Timestamp refTs, boolean isBackupTime, BigQuerySnapshoterRequest bigQuerySnapshoterRequest, GCSSnapshoterRequest gcsSnapshoterRequest, PubSubPublishResults bigQueryBackupPublishingResults, PubSubPublishResults gcsBackupPublishingResults) {
        super(targetTable, runId, trackingId);
        this.backupPolicy = backupPolicy;
        this.refTs = refTs;
        this.isBackupTime = isBackupTime;
        this.bigQuerySnapshoterRequest = bigQuerySnapshoterRequest;
        this.gcsSnapshoterRequest = gcsSnapshoterRequest;
        this.bigQueryBackupPublishingResults = bigQueryBackupPublishingResults;
        this.gcsBackupPublishingResults = gcsBackupPublishingResults;
    }

    public BackupPolicy getBackupPolicy() {
        return backupPolicy;
    }

    public Timestamp getRefTs() {
        return refTs;
    }

    public boolean isBackupTime() {
        return isBackupTime;
    }

    public BigQuerySnapshoterRequest getBigQuerySnapshoterRequest() {
        return bigQuerySnapshoterRequest;
    }

    public GCSSnapshoterRequest getGcsSnapshoterRequest() {
        return gcsSnapshoterRequest;
    }

    public PubSubPublishResults getBigQueryBackupPublishingResults() {
        return bigQueryBackupPublishingResults;
    }

    public PubSubPublishResults getGcsBackupPublishingResults() {
        return gcsBackupPublishingResults;
    }
}
