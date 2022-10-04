variable "project" {}
variable "region" {}
variable "scheduler_name" {}
variable "target_uri" {}
variable "cron_expression" {}

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
variable "payload" {}
