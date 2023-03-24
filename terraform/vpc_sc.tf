module "vpc" {
  source = "./modules/vpc"

  network_name = "backup-manager-vpc"
  project_id   = var.project
  description  = "bq-backup-manager VPC network"
  routing_mode = "REGIONAL"
}

module "subnet" {
  source = "./modules/subnets"

  subnet_name           = "backup-manager-subnet"
  subnet_ip             = "10.0.0.0/28"
  subnet_region         = var.compute_region
  subnet_private_access = true
  network_name          = module.vpc.network_name
  project_id            = var.project
  description           = "bq-backup-manager subnet"
}

module "egress_deny_all" {
  source = "./modules/firewall-rules"

  name         = "backup-manager-egress-deny-all"
  description  = "Deny all egress traffic"
  direction    = "EGRESS"
  network_name = module.vpc.network_name
  project_id   = var.project
  ranges       = ["0.0.0.0/0"]
  deny = [{
    protocol = "all"
  }]
}

module "egress_allow_restricted" {
  source = "./modules/firewall-rules"

  name         = "backup-manager-egress-allow-restricted"
  description  = "Allow egress traffic only from restricted apis"
  direction    = "EGRESS"
  network_name = module.vpc.network_name
  project_id   = var.project
  ranges       = ["199.36.153.4/30"]
  priority     = 999
  allow = [{
    protocol = "tcp",
    ports    = ["443"]
  }]
}

module "googleapis_private_dns" {
  source = "./modules/cloud-dns"

  type                               = "private"
  project_id                         = var.project
  name                               = "backup-manager-googleapis-dns"
  domain                             = "googleapis.com."
  private_visibility_config_networks = [module.vpc.network_self_link]

  recordsets = [
    {
      name = "*"
      type = "CNAME"
      ttl  = 300
      records = [
        "restricted.googleapis.com."
      ]
    },
    {
      name = "restricted"
      type = "A"
      ttl  = 300
      records = [
        "199.36.153.4",
        "199.36.153.5",
        "199.36.153.6",
        "199.36.153.7"
      ]
    }
  ]
}

module "cloud_run_private_dns" {
  source = "./modules/cloud-dns"

  type                               = "private"
  project_id                         = var.project
  name                               = "backup-manager-cloud-run-dns"
  domain                             = "run.app."
  description                        = "none"
  private_visibility_config_networks = [module.vpc.network_self_link]

  recordsets = [
    {
      name = "*"
      type = "A"
      ttl  = 300
      records = [
        "199.36.153.4",
        "199.36.153.5",
        "199.36.153.6",
        "199.36.153.7"
      ]
    }
  ]
  depends_on = [module.googleapis_private_dns]
}

module "vpc_connector" {
  source = "./modules/vpc-serverless-connector"

  project_id     = var.project
  name           = "backup-manager-svpc-conn"
  region         = var.compute_region
  subnet_name    = module.subnet.subnet_name
  machine_type   = "e2-micro"
  min_throughput = 200
  max_throughput = 1000
}