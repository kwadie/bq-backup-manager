variable "project" {
  type = string
}
variable "region" {
  type = string
}
variable "scheduler_name" {
  type = string
}
variable "target_uri" {
  type = string
}
variable "cron_expression" {
  type = string
}

# BigQuery Scope
# Fromat:
//# {
//is_force_run = boolean
//folders_include_list = []
//projects_include_list = []
//projects_exclude_list = []
//datasets_include_list = []
//datasets_exclude_list = []
//tables_include_list = []
//tables_exclude_list = []
//}
variable "payload" {
  type = object({
    is_force_run = bool,
    folders_include_list = list(number),
    projects_include_list = list(string),
    projects_exclude_list = list(string),
    datasets_include_list = list(string),
    datasets_exclude_list = list(string),
    tables_include_list = list(string),
    tables_exclude_list = list(string),
  })
}
