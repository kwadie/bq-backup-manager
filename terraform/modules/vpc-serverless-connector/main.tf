resource "google_vpc_access_connector" "connector" {
  name          = var.name
  project       = var.project_id
  region        = var.region
  ip_cidr_range = var.ip_cidr_range
  network       = var.network
  
  dynamic "subnet" {
    for_each = var.subnet_name == null ? [] : [var.subnet_name]
    content {
      name       = subnet.value
      project_id = try(var.host_project_id, var.project_id)
    }
  }
  
  machine_type   = var.machine_type
  min_instances  = var.min_instances
  max_instances  = var.max_instances
  max_throughput = var.max_throughput
}