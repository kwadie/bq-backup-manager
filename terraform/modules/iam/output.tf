

output "sa_dispatcher_email" {
  value = google_service_account.sa_dispatcher.email
}

output "sa_configurator_email" {
  value = google_service_account.sa_configurator.email
}

output "sa_snapshoter_bq_email" {
  value = google_service_account.sa_snapshoter_bq.email
}

output "sa_snapshoter_gcs_email" {
  value = google_service_account.sa_snapshoter_gcs.email
}

output "sa_tagger_email" {
  value = google_service_account.sa_tagger.email
}

output "sa_dispatcher_tasks_email" {
  value = google_service_account.sa_dispatcher_tasks.email
}

output "sa_configurator_tasks_email" {
  value = google_service_account.sa_configurator_tasks.email
}

output "sa_snapshoter_bq_tasks_email" {
  value = google_service_account.sa_snapshoter_bq_tasks.email
}

output "sa_snapshoter_gcs_tasks_email" {
  value = google_service_account.sa_snapshoter_gcs_tasks.email
}

output "sa_tagger_tasks_email" {
  value = google_service_account.sa_tagger_tasks.email
}





