variable "project" {}
variable "region" {}
variable "scheduler_name" {}
variable "target_uri" {}
variable "cron_expression" {}

# BigQuery Scope
# Fromat:
//# {
//tables_include_list = []
//datasets_include_list = []
//projects_include_list = []
//datasets_exclude_list = []
//tables_exclude_list = []
//}
variable "scope" {}
