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

variable "project" {}

variable "compute_region" {}

variable "data_region" {}

variable "bigquery_dataset_name" {
  default = "bq_snapshot_manager"
}



variable "sa_dispatcher" {
  default = "dispatcher"
}


variable "sa_dispatcher_tasks" {
  default = "dispatcher-tasks"
}


variable "sa_snapshoter" {
  default = "snapshoter"
}

variable "sa_snapshoter_tasks" {
  default = "snapshoter-tasks"
}

variable "sa_tagger" {
  default = "tagger"
}

variable "sa_tagger_tasks" {
  default = "tagger-tasks"
}

variable "log_sink_name" {
  default = "sc_bigquery_log_sink"
}


variable "dispatcher_service_name" {
  default = "s1-dispatcher"
}

variable "snapshoter_service_name" {
  default = "s2-snapshoter"
}

variable "tagger_service_name" {
  default = "s3-tagger"
}


variable "dispatcher_pubsub_topic" {
  default = "dispatcher_topic"
}

variable "dispatcher_pubsub_sub" {
  default = "dispatcher_push_sub"
}

variable "snapshoter_pubsub_topic" {
  default = "snapshoter_topic"
}

variable "snapshoter_pubsub_sub" {
  default = "snapshoter_push_sub"
}

variable "tagger_pubsub_topic" {
  default = "tagger_topic"
}

variable "tagger_pubsub_sub" {
  default = "tagger_push_sub"
}

variable "gcs_flags_bucket_name" {
  default = "bq-snapshot-mgr-flags"
}

# Images
variable "dispatcher_service_image" {}

variable "snapshoter_service_image" {}

variable "tagger_service_image" {}


variable "cloud_scheduler_account" {
  description = "Service agent account for Cloud Scheduler. Format service-<project number>@gcp-sa-cloudscheduler.iam.gserviceaccount.com"
}

variable "terraform_service_account" {
  description = "service account used by terraform to deploy to GCP"
}

variable "is_dry_run" {
  type = bool
  default = false
  description = "Taking snapshots and tagging tables (False) or just logging actions (True)"
}


# Dispatcher settings.
variable "dispatcher_service_timeout_seconds" {
  description = "Max period for the cloud run service to complete a request. Otherwise, it terminates with HTTP 504 and NAK to PubSub (retry)"
  type = number
  # Dispatcher might need relatively long time to process large BigQuery scan scopes
  default = 540 # 9m
}

variable "dispatcher_subscription_ack_deadline_seconds" {
  description = "This value is the maximum time after a subscriber receives a message before the subscriber should acknowledge the message. If it timeouts without ACK PubSub will retry the message."
  type = number
  // This should be higher than the service_timeout_seconds to avoid retrying messages that are still processing
  // range is 10 to 600
  default = 600 # 10m
}

variable "dispatcher_subscription_message_retention_duration" {
  description = "How long to retain unacknowledged messages in the subscription's backlog"
  type = string
  # In case of unexpected problems we want to avoid a buildup that re-trigger functions (e.g. Tagger issuing unnecessary BQ queries)
  # min value must be at least equal to the ack_deadline_seconds
  # Dispatcher should have the shortest retention possible because we want to avoid retries (on the app level as well)
  default = "600s" # 10m
}

# snapshoter settings.
variable "snapshoter_service_timeout_seconds" {
  description = "Max period for the cloud run service to complete a request. Otherwise, it terminates with HTTP 504 and NAK to PubSub (retry)"
  type = number
  default = 300 # 5m
}

variable "snapshoter_subscription_ack_deadline_seconds" {
  description = "This value is the maximum time after a subscriber receives a message before the subscriber should acknowledge the message. If it timeouts without ACK PubSub will retry the message."
  type = number
  // This should be higher than the service_timeout_seconds to avoid retrying messages that are still processing
  default = 420 # 7m
}

variable "snapshoter_subscription_message_retention_duration" {
  description = "How long to retain unacknowledged messages in the subscription's backlog"
  type = string
  # In case of unexpected problems we want to avoid a buildup that re-trigger functions (e.g. Tagger issuing unnecessary BQ queries)
  # It also sets how long should we keep trying to process one run
  # min value must be at least equal to the ack_deadline_seconds
  # snapshoter should have a relatively long retention to handle runs with large number of tables.
  default = "86400s" # 24h
}

# Tagger settings.
variable "tagger_service_timeout_seconds" {
  description = "Max period for the cloud run service to complete a request. Otherwise, it terminates with HTTP 504 and NAK to PubSub (retry)"
  type = number
  # Tagger is using BQ batch jobs that might need time to start running and thus a relatively longer timeout
  default = 540 # 9m
}

variable "tagger_subscription_ack_deadline_seconds" {
  description = "This value is the maximum time after a subscriber receives a message before the subscriber should acknowledge the message. If it timeouts without ACK PubSub will retry the message."
  type = number
  // This should be higher than the service_timeout_seconds to avoid retrying messages that are still processing
  // range is 10 to 600
  default = 600 # 10m
}

variable "tagger_subscription_message_retention_duration" {
  description = "How long to retain unacknowledged messages in the subscription's backlog"
  type = string
  # In case of unexpected problems we want to avoid a buildup that re-trigger functions (e.g. Tagger issuing unnecessary BQ queries)
  # It also sets how long should we keep trying to process one run
  # min value must be at least equal to the ack_deadline_seconds
  # Tagger should have a relatively long retention to handle runs with large number of tables.
  default = "86400s" # 24h
}

variable "schedulers" {}

variable "snapshot_policy" {}





