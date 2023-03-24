output "subnet_id" {
  value       = google_compute_subnetwork.subnetwork.id
  description = "An identifier for the resource with format projects/{{project}}/regions/{{region}}/subnetworks/{{name}}"
}

output "subnet_name" {
  value       = google_compute_subnetwork.subnetwork.name
  description = "The subnet name."
}

output "self_link" {
  value       = google_compute_subnetwork.subnetwork.self_link
  description = "The URI of the created resource."
}

output "gateway_address" {
  value       = google_compute_subnetwork.subnetwork.gateway_address
  description = "The gateway address for default routes to reach destination addresses outside this subnetwork."
}

output "external_ipv6_prefix" {
  value       = google_compute_subnetwork.subnetwork.external_ipv6_prefix
  description = "The range of external IPv6 addresses that are owned by this subnetwork."
}

output "ipv6_cidr_range" {
  value       = google_compute_subnetwork.subnetwork.ipv6_cidr_range
  description = "The range of internal IPv6 addresses that are owned by this subnetwork."
}