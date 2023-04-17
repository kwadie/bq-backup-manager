output "create_gcs_flags_bucket_name" {
  value = google_storage_bucket.gcs_flags_bucket.name
}

output "create_gcs_backup_policies_bucket_name" {
  value = google_storage_bucket.gcs_backup_policies_bucket.name
}