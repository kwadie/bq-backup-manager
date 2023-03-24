variable "project" { type = string }
variable "region" { type = string }

variable "service_name" { type = string }
variable "service_image" { type = string }
variable "service_account_email" { type = string }
variable "invoker_service_account_email" { type = string }

variable "environment_variables" {
  type = list(map(string))
}

variable "vpc_access_connector" {
  type = string
}

variable "max_memory" {
  type    = string
  default = "1Gi"
}

variable "max_cpu" {
  type    = string
  default = "1"
}

variable "max_containers" {
  type    = number
  default = 10
}

variable "max_requests_per_container" {
  type    = number
  default = 80
}

variable "timeout_seconds" { type = number }

variable "common_labels" {
  type = map(string)
}