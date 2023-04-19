# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/google_service_account
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/google_service_account_iam
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/google_project_iam#google_project_iam_member

############## Service Accounts ######################################

resource "google_service_account" "sa_dispatcher" {
  project = var.project
  account_id = var.sa_dispatcher
  display_name = "Runtime SA for Dispatcher service"
}

resource "google_service_account" "sa_tagger" {
  project = var.project
  account_id = var.sa_tagger
  display_name = "Runtime SA for Tagger service"
}

resource "google_service_account" "sa_dispatcher_tasks" {
  project = var.project
  account_id = var.sa_dispatcher_tasks
  display_name = "To authorize PubSub Push requests to Tagging Dispatcher Service"
}

resource "google_service_account" "sa_configurator" {
  project = var.project
  account_id = var.sa_configurator
  display_name = "Runtime SA for configurator service"
}

resource "google_service_account" "sa_configurator_tasks" {
  project = var.project
  account_id = var.sa_configurator_tasks
  display_name = "To authorize PubSub Push requests to configurator Service"
}

resource "google_service_account" "sa_snapshoter_bq" {
  project = var.project
  account_id = var.sa_snapshoter_bq
  display_name = "Runtime SA for BQ Snapshoter service"
}

resource "google_service_account" "sa_snapshoter_bq_tasks" {
  project = var.project
  account_id = var.sa_snapshoter_bq_tasks
  display_name = "To authorize PubSub Push requests to BQ Snapshoter Service"
}

resource "google_service_account" "sa_snapshoter_gcs" {
  project = var.project
  account_id = var.sa_snapshoter_gcs
  display_name = "Runtime SA for GCS Snapshoter service"
}

resource "google_service_account" "sa_snapshoter_gcs_tasks" {
  project = var.project
  account_id = var.sa_snapshoter_gcs_tasks
  display_name = "To authorize PubSub Push requests to GCS Snapshoter Service"
}

resource "google_service_account" "sa_tagger_tasks" {
  project = var.project
  account_id = var.sa_tagger_tasks
  display_name = "To authorize PubSub Push requests to Tagger Service"
}

############## Service Accounts Access ################################

# Use google_project_iam_member because it's Non-authoritative.
# It Updates the IAM policy to grant a role to a new member.
# Other members for the role for the project are preserved.


#### Dispatcher Tasks Permissions ###

resource "google_service_account_iam_member" "sa_dispatcher_account_user_sa_dispatcher_tasks" {
  service_account_id = google_service_account.sa_dispatcher.name
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.sa_dispatcher_tasks.email}"
}

#### Dispatcher SA Permissions ###

#### Configurator SA Permissions ###

// write cache entries and/or read backup policies (when using datastore as policies backend)
resource "google_project_iam_member" "sa_configurator_datastore_viewer" {
project = var.project
role    = "roles/datastore.user"
member  = "serviceAccount:${google_service_account.sa_configurator.email}"
}

// read backup policies when using data catalog as backend
resource "google_project_iam_member" "sa_configurator_datacatalog_viewer" {
  project = var.project
  role    = "roles/datacatalog.viewer"
  member  = "serviceAccount:${google_service_account.sa_configurator.email}"
}

#### Configurator Tasks SA Permissions ###
resource "google_service_account_iam_member" "sa_configurator_account_user_sa_configurator_tasks" {
  service_account_id = google_service_account.sa_configurator.name
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.sa_configurator_tasks.email}"
}

#### Snapshoter SA Permissions ###
# TODO: add relevant snapshoter permissions

#### BQ Snapshoter Tasks SA Permissions ###

resource "google_service_account_iam_member" "sa_snapshoter_bq_account_user_sa_inspector_tasks" {
  service_account_id = google_service_account.sa_snapshoter_bq.name
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.sa_snapshoter_bq_tasks.email}"
}

#### GCS Snapshoter Tasks SA Permissions ###

resource "google_service_account_iam_member" "sa_snapshoter_gcs_account_user_sa_inspector_tasks" {
  service_account_id = google_service_account.sa_snapshoter_gcs.name
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.sa_snapshoter_gcs_tasks.email}"
}

#### Tagger SA Permissions ###

// read / write for backup policies when using datastore as backend
resource "google_project_iam_member" "sa_tagger_datastore_user" {
project = var.project
role    = "roles/datastore.user"
member  = "serviceAccount:${google_service_account.sa_tagger.email}"
}

resource "google_project_iam_member" "sa_tagger_datacatalog_viewer" {
  project = var.project
  role    = "roles/datacatalog.viewer"
  member  = "serviceAccount:${google_service_account.sa_tagger.email}"
}

#### Tagger Tasks SA Permissions ###

resource "google_service_account_iam_member" "sa_tagger_account_user_sa_tagger_tasks" {
  service_account_id = google_service_account.sa_tagger.name
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.sa_tagger_tasks.email}"
}

