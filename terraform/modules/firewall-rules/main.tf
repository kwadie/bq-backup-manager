resource "google_compute_firewall" "rules" {
  name                    = var.name
  description             = var.description
  direction               = var.direction
  network                 = var.network_name
  project                 = var.project_id
  source_ranges           = var.direction == "INGRESS" ? var.ranges : null
  destination_ranges      = var.direction == "EGRESS" ? var.ranges : null
  source_tags             = var.source_tags
  source_service_accounts = var.source_service_accounts
  target_tags             = var.target_tags
  target_service_accounts = var.target_service_accounts
  priority                = var.priority

  log_config {
    metadata = var.metadata
  }

  dynamic "allow" {
    for_each = var.allow == null ? [] : var.allow
    content {
      protocol = allow.value.protocol
      ports    = try(allow.value.ports, null)
    }
  }

  dynamic "deny" {
    for_each = var.deny == null ? [] : var.deny
    content {
      protocol = deny.value.protocol
      ports    = try(deny.value.ports, null)
    }
  }
}