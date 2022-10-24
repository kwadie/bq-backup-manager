package com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy;

import com.google.cloud.Timestamp;
import com.google.cloud.datacatalog.v1.Tag;
import com.google.cloud.datacatalog.v1.TagField;
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
    private Timestamp lastBackupAt;
    private String lastBqSnapshotStorageUri;
    private String lastGcsSnapshotStorageUri;

    public BackupPolicy(String cron, BackupMethod method, TimeTravelOffsetDays timeTravelOffsetDays, Double bigQuerySnapshotExpirationDays, String bigQuerySnapshotStorageProject, String bigQuerySnapshotStorageDataset, String gcsSnapshotStorageLocation, GCSSnapshotFormat gcsExportFormat, BackupConfigSource configSource, Timestamp lastBackupAt, String lastBqSnapshotStorageUri, String lastGcsSnapshotStorageUri) {
        this.cron = cron;
        this.method = method;
        this.timeTravelOffsetDays = timeTravelOffsetDays;
        this.bigQuerySnapshotExpirationDays = bigQuerySnapshotExpirationDays;
        this.bigQuerySnapshotStorageProject = bigQuerySnapshotStorageProject;
        this.bigQuerySnapshotStorageDataset = bigQuerySnapshotStorageDataset;
        this.gcsSnapshotStorageLocation = gcsSnapshotStorageLocation;
        this.gcsExportFormat = gcsExportFormat;
        this.configSource = configSource;
        this.lastBackupAt = lastBackupAt;
        this.lastBqSnapshotStorageUri = lastBqSnapshotStorageUri;
        this.lastGcsSnapshotStorageUri = lastGcsSnapshotStorageUri;
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

    public String getGcsSnapshotStorageLocation() {
        return gcsSnapshotStorageLocation;
    }

    public GCSSnapshotFormat getGcsExportFormat() {
        return gcsExportFormat;
    }

    public BackupConfigSource getConfigSource() {
        return configSource;
    }

    public Timestamp getLastBackupAt() {
        return lastBackupAt;
    }

    public String getLastBqSnapshotStorageUri() {
        return lastBqSnapshotStorageUri;
    }

    public String getLastGcsSnapshotStorageUri() {
        return lastGcsSnapshotStorageUri;
    }

    public void setLastBackupAt(Timestamp lastBackupAt) {
        this.lastBackupAt = lastBackupAt;
    }

    public void setLastBqSnapshotStorageUri(String lastBqSnapshotStorageUri) {
        this.lastBqSnapshotStorageUri = lastBqSnapshotStorageUri;
    }

    public void setLastGcsSnapshotStorageUri(String lastGcsSnapshotStorageUri) {
        this.lastGcsSnapshotStorageUri = lastGcsSnapshotStorageUri;
    }

    public static BackupPolicy fromJson(String jsonStr) {
        // Parse JSON as map and build the fields while applying validation
        Gson gson = new Gson();
        Map<String, String> jsonMap = gson.fromJson(jsonStr, Map.class);

        return fromMap(jsonMap, true);
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
                ", lastBqSnapshotStorageUri='" + lastBqSnapshotStorageUri + '\'' +
                ", lastGcsSnapshotStorageUri='" + lastGcsSnapshotStorageUri + '\'' +
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
                getLastBackupAt().equals(that.getLastBackupAt()) &&
                getLastBqSnapshotStorageUri().equals(that.getLastBqSnapshotStorageUri()) &&
                getLastGcsSnapshotStorageUri().equals(that.getLastGcsSnapshotStorageUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCron(), getMethod(), getTimeTravelOffsetDays(), getBigQuerySnapshotExpirationDays(), getBigQuerySnapshotStorageProject(), getBigQuerySnapshotStorageDataset(), getGcsSnapshotStorageLocation(), getGcsExportFormat(), getConfigSource(), getLastBackupAt(), getLastBqSnapshotStorageUri(), getLastGcsSnapshotStorageUri());
    }

    /**
     * tagTemplateId is required.
     * tagName is optional. It's used to update an existing Tag on DataCatalog.
     * @param tagTemplateId
     * @param tagName
     * @return
     */
    public Tag toDataCatalogTag(String tagTemplateId, String tagName) {

        TagField cronField =
                TagField.newBuilder().setStringValue(cron).build();

        TagField methodField =
                TagField.newBuilder().setEnumValue(
                        TagField.EnumValue.newBuilder().setDisplayName(
                                method.getText())
                                .build())
                        .build();

        TagField timeTravelOffsetDaysField =
                TagField.newBuilder().setEnumValue(
                        TagField.EnumValue.newBuilder().setDisplayName(
                                timeTravelOffsetDays.getText())
                                .build())
                        .build();

        TagField bigQuerySnapshotExpirationDaysField =
                TagField.newBuilder().setDoubleValue(bigQuerySnapshotExpirationDays).build();

        TagField bigQuerySnapshotStorageProjectField =
                TagField.newBuilder().setStringValue(bigQuerySnapshotStorageProject).build();

        TagField bigQuerySnapshotStorageDatasetField =
                TagField.newBuilder().setStringValue(bigQuerySnapshotStorageDataset).build();

        TagField gcsSnapshotStorageLocationField =
                TagField.newBuilder().setStringValue(gcsSnapshotStorageLocation).build();

        TagField gcsExportFormatField =
                TagField.newBuilder().setEnumValue(
                        TagField.EnumValue.newBuilder().setDisplayName(
                                gcsExportFormat.toString())
                                .build())
                        .build();

        TagField configSourceField =
                TagField.newBuilder().setEnumValue(
                        TagField.EnumValue.newBuilder().setDisplayName(
                                configSource.toString())
                                .build())
                        .build();

        TagField lastBackupAtField =
                TagField.newBuilder().setTimestampValue(
                        com.google.protobuf.Timestamp.newBuilder().setSeconds(lastBackupAt.getSeconds()).build()
                ).build();

        TagField lastBqSnapshotUriField =
                TagField.newBuilder().setStringValue(lastBqSnapshotStorageUri).build();

        TagField lastGcsSnapshotUriField =
                TagField.newBuilder().setStringValue(lastGcsSnapshotStorageUri).build();


        Tag.Builder tagBuilder =  Tag.newBuilder()
                .setTemplate(tagTemplateId)
                .putFields(DataCatalogBackupPolicyTagFields.backup_cron.toString(), cronField)
                .putFields(DataCatalogBackupPolicyTagFields.backup_method.toString(), methodField)
                .putFields(DataCatalogBackupPolicyTagFields.backup_time_travel_offset_days.toString(), timeTravelOffsetDaysField)
                .putFields(DataCatalogBackupPolicyTagFields.bq_snapshot_expiration_days.toString(), bigQuerySnapshotExpirationDaysField)
                .putFields(DataCatalogBackupPolicyTagFields.bq_snapshot_storage_project.toString(), bigQuerySnapshotStorageProjectField)
                .putFields(DataCatalogBackupPolicyTagFields.bq_snapshot_storage_dataset.toString(), bigQuerySnapshotStorageDatasetField)
                .putFields(DataCatalogBackupPolicyTagFields.gcs_snapshot_storage_location.toString(), gcsSnapshotStorageLocationField)
                .putFields(DataCatalogBackupPolicyTagFields.gcs_snapshot_format.toString(), gcsExportFormatField)
                .putFields(DataCatalogBackupPolicyTagFields.config_source.toString(), configSourceField)
                .putFields(DataCatalogBackupPolicyTagFields.last_backup_at.toString(), lastBackupAtField)
                .putFields(DataCatalogBackupPolicyTagFields.last_bq_snapshot_storage_uri.toString(), lastBqSnapshotUriField)
                .putFields(DataCatalogBackupPolicyTagFields.last_gcs_snapshot_storage_uri.toString(), lastGcsSnapshotUriField);

        if(tagName != null){
            tagBuilder.setName(tagName);
        }

        return tagBuilder.build();
    }

    public static BackupPolicy fromMap(Map<String, String> tagTemplate, boolean fromFallbackConfig) throws IllegalArgumentException {

        // parse common settings
        String cron = Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_cron.toString());

        BackupMethod method = BackupMethod.fromString(
                Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_method.toString())
        );

        TimeTravelOffsetDays timeTravelOffsetDays = TimeTravelOffsetDays.fromString(
                Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_time_travel_offset_days.toString())
        );

        // parse BQ snapshot settings
        String bqSnapshotStorageProject = Utils.getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.bq_snapshot_storage_project.toString());
        String bqSnapshotStorageDataset = Utils.getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.bq_snapshot_storage_dataset.toString());
        String bqSnapshotExpirationDays = Utils.getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.bq_snapshot_expiration_days.toString());

        // parse GCS snapshot settings
        String gcsSnapshotStorageLocation = Utils.getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.gcs_snapshot_storage_location.toString());

        String gcsSnapshotFormatStr = Utils.getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.gcs_snapshot_format.toString());
        GCSSnapshotFormat gcsSnapshotFormat = gcsSnapshotFormatStr.isEmpty() ? GCSSnapshotFormat.NOT_APPLICABLE : GCSSnapshotFormat.valueOf(gcsSnapshotFormatStr);

        // these fields are only used when we read from a data catalog tag
        // when we read from fallback configurations we can provide default values
        BackupConfigSource configSource = BackupConfigSource.SYSTEM;
        Timestamp lastBackupAt = Timestamp.MIN_VALUE;
        String lastBqSnapshotUri = "";
        String lastGcsSnapshotUri = "";

        if(!fromFallbackConfig){
            configSource = BackupConfigSource.fromString(
                    Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.config_source.toString())
            );

            String lastBackupAtStr = Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.last_backup_at.toString());
            if (lastBackupAtStr.isEmpty()) {
                lastBackupAt = Timestamp.MIN_VALUE;
            } else {
                lastBackupAt = Timestamp.parseTimestamp(lastBackupAtStr);
            }

            lastBqSnapshotUri = Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.last_bq_snapshot_storage_uri.toString());
            lastGcsSnapshotUri = Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.last_gcs_snapshot_storage_uri.toString());
        }


        return new BackupPolicy(
                cron,
                method,
                timeTravelOffsetDays,
                Double.valueOf(bqSnapshotExpirationDays),
                bqSnapshotStorageProject,
                bqSnapshotStorageDataset,
                gcsSnapshotStorageLocation,
                gcsSnapshotFormat,
                configSource,
                lastBackupAt,
                lastBqSnapshotUri,
                lastGcsSnapshotUri
        );
    }
}
