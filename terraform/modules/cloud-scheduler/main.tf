# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/cloud_scheduler_job

resource "google_cloud_scheduler_job" "scheduler_job" {
  project = var.project
  name = var.scheduler_name
  description = "CRON job to trigger BQ Security Classifier"
  schedule = var.cron_expression

  retry_config {
    retry_count = 0
  }

  pubsub_target {
    # topic.id is the topic's full resource name.
    topic_name = var.target_uri
    data = base64encode(jsonencode(
    {
      isForceRun = lookup(var.payload, "is_force_run"),
      bigQueryScope = {
        folderIncludeList = lookup(var.payload, "folders_include_list"),
        projectIncludeList = lookup(var.payload, "projects_include_list"),
        projectExcludeList = lookup(var.payload, "projects_exclude_list"),
        datasetIncludeList = lookup(var.payload, "datasets_include_list"),
        datasetExcludeList = lookup(var.payload, "datasets_exclude_list"),
        tableIncludeList = lookup(var.payload, "tables_include_list"),
        tableExcludeList = lookup(var.payload, "tables_exclude_list")
      }
    }
    ))
  }
}




