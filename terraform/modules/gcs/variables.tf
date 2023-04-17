variable "project" {type = string}

variable "compute_region" {type = string}

variable "data_region" {type = string}

variable "gcs_flags_bucket_name" {type = string}

variable "gcs_flags_bucket_admins" {
  type = list(string)
}

variable "gcs_backup_policies_bucket_name" {type = string}

variable "gcs_backup_policies_bucket_admins" {
  type = list(string)
}

variable "common_labels" {
  type = map(string)
}