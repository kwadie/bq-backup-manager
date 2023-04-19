#https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/firestore_database

resource "google_project_service" "firestore" {
  project = var.project
  service = "firestore.googleapis.com"
}

resource "google_firestore_database" "datastore_mode_database" {
  project = var.project

  name = "(default)"

  location_id = var.region
  type        = "DATASTORE_MODE"

  depends_on = [google_project_service.firestore]
}