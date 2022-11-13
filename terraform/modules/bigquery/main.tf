# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/bigquery_table
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/bigquery_dataset


######## Datasets ##############################################################

resource "google_bigquery_dataset" "results_dataset" {
  project = var.project
  location = var.region
  dataset_id = var.dataset
  description = "To store DLP results from BQ Security Classifier app"
}

# Logging BQ sink must be able to write data to logging table in the dataset
resource "google_bigquery_dataset_iam_member" "logging_sink_access" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  role = "roles/bigquery.dataEditor"
  member = var.logging_sink_sa
}

##### Tables #######################################################


resource "google_bigquery_table" "logging_table" {
  project = var.project
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  # don't change the name so that cloud logging can find it
  table_id = "run_googleapis_com_stdout"

  time_partitioning {
    type = "DAY"
    #expiration_ms = 604800000 # 7 days
  }

  schema = file("modules/bigquery/schema/run_googleapis_com_stdout.json")

  deletion_protection = true
}


### Monitoring Views ##################################################

resource "google_bigquery_table" "view_audit_log_by_table" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_audit_log_by_table"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_audit_log_by_table.tpl",
      {
        project = var.project
        dataset = var.dataset
        logging_table = google_bigquery_table.logging_table.table_id
      }
    )
  }
}

resource "google_bigquery_table" "view_audit_log_by_table_grouped" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_audit_log_by_table_grouped"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_audit_log_by_table_grouped.tpl",
      {
        project = var.project
        dataset = var.dataset
        v_audit_log_by_table = google_bigquery_table.view_audit_log_by_table.table_id
      }
    )
  }
}

resource "google_bigquery_table" "logging_view_steps" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_steps"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_steps.tpl",
    {
      project = var.project
      dataset = var.dataset
      logging_table = google_bigquery_table.logging_table.table_id
    }
    )
  }
}

resource "google_bigquery_table" "view_service_calls" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_service_calls"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_service_calls.tpl",
    {
      project = var.project
      dataset = var.dataset
      logging_view_steps = google_bigquery_table.logging_view_steps.table_id
    }
    )
  }
}

resource "google_bigquery_table" "view_run_summary" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_run_summary"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_run_summary.tpl",
    {
      project = var.project
      dataset = var.dataset
      v_unified_logging = google_bigquery_table.view_audit_log_by_table.table_id
    }
    )
  }
}

resource "google_bigquery_table" "view_run_summary_counts" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_run_summary_counts"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_run_summary_counts.tpl",
    {
      project = var.project
      dataset = var.dataset
      v_run_summary = google_bigquery_table.view_run_summary.table_id
    }
    )
  }
}

resource "google_bigquery_table" "view_errors_non_retryable" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_errors_non_retryable"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_errors_non_retryable.tpl",
    {
      project = var.project
      dataset = var.dataset
      logging_table = google_bigquery_table.logging_table.table_id
    }
    )
  }
}

resource "google_bigquery_table" "view_errors_retryable" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_errors_retryable"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_errors_retryable.tpl",
    {
      project = var.project
      dataset = var.dataset
      logging_table = google_bigquery_table.logging_table.table_id
    }
    )
  }
}

resource "google_bigquery_table" "view_tracking_id_map" {
  dataset_id = google_bigquery_dataset.results_dataset.dataset_id
  table_id = "v_tracking_id_to_table_map"

  deletion_protection = false

  view {
    use_legacy_sql = false
    query = templatefile("modules/bigquery/views/v_tracking_id_to_table_map.tpl",
    {
      project = var.project
      dataset = var.dataset
      logging_table = google_bigquery_table.logging_table.table_id
    }
    )
  }
}









