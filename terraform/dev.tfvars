

project = "bqsm-host"
compute_region = "europe-west3"
data_region = "eu"

bigquery_dataset_name = "bq_snapshot_manager"

is_dry_run = false

cloud_scheduler_account = "service-752982373840@gcp-sa-cloudscheduler.iam.gserviceaccount.com"

terraform_service_account = "bq-snapshot-mgr-terraform@bqsm-host.iam.gserviceaccount.com"

dispatcher_service_image = "europe-west3-docker.pkg.dev/bqsm-host/docker-repo/bqsm-dispatcher-service:latest"
configurator_service_image = "europe-west3-docker.pkg.dev/bqsm-host/docker-repo/bqsm-configurator-service:latest"
snapshoter_bq_service_image = "europe-west3-docker.pkg.dev/bqsm-host/docker-repo/bqsm-snapshoter-bq-service:latest"
snapshoter_gcs_service_image = "europe-west3-docker.pkg.dev/bqsm-host/docker-repo/bqsm-snapshoter-gcs-service:latest"
tagger_service_image = "europe-west3-docker.pkg.dev/bqsm-host/docker-repo/bqsm-tagger-service:latest"

scheduler = {
    name = "heart_beat"
    cron = "0 0 * * *"
    payload = {
      is_force_run = false

      folders_include_list = [456209084685]
      projects_include_list = []
      projects_exclude_list = ["bqsm-host"]
      datasets_include_list = []
      datasets_exclude_list = []
      tables_include_list = []
      tables_exclude_list = []
    }
  }

snapshot_policy = {
  "default_policy": {
    "backup_cron": "0 0 1 * *",
    "backup_method": "BigQuery Snapshot",
    "backup_time_travel_offset_days": "7",
    "bq_snapshot_expiration_days": "15",
    "bq_snapshot_storage_project": "project",
    "bq_snapshot_storage_dataset": "dataset",
    "gcs_snapshot_storage_location": "gs://bla/",
    "gcs_snapshot_format": "",
    "config_source": "SYSTEM",
    "last_backup_at": ""
  },
  "folder_overrides": {
    "456209084685": {
      "backup_cron": "*****",
      "backup_method": "BigQuery Snapshot",
      "backup_time_travel_offset_days": "0",
      "bq_snapshot_expiration_days": "15",
      "bq_snapshot_storage_project": "project",
      "bq_snapshot_storage_dataset": "dataset",
      "gcs_snapshot_storage_location": "gs://bla/",
      "gcs_snapshot_format": "",
      "config_source": "SYSTEM",
      "last_backup_at": ""
    },
  },
  "project_overrides": {
    "bqsm-data-1": {
      "backup_cron": "0 0 1 * *",
      "backup_method": "BigQuery Snapshot",
      "backup_time_travel_offset_days": "0",
      "bq_snapshot_expiration_days": "15",
      "bq_snapshot_storage_project": "bqsm-data-1",
      "bq_snapshot_storage_dataset": "backup",
      "gcs_snapshot_storage_location": "",
      "gcs_snapshot_format": "",
      "config_source": "SYSTEM",
      "last_backup_at": ""
    }
  },
  "dataset_overrides": {
    "project1.dataset1": {
      "backup_cron": "*****",
      "backup_method": "BigQuery Snapshot",
      "backup_time_travel_offset_days": "0",
      "bq_snapshot_expiration_days": "15",
      "bq_snapshot_storage_project": "project",
      "bq_snapshot_storage_dataset": "dataset",
      "gcs_snapshot_storage_location": "gs://bla/",
      "gcs_snapshot_format": "",
      "config_source": "SYSTEM",
      "last_backup_at": ""
    },
    "project1.dataset2": {
      "backup_cron": "*****",
      "backup_method": "BigQuery Snapshot",
      "backup_time_travel_offset_days": "0",
      "bq_snapshot_expiration_days": "15",
      "bq_snapshot_storage_project": "project",
      "bq_snapshot_storage_dataset": "dataset",
      "gcs_snapshot_storage_location": "gs://bla/",
      "gcs_snapshot_format": "",
      "config_source": "SYSTEM",
      "last_backup_at": ""
    }
  },
  "table_overrides": {
    "p1.d1.t1": {
      "backup_cron": "*****",
      "backup_method": "BigQuery Snapshot",
      "backup_time_travel_offset_days": "0",
      "bq_snapshot_expiration_days": "15",
      "bq_snapshot_storage_project": "project",
      "bq_snapshot_storage_dataset": "dataset",
      "gcs_snapshot_storage_location": "gs://bla/",
      "gcs_snapshot_format": "",
      "config_source": "SYSTEM",
      "last_backup_at": ""
    },
    "p1.d1.t2": {
      "backup_cron": "*****",
      "backup_method": "BigQuery Snapshot",
      "backup_time_travel_offset_days": "0",
      "bq_snapshot_expiration_days": "15",
      "bq_snapshot_storage_project": "project",
      "bq_snapshot_storage_dataset": "dataset",
      "gcs_snapshot_storage_location": "gs://bla/",
      "gcs_snapshot_format": "",
      "config_source": "SYSTEM",
      "last_backup_at": ""
    }
  }
}
