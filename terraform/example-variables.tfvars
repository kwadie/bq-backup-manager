project        = "<host project name>"
compute_region = "<gcp region>"
data_region    = "<gcp region including multi-regions eu|us"

cloud_scheduler_account = "service-<project number>@gcp-sa-cloudscheduler.iam.gserviceaccount.com"

terraform_service_account = "bq-backup-mgr-terraform@<host project name>.iam.gserviceaccount.com"

dispatcher_service_image     = "<compute region>-docker.pkg.dev/<host project>/<docker repo>/bqsm-dispatcher-service:latest"
configurator_service_image   = "<compute region>-docker.pkg.dev/<host project>/<docker repo>/bqsm-configurator-service:latest"
snapshoter_bq_service_image  = "<compute region>-docker.pkg.dev/<host project>/<docker repo>/bqsm-snapshoter-bq-service:latest"
snapshoter_gcs_service_image = "<compute region>-docker.pkg.dev/<host project>/<docker repo>/bqsm-snapshoter-gcs-service:latest"
tagger_service_image         = "<compute region>-docker.pkg.dev/<host project>/<docker repo>/bqsm-tagger-service:latest"

schedulers = [
  {
    name    = "heart_beat"
    cron    = "0 * * * *" # hourly
    payload = {
      is_force_run = false
      is_dry_run   = false

      folders_include_list  = []
      projects_include_list = []
      projects_exclude_list = []
      datasets_include_list = []
      datasets_exclude_list = []
      tables_include_list   = []
      tables_exclude_list   = []
    }
  }
]

# Fallback policies
fallback_policy = {
  "default_policy" : {
    "backup_cron" : "0 0 */6 * * *", # every 6 hours each day
    "backup_method" : "Both",
    "backup_time_travel_offset_days" : "7",
    "backup_project" : "project name",
    # bq settings
    "bq_snapshot_expiration_days" : "15",
    "bq_snapshot_storage_dataset" : "backups",
    # gcs settings
    "gcs_snapshot_storage_location" : "gs://backups/"
    "gcs_snapshot_format" : "AVRO_SNAPPY"
    "gcs_avro_use_logical_types" : true
  },
  "folder_overrides" : {
    "<folder number>" : {
      # policy attributes
    },
  },
  "project_overrides" : {
    "<project name>" : {
      # policy attributes
    }
  },
  "dataset_overrides" : {
    "<project name>.<dataset name>" : {
      # policy attributes
    }
  },
  "table_overrides" : {
    "<project name>.<dataset name>.<table name>" : {
      # policy attributes
    }
  }
}
