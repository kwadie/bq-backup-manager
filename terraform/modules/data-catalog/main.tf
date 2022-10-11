# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/data_catalog_tag_template

### Create Tag Template

resource "google_data_catalog_tag_template" "snapshot_tag_template" {
  tag_template_id = "bq_backup_manager_template"
  project = var.project
  region = var.region
  display_name = "BigQuery Backup Manager"

  fields {
    field_id = "config_source"
    display_name = "Configuration Source"
    type {
      enum_type {
        allowed_values {
          display_name = "MANUAL"
        }
        allowed_values {
          display_name = "SYSTEM"
        }
      }
    }
  }

  fields {
    field_id = "backup_cron"
    display_name = "Cron expression for backup frequency"
    type {
      primitive_type = "STRING"
    }
    is_required = true
  }

  fields {
    field_id = "last_backup_at"
    display_name = "Timestamp of the latest backup taken"
    type {
      primitive_type = "TIMESTAMP"
    }
    is_required = false
  }

  fields {
    field_id = "backup_time_travel_offset_days"
    display_name = "Number of days in the past where the backup is taken relative to NOW"
    type {
      enum_type {
        allowed_values {
          display_name = "0"
        }
        allowed_values {
          display_name = "1"
        }
        allowed_values {
          display_name = "2"
        }
        allowed_values {
          display_name = "3"
        }
        allowed_values {
          display_name = "4"
        }
        allowed_values {
          display_name = "5"
        }
        allowed_values {
          display_name = "6"
        }
        allowed_values {
          display_name = "7"
        }
      }
    }
    is_required = true
  }

  fields {
    field_id = "backup_method"
    display_name = "How to backup this table"
    type {
      enum_type {
        allowed_values {
          display_name = "BigQuery Snapshot"
        }
        allowed_values {
          display_name = "GCS Snapshot"
        }
        allowed_values {
          display_name = "Both"
        }
      }
    }
    is_required = true
  }

  fields {
    field_id = "bq_snapshot_storage_project"
    display_name = "GCP project where the BigQuery snapshot is saved"
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  fields {
    field_id = "bq_snapshot_storage_dataset"
    display_name = "Dataset where the BigQuery snapshot is saved"
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  fields {
    field_id = "bq_snapshot_expiration_days"
    display_name = "BigQuery snapshot retention period in days"
    type {
      primitive_type = "DOUBLE"
    }
    is_required = false
  }

  fields {
    field_id = "gcs_snapshot_storage_location"
    display_name = "GCS path to store table snapshots"
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  fields {
    field_id = "gcs_snapshot_format"
    display_name = "Export format and compression"
    type {
      enum_type {
        allowed_values {
          display_name = "CSV"
        }
        allowed_values {
          display_name = "CSV_GZIP"
        }
        allowed_values {
          display_name = "JSON"
        }
        allowed_values {
          display_name = "JSON_GZIP"
        }
        allowed_values {
          display_name = "AVRO"
        }
        allowed_values {
          display_name = "AVRO_DEFLATE"
        }
        allowed_values {
          display_name = "AVRO_SNAPPY"
        }
        allowed_values {
          display_name = "PARQUET"
        }
        allowed_values {
          display_name = "PARQUET_SNAPPY"
        }
        allowed_values {
          display_name = "PARQUET_GZIP"
        }
      }
    }
    is_required = false
  }

  // deleting the tag template will delete all configs attached to tables
  force_delete = false
}