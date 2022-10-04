#   Copyright 2021 Google LLC
#
#   Licensed under the Apache License, Version 2.0 (the "License");
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.

provider "google" {
  version = "~> 4.39.0"

  alias = "impersonation"
  scopes = [
    "https://www.googleapis.com/auth/cloud-platform",
    "https://www.googleapis.com/auth/userinfo.email",
  ]
}

data "google_service_account_access_token" "default" {
  provider = google.impersonation
  target_service_account = var.terraform_service_account
  scopes = [
    "userinfo-email",
    "cloud-platform"]
  lifetime = "1200s"
}

provider "google" {
  version = "~> 4.39.0"

  project = var.project
  region = var.compute_region

  access_token = data.google_service_account_access_token.default.access_token
  request_timeout = "60s"
}

provider "google-beta" {
  version = "~> 4.39.0"

  project = var.project
  region = var.compute_region

  access_token = data.google_service_account_access_token.default.access_token
  request_timeout = "60s"
}

locals {
  common_cloud_run_variables = [
    {
      name = "PROJECT_ID",
      value = var.project,
    },
    {
      name = "COMPUTE_REGION_ID",
      value = var.compute_region,
    },
    {
      name = "DATA_REGION_ID",
      value = var.data_region,
    },
    {
      name = "GCS_FLAGS_BUCKET",
      value = module.gcs.create_gcs_flags_bucket_name
    },
    {
      name = "IS_DRY_RUN",
      value = tostring(var.is_dry_run)
    }
  ]
}

module "iam" {
  source = "./modules/iam"
  project = var.project
  region = var.compute_region
  sa_dispatcher = var.sa_dispatcher
  sa_dispatcher_tasks = var.sa_dispatcher_tasks
  sa_snapshoter_bq = var.sa_snapshoter_bq
  sa_snapshoter_bq_tasks = var.sa_snapshoter_bq_tasks
  sa_snapshoter_gcs = var.sa_snapshoter_gcs
  sa_snapshoter_gcs_tasks = var.sa_snapshoter_gcs_tasks
  sa_tagger = var.sa_tagger
  sa_tagger_tasks = var.sa_tagger_tasks
  bq_results_dataset = module.bigquery.results_dataset
  sa_configurator = var.sa_configurator
  sa_configurator_tasks = var.sa_configurator_tasks
}

module "gcs" {
  source = "./modules/gcs"
  gcs_flags_bucket_name = "${var.project}-${var.gcs_flags_bucket_name}"
  project = var.project
  region = var.compute_region
  # because it's used by the cloud run services
  # both dispatchers should be admins. Add the inspection-dispatcher-sa only if it's being deployed
  gcs_flags_bucket_admins = [
    "serviceAccount:${module.iam.sa_dispatcher_email}",
    "serviceAccount:${module.iam.sa_configurator_email}",
    "serviceAccount:${module.iam.sa_snapshoter_bq_email}",
    "serviceAccount:${module.iam.sa_snapshoter_gcs_email}",
    "serviceAccount:${module.iam.sa_tagger_email}",
  ]
}

module "bigquery" {
  source = "./modules/bigquery"
  project = var.project
  region = var.data_region
  dataset = var.bigquery_dataset_name
  logging_sink_sa = module.cloud_logging.service_account
}

module "cloud_logging" {
  source = "./modules/cloud-logging"
  dataset = module.bigquery.results_dataset
  project = var.project
  region = var.compute_region
  log_sink_name = var.log_sink_name
}


### Cloud Run Services

module "cloud-run-dispatcher" {
  source = "./modules/cloud-run"
  project = var.project
  region = var.compute_region
  service_image = var.dispatcher_service_image
  service_name = var.dispatcher_service_name
  service_account_email = module.iam.sa_dispatcher_email
  invoker_service_account_email = module.iam.sa_dispatcher_tasks_email
  # Dispatcher could take time to list large number of tables
  timeout_seconds = var.dispatcher_service_timeout_seconds
  # We don't need high conc for the entry point
  max_containers = 1
  # We need more than 1 CPU to help accelerate processing of large BigQuery Scan scope
  max_cpu = 2
  # Use the common variables in addition to specific variables for this service
  environment_variables = concat(local.common_cloud_run_variables, [
    {
      name = "OUTPUT_TOPIC",
      value = module.pubsub-configurator.topic-name,
    },
  ]
  )
}

module "cloud-run-configurator" {
  source = "./modules/cloud-run"
  project = var.project
  region = var.compute_region
  service_image = var.configurator_service_image
  service_name = var.configurator_service_name
  service_account_email = module.iam.sa_configurator_email
  invoker_service_account_email = module.iam.sa_configurator_tasks_email
  timeout_seconds = var.configurator_service_timeout_seconds

  # Use the common variables in addition to specific variables for this service
  environment_variables = concat(local.common_cloud_run_variables, [
    {
      name = "SNAPSHOTER_BQ_OUTPUT_TOPIC",
      value = module.pubsub-snapshoter-bq.topic-name,
    },
    {
      name = "SNAPSHOTER_GCS_OUTPUT_TOPIC",
      value = module.pubsub-snapshoter-gcs.topic-name,
    },
    {
      name = "SNAPSHOT_POLICY_JSON",
      value = jsonencode(var.snapshot_policy)
    }
  ]
  )
}

module "cloud-run-snapshoter-bq" {
  source = "./modules/cloud-run"
  project = var.project
  region = var.compute_region
  service_image = var.snapshoter_bq_service_image
  service_name = var.snapshoter_bq_service_name
  service_account_email = module.iam.sa_snapshoter_bq_email
  invoker_service_account_email = module.iam.sa_snapshoter_bq_tasks_email
  timeout_seconds = var.snapshoter_bq_service_timeout_seconds

  # Use the common variables in addition to specific variables for this service
  environment_variables = concat(local.common_cloud_run_variables, [
    {
      name = "OUTPUT_TOPIC",
      value = module.pubsub-tagger.topic-name,
    },
    {
      name = "SNAPSHOT_POLICY_JSON",
      value = jsonencode(var.snapshot_policy)
    }
  ]
  )
}

module "cloud-run-snapshoter-gcs" {
  source = "./modules/cloud-run"
  project = var.project
  region = var.compute_region
  service_image = var.snapshoter_gcs_service_image
  service_name = var.snapshoter_gcs_service_name
  service_account_email = module.iam.sa_snapshoter_gcs_email
  invoker_service_account_email = module.iam.sa_snapshoter_gcs_tasks_email
  timeout_seconds = var.snapshoter_gcs_service_timeout_seconds

  # Use the common variables in addition to specific variables for this service
  environment_variables = concat(local.common_cloud_run_variables, [
    {
      name = "OUTPUT_TOPIC",
      value = module.pubsub-tagger.topic-name,
    },
    {
      name = "SNAPSHOT_POLICY_JSON",
      value = jsonencode(var.snapshot_policy)
    }
  ]
  )
}

module "cloud-run-tagger" {
  source = "./modules/cloud-run"
  project = var.project
  region = var.compute_region
  service_image = var.tagger_service_image
  service_name = var.tagger_service_name
  service_account_email = module.iam.sa_tagger_email
  invoker_service_account_email = module.iam.sa_tagger_tasks_email
  timeout_seconds = var.tagger_service_timeout_seconds

  # Use the common variables in addition to specific variables for this service
  environment_variables = concat(local.common_cloud_run_variables, [
    {
      name = "TAG_TEMPLATE_ID",
      value = module.data-catalog.tag_template_id,
    },
  ]
  )
}


# PubSub Resources

module "pubsub-dispatcher" {
  source = "./modules/pubsub"
  project = var.project
  subscription_endpoint = module.cloud-run-dispatcher.service_endpoint
  subscription_name = var.dispatcher_pubsub_sub
  subscription_service_account = module.iam.sa_dispatcher_tasks_email
  topic = var.dispatcher_pubsub_topic
  topic_publishers_sa_emails = [
    var.cloud_scheduler_account]
  # use a deadline large enough to process BQ listing for large scopes
  subscription_ack_deadline_seconds = var.dispatcher_subscription_ack_deadline_seconds
  # avoid resending dispatcher messages if things went wrong and the msg was NAK (e.g. timeout expired, app error, etc)
  # min value must be at equal to the ack_deadline_seconds
  subscription_message_retention_duration = var.dispatcher_subscription_message_retention_duration
}

module "pubsub-configurator" {
  source = "./modules/pubsub"
  project = var.project
  subscription_endpoint = module.cloud-run-configurator.service_endpoint
  subscription_name = var.configurator_pubsub_sub
  subscription_service_account = module.iam.sa_configurator_tasks_email
  topic = var.configurator_pubsub_topic
  topic_publishers_sa_emails = [
    module.iam.sa_dispatcher_email]
  subscription_ack_deadline_seconds = var.configurator_subscription_ack_deadline_seconds
  # How long to retain unacknowledged messages in the subscription's backlog, from the moment a message is published.
  # In case of unexpected problems we want to avoid a buildup that re-trigger functions
  subscription_message_retention_duration = var.configurator_subscription_message_retention_duration
}

module "pubsub-snapshoter-bq" {
  source = "./modules/pubsub"
  project = var.project
  subscription_endpoint = module.cloud-run-snapshoter-bq.service_endpoint
  subscription_name = var.snapshoter_bq_pubsub_sub
  subscription_service_account = module.iam.sa_snapshoter_bq_tasks_email
  topic = var.snapshoter_bq_pubsub_topic
  topic_publishers_sa_emails = [
    module.iam.sa_configurator_email]
  subscription_ack_deadline_seconds = var.snapshoter_bq_subscription_ack_deadline_seconds
  # How long to retain unacknowledged messages in the subscription's backlog, from the moment a message is published.
  # In case of unexpected problems we want to avoid a buildup that re-trigger functions
  subscription_message_retention_duration = var.snapshoter_bq_subscription_message_retention_duration
}

module "pubsub-snapshoter-gcs" {
  source = "./modules/pubsub"
  project = var.project
  subscription_endpoint = module.cloud-run-snapshoter-gcs.service_endpoint
  subscription_name = var.snapshoter_gcs_pubsub_sub
  subscription_service_account = module.iam.sa_snapshoter_gcs_tasks_email
  topic = var.snapshoter_gcs_pubsub_topic
  topic_publishers_sa_emails = [
    module.iam.sa_configurator_email]
  subscription_ack_deadline_seconds = var.snapshoter_gcs_subscription_ack_deadline_seconds
  # How long to retain unacknowledged messages in the subscription's backlog, from the moment a message is published.
  # In case of unexpected problems we want to avoid a buildup that re-trigger functions
  subscription_message_retention_duration = var.snapshoter_gcs_subscription_message_retention_duration
}

module "pubsub-tagger" {
  source = "./modules/pubsub"
  project = var.project
  subscription_endpoint = module.cloud-run-tagger.service_endpoint
  subscription_name = var.tagger_pubsub_sub
  subscription_service_account = module.iam.sa_tagger_tasks_email
  topic = var.tagger_pubsub_topic
  topic_publishers_sa_emails = [
    module.iam.sa_snapshoter_bq_email,
    module.iam.sa_snapshoter_gcs_email]
  # Tagger is using BigQuery queries in BATCH mode to avoid INTERACTIVE query concurency limits and they might take longer time to execute under heavy load
  # 10m is max allowed
  subscription_ack_deadline_seconds = var.tagger_subscription_ack_deadline_seconds
  # How long to retain unacknowledged messages in the subscription's backlog, from the moment a message is published.
  # In case of unexpected problems we want to avoid a buildup that re-trigger functions
  subscription_message_retention_duration = var.tagger_subscription_message_retention_duration
}

module "cloud-scheduler" {
  source = "./modules/cloud-scheduler"

  project = var.project
  region = var.compute_region
  target_uri = module.pubsub-dispatcher.topic-id

  scheduler_name = lookup(var.scheduler, "name")
  cron_expression = lookup(var.scheduler, "cron")
  payload = lookup(var.scheduler, "payload")
}

module "data-catalog" {
  source = "./modules/data-catalog"
  project = var.project
  region = var.data_region
}

