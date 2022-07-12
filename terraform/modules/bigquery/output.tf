
output "results_dataset" {
  value = google_bigquery_dataset.results_dataset.dataset_id
}

output "logging_table" {
  value = google_bigquery_table.logging_table.table_id
}