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
    order = 1
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
    order = 2
    type {
      primitive_type = "STRING"
    }
    is_required = true
  }

  fields {
    field_id = "backup_time_travel_offset_days"
    display_name = "Number of days in the past where the backup is taken relative to NOW"
    order = 3
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
    order = 4
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
    display_name = "BigQuery - Project where the snapshot is stored"
    order = 5
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  fields {
    field_id = "bq_snapshot_storage_dataset"
    display_name = "BigQuery - Dataset where the snapshot is stored"
    order = 6
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  fields {
    field_id = "bq_snapshot_expiration_days"
    display_name = "BigQuery - Snapshot retention period in days"
    order = 7
    type {
      primitive_type = "DOUBLE"
    }
    is_required = false
  }

  fields {
    field_id = "gcs_snapshot_storage_location"
    display_name = "GCS - Parent path to store all table snapshots"
    order = 8
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  fields {
    field_id = "gcs_snapshot_format"
    display_name = "GCS - Export format and compression"
    order = 9
    type {
      enum_type {
        allowed_values {
          display_name = "NOT_APPLICABLE"
        }
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

  fields {
    field_id = "last_backup_at"
    display_name = "Read-Only - Timestamp of the latest backup taken"
    order = 10
    type {
      primitive_type = "TIMESTAMP"
    }
    is_required = false
  }

  fields {
    field_id = "last_gcs_snapshot_storage_uri"
    display_name = "Read-Only - Last GCS snapshot location"
    order = 11
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  fields {
    field_id = "last_bq_snapshot_storage_uri"
    display_name = "Read-Only - Last BQ snapshot location"
    order = 12
    type {
      primitive_type = "STRING"
    }
    is_required = false
  }

  // deleting the tag template will delete all configs attached to tables
  force_delete = true
}

resource "google_data_catalog_tag_template_iam_member" "tag_template_user" {
  count = length(var.tagTemplateUsers)
  tag_template = google_data_catalog_tag_template.snapshot_tag_template.name
  role = "roles/datacatalog.tagTemplateUser"
  member = "serviceAccount:${var.tagTemplateUsers[count.index]}"
}