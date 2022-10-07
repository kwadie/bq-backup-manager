package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

public class ConfiguratorConfig {

    private final String projectId;
    private final String defaultBackupPolicyJson;
    private final String bigQuerySnapshoterTopic;
    private final String gcsSnapshoterTopic;
    private final String backupTagTemplateId;

    public ConfiguratorConfig(String projectId,
                              String defaultBackupPolicyJson,
                              String bigQuerySnapshoterTopic,
                              String gcsSnapshoterTopic,
                              String backupTagTemplateId) {
        this.projectId = projectId;
        this.defaultBackupPolicyJson = defaultBackupPolicyJson;
        this.bigQuerySnapshoterTopic = bigQuerySnapshoterTopic;
        this.gcsSnapshoterTopic = gcsSnapshoterTopic;
        this.backupTagTemplateId = backupTagTemplateId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getDefaultBackupPolicyJson() {
        return defaultBackupPolicyJson;
    }

    public String getBigQuerySnapshoterTopic() {
        return bigQuerySnapshoterTopic;
    }

    public String getGcsSnapshoterTopic() {
        return gcsSnapshoterTopic;
    }

    public String getBackupTagTemplateId() {
        return backupTagTemplateId;
    }

    @Override
    public String toString() {
        return "ConfiguratorConfig{" +
                "projectId='" + projectId + '\'' +
                ", defaultBackupPolicyJson='" + defaultBackupPolicyJson + '\'' +
                ", bigQuerySnapshoterTopic='" + bigQuerySnapshoterTopic + '\'' +
                ", gcsSnapshoterTopic='" + gcsSnapshoterTopic + '\'' +
                ", backupTagTemplateId='" + backupTagTemplateId + '\'' +
                '}';
    }
}
