output "firewall_rules" {
  value       = google_compute_firewall.rules
  description = "The created firewall rule resources"
}