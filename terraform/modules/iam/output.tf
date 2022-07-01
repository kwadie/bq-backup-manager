

output "sa_dispatcher_email" {
  value = google_service_account.sa_dispatcher.email
}

output "sa_snapshoter_email" {
  value = google_service_account.sa_snapshoter.email
}

output "sa_tagger_email" {
  value = google_service_account.sa_tagger.email
}

output "sa_dispatcher_tasks_email" {
  value = google_service_account.sa_dispatcher_tasks.email
}

output "sa_snapshoter_tasks_email" {
  value = google_service_account.sa_snapshoter_tasks.email
}

output "sa_tagger_tasks_email" {
  value = google_service_account.sa_tagger_tasks.email
}





