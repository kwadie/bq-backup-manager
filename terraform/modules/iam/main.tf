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

resource "google_service_account" "sa_snapshoter" {
  project = var.project
  account_id = var.sa_snapshoter
  display_name = "Runtime SA for Snapshoter service"
}

resource "google_service_account" "sa_snapshoter_tasks" {
  project = var.project
  account_id = var.sa_snapshoter_tasks
  display_name = "To authorize PubSub Push requests to Inspector Service"
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

# Grant sa_dispatcher access to submit query jobs
resource "google_project_iam_member" "sa_dispatcher_bq_job_user" {
  project = var.project
  role = "roles/bigquery.jobUser"
  member = "serviceAccount:${google_service_account.sa_dispatcher.email}"
}

// tagging dispatcher needs to read data from dlp results table and views created inside the solution-managed dataset
// e.g. listing tables to be tagged
resource "google_bigquery_dataset_access" "sa_dispatcher_bq_dataset_reader" {
  dataset_id    = var.bq_results_dataset
  role          = "roles/bigquery.dataViewer"
  user_by_email = google_service_account.sa_dispatcher.email
}

#### Snapshoter Tasks SA Permissions ###

resource "google_service_account_iam_member" "sa_snapshoter_account_user_sa_inspector_tasks" {
  service_account_id = google_service_account.sa_snapshoter.name
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.sa_snapshoter_tasks.email}"
}

#### Snapshoter SA Permissions ###
# TODO: add relevant snapshoter permissions

# Grant sa_inspector access to list dlp jobs
resource "google_project_iam_member" "sa_inspector_dlp_jobs_editor" {
  project = var.project
  role = "roles/dlp.jobsEditor"
  member = "serviceAccount:${google_service_account.sa_snapshoter.email}"
}

#### Tagger Tasks SA Permissions ###

resource "google_service_account_iam_member" "sa_tagger_account_user_sa_tagger_tasks" {
  service_account_id = google_service_account.sa_tagger.name
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.sa_tagger_tasks.email}"
}

