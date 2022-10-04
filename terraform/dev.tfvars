

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

snapshot_policy = "tbd"
// backup
//snapshot_policy = {
//
//  # default snapshot config
//  default_config = {
//    snapshot_storage_dataset = "p.d.t" # where to store the snapshot
//    snapshot_expiration_ms = 0, # snapshot expiration in milliseconds
//    time_travel_offset_ms = 0 # to determine a point in time from which the table is backed up (NOW - time_travel_offset_ms)
//  },
//
//  # except for these projects we want to use different snapshot configs than the default
//  projects_override = [
//    {
//      project_id = "project-1"
//      config = {
//        snapshot_storage_dataset = ""
//        snapshot_expiration_ms = 0,
//        time_travel_offset_ms = 0
//      }
//    },
//  ], # end of projects_override
//
//  # except for these datasets we want to use different snapshot configs than the default and project-level config
//  datasets_override = [
//    {
//      project_id = "project-1"
//      dataset_id = "dataset-1"
//      config = {
//        snapshot_storage_dataset = ""
//        snapshot_expiration_ms = 0,
//        time_travel_offset_ms = 0
//      }
//    },
//  ]
//
//  # except for these tables we want to use different snapshot configs than the default, project-level and dataset-level config
//  tables_override = [
//    {
//      project_id = "project-1"
//      dataset_id = "dataset-1"
//      table = "table-1"
//      config = {
//        snapshot_storage_dataset = ""
//        snapshot_expiration_ms = 0,
//        time_travel_offset_ms = 0
//      }
//    },
//  ]
//}


//
//# IDEA 1
//# Separate Policy from Execution scope
//
//scope_1 = {
//  tables_include_list = []
//  datasets_include_list = []
//  projects_include_list = ["bqsc-marketing-v1"]
//  datasets_exclude_list = ["bqsc-marketing-v1.marketing_sessions","bqsc-dwh-v1.stress_testing_1000", "bqsc-dwh-v1.stress_testing_3000", "bqsc-dwh-v1.stress_testing_20000", "bqsc-dwh-v1.stress_testing_original"]
//  tables_exclude_list = []
//}
//
//# Deploy x schedulers based on unique CRON expresions in the policy
//# One cron will trigger at time with the same scope
//  # Dispatcher lists down all tables in scope
//  # Dispatcher loops on all tables
//    # filters out tables that shouldn't run on that schedule
//    # determine the snapshot config for that table
//  # Snapshoter takes a snapshot of the table based on it's config
//
//# CONS
//  # List down all tables  for all crons then filter out
//  # Hard(er) to understand configs
//policy_1 = {
//
//  # default snapshot config
//  config = {
//    cron = 'MONTHLY'
//    target_dataset = 'p.d.t'
//    snapshot_expiration_ms = ''
//  },
//
//  # except for these projects we want to use a different snapshot config than the default
//  projects_override = [
//    {
//      project_id = "project-1"
//      config = {
//        cron = 'DAILY'
//        target_dataset = ''
//        snapshot_expiration_ms = ''
//      }
//    },
//    {
//      project_id = "project-2"
//      config = {
//        cron = 'DAILY'
//        snapshot_expiration_ms = ''
//      }
//    }
//  ], # end of projects_override
//
//  # except for these datasets we want to use a different snapshot config than the default
//  datasets_override = [
//    {
//      project_id = "project-1"
//      dataset_id = "dataset-1"
//      config = {
//        cron = 'HOURLY'
//        snapshot_expiration_ms = ''
//      }
//    },
//    {
//      project_id = "project-1"
//      dataset_id = "dataset-2"
//      config = {
//        cron = ''
//        snapshot_expiration_ms = ''
//      }
//    }
//  ] # end of projects_override
//
//  # except for these tables we want to use a different snapshot config than the default
//  tables_override = [
//    {
//      project_id = "project-1"
//      dataset_id = "dataset-1"
//      table = "table-1"
//      config = {
//        cron = ''
//        snapshot_expiration_ms = ''
//      }
//    },
//    {
//      project_id = "project-1"
//      dataset_id = "dataset-1"
//      table = "table-2"
//      config = {
//        cron = ''
//        snapshot_expiration_ms = ''
//      }
//    }
//    ]
//}
//
//
//# IDEA 2
//# config for policy and cron
//
//# Deploy x schedulers based on scope settings
//# Dispatcher recieves a scope payload and list down tables (reuse from PII classifier)
//# Dispatcher detects the snapshot setting for that table
//  # If table is in table_overrides else if it's dataset is under dataset_overrides else if it's project under project_overrides else default
//# Snapshoter takes snapshot based on provided configuration
//
//# CONS
// # overlapping scopes by mistake
//