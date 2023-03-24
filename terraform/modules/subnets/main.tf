/******************************************
	Subnet configuration
 *****************************************/
resource "google_compute_subnetwork" "subnetwork" {
  name                     = var.subnet_name
  ip_cidr_range            = var.subnet_ip
  region                   = var.subnet_region
  private_ip_google_access = var.subnet_private_access
  network                  = var.network_name
  project                  = var.project_id
  description              = var.description
  
  log_config {
    aggregation_interval = try(var.subnet_flow_logs_interval, "INTERVAL_5_SEC")
    flow_sampling        = try(var.subnet_flow_logs_sampling, "0.5")
    metadata             = try(var.subnet_flow_logs_metadata, "INCLUDE_ALL_METADATA")
    filter_expr          = try(var.subnet_flow_logs_filter_expr, "true")
  }

  secondary_ip_range = [
    for i in range(length(var.secondary_ranges)) : var.secondary_ranges[i]
  ]

  purpose = var.purpose
  role    = var.role
}