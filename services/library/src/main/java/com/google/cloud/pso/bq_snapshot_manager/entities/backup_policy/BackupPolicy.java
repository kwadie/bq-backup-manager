package com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy;

import com.google.cloud.Timestamp;
import com.google.cloud.pso.bq_snapshot_manager.entities.GCSSnapshotFormat;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;

public class BackupPolicy {

    private final String cron;
    private final BackupMethod method;
    private final TimeTravelOffsetDays timeTravelOffsetDays;
    private final Double bigQuerySnapshotExpirationDays;
    private final String bigQuerySnapshotStorageProject;
    private final String bigQuerySnapshotStorageDataset;
    private final String gcsSnapshotStorageLocation;
    private final GCSSnapshotFormat gcsExportFormat;
    private final BackupConfigSource configSource;
    private final Timestamp lastBackupAt;

    public BackupPolicy(String cron,
                        BackupMethod method,
                        TimeTravelOffsetDays timeTravelOffsetDays,
                        Double bigQuerySnapshotExpirationDays,
                        String bigQuerySnapshotStorageProject,
                        String bigQuerySnapshotStorageDataset,
                        String gcsSnapshotStorageLocation,
                        GCSSnapshotFormat gcsExportFormat,
                        BackupConfigSource configSource,
                        Timestamp lastBackupAt
                        ) {
        this.cron = cron;
        this.method = method;
        this.timeTravelOffsetDays = timeTravelOffsetDays;
        this.bigQuerySnapshotExpirationDays = bigQuerySnapshotExpirationDays;
        this.bigQuerySnapshotStorageProject = bigQuerySnapshotStorageProject;
        this.bigQuerySnapshotStorageDataset = bigQuerySnapshotStorageDataset;
        this.gcsExportFormat = gcsExportFormat;
        this.gcsSnapshotStorageLocation = gcsSnapshotStorageLocation;
        this.configSource = configSource;
        this.lastBackupAt = lastBackupAt;
    }

    public String getCron() {
        return cron;
    }

    public BackupMethod getMethod() {
        return method;
    }

    public TimeTravelOffsetDays getTimeTravelOffsetDays() {
        return timeTravelOffsetDays;
    }

    public Double getBigQuerySnapshotExpirationDays() {
        return bigQuerySnapshotExpirationDays;
    }

    public String getBigQuerySnapshotStorageProject() {
        return bigQuerySnapshotStorageProject;
    }

    public String getBigQuerySnapshotStorageDataset() {
        return bigQuerySnapshotStorageDataset;
    }

    public BackupConfigSource getConfigSource() {
        return configSource;
    }

    public Timestamp getLastBackupAt() {
        return lastBackupAt;
    }

    public String getGcsSnapshotStorageLocation() {
        return gcsSnapshotStorageLocation;
    }

    public static BackupPolicy fromJson(String jsonStr) {
        // Parse JSON as map and build the fields while applying validation
        Gson gson = new Gson();
        Map<String, String> jsonMap = gson.fromJson(jsonStr, Map.class);

        return Utils.parseBackupTagTemplateMap(jsonMap);
    }

    public GCSSnapshotFormat getGcsExportFormat() {
        return gcsExportFormat;
    }

    @Override
    public String toString() {
        return "BackupPolicy{" +
                "cron='" + cron + '\'' +
                ", method=" + method +
                ", timeTravelOffsetDays=" + timeTravelOffsetDays +
                ", bigQuerySnapshotExpirationDays=" + bigQuerySnapshotExpirationDays +
                ", bigQuerySnapshotStorageProject='" + bigQuerySnapshotStorageProject + '\'' +
                ", bigQuerySnapshotStorageDataset='" + bigQuerySnapshotStorageDataset + '\'' +
                ", gcsSnapshotStorageLocation='" + gcsSnapshotStorageLocation + '\'' +
                ", gcsExportFormat=" + gcsExportFormat +
                ", configSource=" + configSource +
                ", lastBackupAt=" + lastBackupAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BackupPolicy)) return false;
        BackupPolicy that = (BackupPolicy) o;
        return getCron().equals(that.getCron()) &&
                getMethod() == that.getMethod() &&
                getTimeTravelOffsetDays() == that.getTimeTravelOffsetDays() &&
                getBigQuerySnapshotExpirationDays().equals(that.getBigQuerySnapshotExpirationDays()) &&
                getBigQuerySnapshotStorageProject().equals(that.getBigQuerySnapshotStorageProject()) &&
                getBigQuerySnapshotStorageDataset().equals(that.getBigQuerySnapshotStorageDataset()) &&
                getGcsSnapshotStorageLocation().equals(that.getGcsSnapshotStorageLocation()) &&
                getGcsExportFormat() == that.getGcsExportFormat() &&
                getConfigSource() == that.getConfigSource() &&
                getLastBackupAt().equals(that.getLastBackupAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCron(), getMethod(), getTimeTravelOffsetDays(), getBigQuerySnapshotExpirationDays(), getBigQuerySnapshotStorageProject(), getBigQuerySnapshotStorageDataset(), getGcsSnapshotStorageLocation(), getGcsExportFormat(), getConfigSource(), getLastBackupAt());
    }
}
