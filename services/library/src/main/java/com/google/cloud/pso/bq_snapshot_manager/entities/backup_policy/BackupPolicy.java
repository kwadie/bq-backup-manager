package com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy;

import com.google.cloud.Timestamp;
import com.google.cloud.datacatalog.v1.Tag;
import com.google.cloud.datacatalog.v1.TagField;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;
import com.google.common.base.Objects;
import com.google.gson.Gson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public BackupPolicy(@Nonnull String cron,
                        @Nonnull BackupMethod method,
                        @Nonnull TimeTravelOffsetDays timeTravelOffsetDays,
                        @Nullable Double bigQuerySnapshotExpirationDays,
                        @Nullable String bigQuerySnapshotStorageProject,
                        @Nullable String bigQuerySnapshotStorageDataset,
                        @Nullable String gcsSnapshotStorageLocation,
                        @Nullable GCSSnapshotFormat gcsExportFormat,
                        @Nonnull BackupConfigSource configSource,
                        @Nullable Timestamp lastBackupAt,
                        @Nullable String lastBqSnapshotStorageUri,
                        @Nullable String lastGcsSnapshotStorageUri) {

        // validate that all required fields are provided depending on the backup method
        List<DataCatalogBackupPolicyTagFields> missing = new ArrayList<>();

        if(cron == null){
            missing.add(DataCatalogBackupPolicyTagFields.backup_cron);
        }
        if(method == null){
            missing.add(DataCatalogBackupPolicyTagFields.backup_method);
        }
        if(timeTravelOffsetDays == null){
            missing.add(DataCatalogBackupPolicyTagFields.backup_time_travel_offset_days);
        }
        if(configSource == null){
            missing.add(DataCatalogBackupPolicyTagFields.config_source);
        }

        if(method.equals(BackupMethod.BIGQUERY_SNAPSHOT) || method.equals(BackupMethod.BOTH)){
            if (bigQuerySnapshotStorageProject == null){
                missing.add(DataCatalogBackupPolicyTagFields.bq_snapshot_storage_project);
            }
            if(bigQuerySnapshotStorageDataset == null){
                missing.add(DataCatalogBackupPolicyTagFields.bq_snapshot_storage_dataset);
            }
            if(bigQuerySnapshotExpirationDays == null){
                missing.add(DataCatalogBackupPolicyTagFields.bq_snapshot_expiration_days);
            }
        }

        if(method.equals(BackupMethod.GCS_SNAPSHOT) || method.equals(BackupMethod.BOTH)){
            if (gcsExportFormat == null){
                missing.add(DataCatalogBackupPolicyTagFields.gcs_snapshot_format);
            }
            if(gcsSnapshotStorageLocation == null){
                missing.add(DataCatalogBackupPolicyTagFields.gcs_snapshot_storage_location);
            }
        }

        if(!missing.isEmpty()){
            throw new IllegalArgumentException(
                    String.format("Backup policy is invalid for backup method '%s'. The following fields are missing %s",
                            method,
                            missing
                            )
            );
        }

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

    public boolean hasBigQuerySnapshotExpirationDays() {
        return bigQuerySnapshotExpirationDays != null;
    }

    public boolean hasBigQuerySnapshotStorageProject() {
        return bigQuerySnapshotStorageProject != null;
    }

    public boolean hasBigQuerySnapshotStorageDataset() {
        return bigQuerySnapshotStorageDataset != null;
    }

    public boolean hasGcsSnapshotStorageLocation() {
        return gcsSnapshotStorageLocation != null;
    }

    public boolean hasLastBqSnapshotStorageUri() {
        return lastBqSnapshotStorageUri != null;
    }

    public boolean hasLastGcsSnapshotStorageUri() {
        return lastGcsSnapshotStorageUri != null;
    }

    public boolean hasGcsExportFormat() {
        return gcsExportFormat != null;
    }

    public boolean hasLastBackupAt() {
        return lastBackupAt != null;
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

        return fromMap(jsonMap);
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
        if (o == null || getClass() != o.getClass()) return false;
        BackupPolicy that = (BackupPolicy) o;
        return Objects.equal(cron, that.cron) && method == that.method && timeTravelOffsetDays == that.timeTravelOffsetDays && Objects.equal(bigQuerySnapshotExpirationDays, that.bigQuerySnapshotExpirationDays) && Objects.equal(bigQuerySnapshotStorageProject, that.bigQuerySnapshotStorageProject) && Objects.equal(bigQuerySnapshotStorageDataset, that.bigQuerySnapshotStorageDataset) && Objects.equal(gcsSnapshotStorageLocation, that.gcsSnapshotStorageLocation) && gcsExportFormat == that.gcsExportFormat && configSource == that.configSource && Objects.equal(lastBackupAt, that.lastBackupAt) && Objects.equal(lastBqSnapshotStorageUri, that.lastBqSnapshotStorageUri) && Objects.equal(lastGcsSnapshotStorageUri, that.lastGcsSnapshotStorageUri);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cron, method, timeTravelOffsetDays, bigQuerySnapshotExpirationDays, bigQuerySnapshotStorageProject, bigQuerySnapshotStorageDataset, gcsSnapshotStorageLocation, gcsExportFormat, configSource, lastBackupAt, lastBqSnapshotStorageUri, lastGcsSnapshotStorageUri);
    }

    /**
     * tagTemplateId is required.
     * tagName is optional. It's used to update an existing Tag on DataCatalog.
     *
     * @param tagTemplateId
     * @param tagName
     * @return
     */
    public Tag toDataCatalogTag(String tagTemplateId, String tagName) {

        Tag.Builder tagBuilder = Tag.newBuilder()
                .setTemplate(tagTemplateId);

        // required: cron
        TagField cronField = TagField.newBuilder().setStringValue(cron).build();
        tagBuilder.putFields(DataCatalogBackupPolicyTagFields.backup_cron.toString(), cronField);

        // required: backup method
        TagField methodField =
                TagField.newBuilder().setEnumValue(
                                TagField.EnumValue.newBuilder().setDisplayName(
                                                method.getText())
                                        .build())
                        .build();
        tagBuilder.putFields(DataCatalogBackupPolicyTagFields.backup_method.toString(), methodField);


        // required: time travel
        TagField timeTravelOffsetDaysField =
                TagField.newBuilder().setEnumValue(
                                TagField.EnumValue.newBuilder().setDisplayName(
                                                timeTravelOffsetDays.getText())
                                        .build())
                        .build();
        tagBuilder.putFields(DataCatalogBackupPolicyTagFields.backup_time_travel_offset_days.toString(),
                timeTravelOffsetDaysField);

        // optional: bq snapshot expiration
        if(bigQuerySnapshotExpirationDays != null){
            TagField bigQuerySnapshotExpirationDaysField =
                    TagField.newBuilder().setDoubleValue(bigQuerySnapshotExpirationDays).build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.bq_snapshot_expiration_days.toString(), bigQuerySnapshotExpirationDaysField);
        }

        // optional: bq snapshot project
        if(bigQuerySnapshotStorageProject != null){
            TagField bigQuerySnapshotStorageProjectField =
                    TagField.newBuilder().setStringValue(bigQuerySnapshotStorageProject).build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.bq_snapshot_storage_project.toString(),
                   bigQuerySnapshotStorageProjectField);
        }

        // optional: bq snapshot dataset
        if(bigQuerySnapshotStorageDataset != null){
            TagField bigQuerySnapshotStorageDatasetField =
                    TagField.newBuilder().setStringValue(bigQuerySnapshotStorageDataset).build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.bq_snapshot_storage_dataset.toString(),
                    bigQuerySnapshotStorageDatasetField);
        }

        // optional: gcs snapshot storage location
        if(gcsSnapshotStorageLocation != null){
            TagField gcsSnapshotStorageLocationField =
                    TagField.newBuilder().setStringValue(gcsSnapshotStorageLocation).build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.gcs_snapshot_storage_location.toString(),
                    gcsSnapshotStorageLocationField);
        }

        // optional: gcs export format
        if(gcsExportFormat != null){
            TagField gcsExportFormatField =
                    TagField.newBuilder().setEnumValue(
                                    TagField.EnumValue.newBuilder().setDisplayName(
                                                    gcsExportFormat.toString())
                                            .build())
                            .build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.gcs_snapshot_format.toString(),
                    gcsExportFormatField);
        }


        // required: config source
        TagField configSourceField =
                TagField.newBuilder().setEnumValue(
                                TagField.EnumValue.newBuilder().setDisplayName(
                                                configSource.toString())
                                        .build())
                        .build();
        tagBuilder.putFields(DataCatalogBackupPolicyTagFields.config_source.toString(), configSourceField);

        // optional: last backup at
        if(lastBackupAt != null){
            TagField lastBackupAtField =
                    TagField.newBuilder().setTimestampValue(
                            com.google.protobuf.Timestamp.newBuilder()
                                    .setSeconds(lastBackupAt.getSeconds())
                                    .setNanos(lastBackupAt.getNanos())
                                    .build()
                    ).build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.last_backup_at.toString(), lastBackupAtField);
        }


        // optional: last bq snapshot uri
        if(lastBqSnapshotStorageUri != null){
            TagField lastBqSnapshotUriField =
                    TagField.newBuilder().setStringValue(lastBqSnapshotStorageUri).build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.last_bq_snapshot_storage_uri.toString(),
                    lastBqSnapshotUriField);
        }

        // optional: last gcs snapshot uri
        if(lastGcsSnapshotStorageUri != null){
            TagField lastGcsSnapshotUriField =
                    TagField.newBuilder().setStringValue(lastGcsSnapshotStorageUri).build();
            tagBuilder.putFields(DataCatalogBackupPolicyTagFields.last_gcs_snapshot_storage_uri.toString(),
                    lastGcsSnapshotUriField);
        }

        if (tagName != null) {
            tagBuilder.setName(tagName);
        }

        return tagBuilder.build();
    }


    // used to parse from json map (fallback policies) or data catalog tags (backup policies)
    public static BackupPolicy fromMap(Map<String, String> tagTemplate) throws IllegalArgumentException {

        // parse required fields
        String cron = Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_cron.toString());

        BackupMethod method = BackupMethod.fromString(
                Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_method.toString())
        );

        TimeTravelOffsetDays timeTravelOffsetDays = TimeTravelOffsetDays.fromString(
                Utils.getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_time_travel_offset_days.toString())
        );

        // parse optional fields
        // these fields might not exist in the attached tag template if not filled. Same for fallback policies


        // parse optional BQ snapshot settings
        String bqSnapshotStorageProject = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.bq_snapshot_storage_project.toString(),
                null);

        String bqSnapshotStorageDataset = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.bq_snapshot_storage_dataset.toString(),
                null);

        String bqSnapshotExpirationDays = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.bq_snapshot_expiration_days.toString(),
                null);

        // parse optional GCS snapshot settings
        String gcsSnapshotStorageLocation = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.gcs_snapshot_storage_location.toString(),
                null);

        String gcsSnapshotFormatStr = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.gcs_snapshot_format.toString(),
                null);

        GCSSnapshotFormat gcsSnapshotFormat = gcsSnapshotFormatStr == null ? null : GCSSnapshotFormat.valueOf(gcsSnapshotFormatStr);

        // config source is not required in the fallback policies. It defaults to SYSTEM if not present
        String configSourceStr = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.config_source.toString(),
                null);

        BackupConfigSource configSource = configSourceStr == null ? BackupConfigSource.SYSTEM : BackupConfigSource.fromString(configSourceStr);

        String lastBackupAtStr = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.last_backup_at.toString(),
                null);
        Timestamp lastBackupAt = lastBackupAtStr == null ? null : Timestamp.parseTimestamp(lastBackupAtStr);

        String lastBqSnapshotUri = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.last_bq_snapshot_storage_uri.toString(),
                null);

        String lastGcsSnapshotUri = tagTemplate.getOrDefault(
                DataCatalogBackupPolicyTagFields.last_gcs_snapshot_storage_uri.toString(),
                null);

        return new BackupPolicy(
                cron,
                method,
                timeTravelOffsetDays,
                bqSnapshotExpirationDays == null? null :Double.valueOf(bqSnapshotExpirationDays),
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
