# BigQuery Snapshot Manager

## Overview

BigQuery Snapshot Manager is an OSS solution that enables you to define
a flexible and centralized backup strategy to all of your BigQuery projects, datasets and tables
while automating the frequent snapshot operations at scale

![alt text](diagrams/architecture.jpeg)


## Deployment

### Install Maven
* Download [Maven](https://maven.apache.org/download.cgi)
* Add Maven to PATH
```
export PATH=/DOWNLOADED_MAVEN_DIR/bin:$PATH
```
### Setup Environment Variables

In a terminal shell, set and export the following variables.

```
export PROJECT_ID=<host project id>
export TF_SA=bq-snapshot-mgr-terraform
export COMPUTE_REGION=<region to deploy compute resources>
export DATA_REGION=<region to deploy data resources>
export BUCKET_NAME=${PROJECT_ID}-bq-snapshot-mgr
export BUCKET=gs://${BUCKET_NAME}
export DOCKER_REPO_NAME=docker-repo
export CONFIG=bqsm
export ACCOUNT=<user account email>

gcloud config configurations create $CONFIG
gcloud config set project $PROJECT_ID
gcloud config set account $ACCOUNT
gcloud config set compute/region $COMPUTE_REGION

gcloud auth login
gcloud auth application-default login
```

### One-time Environment Setup

#### Enable GCP APIs

```
./scripts/enable_gcp_apis.sh
```

#### Prepare Terraform State Bucket

```
gsutil mb -p $PROJECT_ID -l $COMPUTE_REGION -b on $BUCKET
```

#### Prepare Terraform Service Account

Terraform needs to run with a service account to deploy DLP resources. User accounts are not enough.  

```
./scripts/prepare_terraform_service_account.sh
```

#### Prepare a Docker Repo

We need a Docker Repository to publish images that are used by this solution

```
gcloud artifacts repositories create $DOCKER_REPO_NAME --repository-format=docker \
--location=$COMPUTE_REGION --description="Docker repository"
```

### Solution Deployment

#### gcloud
```
gcloud config configurations activate $CONFIG

gcloud auth login
gcloud auth application-default login 
```
#### Build Cloud Run Services Images

We need to build and deploy docker images to be used by the Cloud Run service.

```
export DISPATCHER_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-dispatcher-service:latest
export SNAPSHOTER_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-snapshoter-service:latest
export TAGGER_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-tagger-service:latest

./scripts/deploy_services.sh
```

#### Terraform Variables Configuration

The solution is deployed by Terraform and thus all configurations are done
on the Terraform side.

##### Create a Terraform .tfvars file

Create a new .tfvars file and override the variables in the below sections. You can use one of the example
tfavrs files as a base [example-variables](terraform/example-variables.tfvars). 

```
export VARS=my-variables.tfvars
```

##### Configure Basic Variables

Most required variables have default names defined in [variables.tf](terraform/variables.tf).
You can use the defaults or overwrite them in the .tfvars file you just created.

Both ways, you must define the below variables:

```
project = "<GCP project ID to deploy solution to>"
compute_region = "<GCP region to deploy compute resources e.g. cloud run, iam, etc>"
data_region = "<GCP region to deploy data resources (buckets, datasets, tag templates, etc">
```

##### Configure BigQuery Dataset  

This dataset will be created under the data_region and will
hold all solution-managed tables and config views. Optionally, it could 
be used to store Auto DLP findings (configured outside of Terraform)

```
bigquery_dataset_name = "<>"
```

##### Configure DryRun

By setting `is_dry_run = "True"` the solution will list down all BigQuery tables
included in the scan scope but it will not trigger table-level operations
on it (i.e. snapshoting, tagging). Instead, structured log messages will be written to BigQuery for 
monitoring and inspection.

```
is_dry_run = "False"
```

##### Configure Cloud Scheduler Service Account

We will need to grant the Cloud Scheduler account permissions to use parts of the solution 

```
cloud_scheduler_account = "service-<project number>@gcp-sa-cloudscheduler.iam.gserviceaccount.com"
```

If this host project never used Cloud Scheduler before, create and run a sample job to force GCP to create the service account.

PS: project number is different from project id/name. You can find both info on the home page of any project.

##### Configure Terraform Service Account

Terraform needs to run with a service account to deploy DLP resources. User accounts are not enough.  

This service account is created in a previous step of the deployment. Use the full email of the created account.
```
terraform_service_account = "bq-snapshot-mgr-terraform@<host project>.iam.gserviceaccount.com"
```

##### Configure Cloud Run Service Images

Earlier, we used Docker to build container images that will be used by the solution.
In this step, we instruct Terraform to use these published images in the Cloud Run services
that Terraform will create. 

PS: Terraform will just "link" a Cloud Run to an existing image. It will not build the images from the code base (this 
is already done in a previous step)

```
dispatcher_service_image = "< value of env variable DISPATCHER_IMAGE >"
snapshoter_service_image = "< value of env variable SNAPSHOTER_IMAGE >"
tagger_service_image = "< value of env variable TAGGER_IMAGE >"
``` 

#### Terraform Deployment

```
cd terraform

terraform init \
    -backend-config="bucket=${BUCKET_NAME}" \
    -backend-config="prefix=terraform-state" \
    -backend-config="impersonate_service_account=$TF_SA@$PROJECT_ID.iam.gserviceaccount.com"

terraform workspace new $CONFIG
# or, if it's not the first deployment
terraform workspace select $CONFIG

terraform plan -var-file=$VARS

terraform apply -var-file=$VARS -auto-approve

```

#### Setup access to data projects

The application is deployed under a host project as set in the `PROJECT_ID` variable.
To enable the application to take snapshots of tables in other projects (i.e. data projects) one must grant a number of
permissions on each data project. To do, run the following script:

Set the following variables that will be used in next steps:

```
export SA_DISPATCHER_EMAIL=dispatcher@${PROJECT_ID}.iam.gserviceaccount.com
export SA_SNAPSHOTER_EMAIL=snapshoter@${PROJECT_ID}.iam.gserviceaccount.com
export SA_TAGGER_EMAIL=tagger@${PROJECT_ID}.iam.gserviceaccount.com

./scripts/prepare_data_projects.sh <project1> <project2> <etc>
```

PS: 
* Update the SA emails if the default names have been changed in Terraform  
* If you have tables to be backed-up in the host project, run the above script and include the host project in the list
* Use the same projects list as set in the Terraform variable `schedulers`


  