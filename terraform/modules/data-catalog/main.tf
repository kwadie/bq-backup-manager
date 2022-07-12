# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/data_catalog_policy_tag


### Create Tag Template

resource "google_data_catalog_tag_template" "snapshot_tag_template" {
  tag_template_id = "bq_snapshot_manager_template"
  project = var.project
  region = var.region
  display_name = "BigQuery Snapshot Manager"

  fields {
    field_id = "latest_snapshot_timestamp"
    display_name = "Timestamp of the latest snapshot taken"
    type {
      primitive_type = "STRING"
    }
    is_required = true
  }

  fields {
    field_id = "latest_snapshot_time"
    display_name = "Datetime of the latest snapshot taken"
    type {
      primitive_type = "TIMESTAMP"
    }
    is_required = true
  }

  fields {
    field_id = "latest_snapshot_id"
    display_name = "BigQuery table ID of the snapshot"
    type {
      primitive_type = "STRING"
    }
    is_required = true
  }

  fields {
    field_id = "latest_snapshot_run_tracking_id"
    display_name = "Tracking ID of the BigQuery Snapshot Manager solution that took the snapshot"
    type {
      primitive_type = "STRING"
    }
    is_required = true
  }

  fields {
    field_id = "last_known_snapshot_cron"
    display_name = "Last known snapshot frequency as a cron expression"
    type {
      primitive_type = "STRING"
    }
    is_required = true
  }

  force_delete = "true"
}