variable "project" {
  type = string
}

variable "region" {
  type = string
}

variable "dataset" {
  type = string
}

variable "logging_sink_sa" {
  type = string
}

variable "common_labels" {
  type = map(string)
}

variable "gcs_backup_policies_bucket_name" {
  type = string
}






