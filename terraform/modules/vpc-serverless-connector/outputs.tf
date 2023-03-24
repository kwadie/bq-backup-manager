output "id" {
  value = google_vpc_access_connector.connector.id
  description = "An identifier for the resource with format projects/{{project}}/locations/{{region}}/connectors/{{name}}"
}

output "state" {
  value = google_vpc_access_connector.connector.state
  description = "State of the VPC access connector."
}

output "self_link" {
  value = google_vpc_access_connector.connector.self_link
  description = "The fully qualified name of this VPC connector."
}

output "name" {
  value = google_vpc_access_connector.connector.name
  description = "The name of the VPC connector."
}

output "connector" {
  value = google_vpc_access_connector.connector
  description = "The created VPC connector resource."
}