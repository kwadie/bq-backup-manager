variable "project_id" {
  type        = string
  description = "Project in which the vpc connector will be deployed."
}

variable "name" {
  type        = string
  description = "The name of the resource."

  validation {
    condition     = length(var.name) <= 25
    error_message = "The name must be less than 25 characters."
  }
}

variable "region" {
  type        = string
  description = "Region where the VPC Access connector resides."
}

variable "ip_cidr_range" {
  type        = string
  description = "The range of internal addresses that follows RFC 4632 notation."
  default     = null
}

variable "network" {
  type        = string
  description = "Name or self_link of the VPC network."
  default     = null
}

variable "machine_type" {
  type        = string
  description = "Machine type of VM Instance underlying connector."
  default     = "e2-micro"
}

variable "min_instances" {
  type        = number
  description = "Minimum value of instances in autoscaling group underlying the connector."
  default     = null
}

variable "max_instances" {
  type        = number
  description = "Maximum value of instances in autoscaling group underlying the connector."
  default     = null
}

variable "min_throughput" {
  type        = string
  description = "Minimum throughput of the connector in Mbps. Default and min is 200."
  default     = "200"
}

variable "max_throughput" {
  type        = string
  description = "Maximum throughput of the connector in Mbps, must be greater than min_throughput. Default is 300."
  default     = "300"
}

variable "subnet_name" {
  type        = string
  description = "Subnet name."
}

variable "host_project_id" {
  type        = string
  description = "Project in which the subnet exists."
  default     = null
}