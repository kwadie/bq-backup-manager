package com.google.cloud.pso.bq_snapshot_manager.functions.f02_configurator;

public class ConfiguratorConfig {

    private final String projectId;
    private final String bigQuerySnapshoterTopic;
    private final String gcsSnapshoterTopic;
    private final String backupTagTemplateId;

    private final String applicationName;

    public ConfiguratorConfig(String projectId,
                              String bigQuerySnapshoterTopic,
                              String gcsSnapshoterTopic,
                              String backupTagTemplateId,
                              String applicationName
                              ) {
        this.projectId = projectId;
        this.bigQuerySnapshoterTopic = bigQuerySnapshoterTopic;
        this.gcsSnapshoterTopic = gcsSnapshoterTopic;
        this.backupTagTemplateId = backupTagTemplateId;
        this.applicationName = applicationName;
    }

    public String getProjectId() {
        return projectId;
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

    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String toString() {
        return "ConfiguratorConfig{" +
                "projectId='" + projectId + '\'' +
                ", bigQuerySnapshoterTopic='" + bigQuerySnapshoterTopic + '\'' +
                ", gcsSnapshoterTopic='" + gcsSnapshoterTopic + '\'' +
                ", backupTagTemplateId='" + backupTagTemplateId + '\'' +
                ", applicationName='" + applicationName + '\'' +
                '}';
    }
}
