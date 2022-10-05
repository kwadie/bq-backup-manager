TODO: remove this file before publishing the solution


## Env setup
Export the account to access GCP with. This user account
must be able to impersonate the Terraform service account.
```
export ACCOUNT=<user account email>
```

Export these variables as-is
```
export PROJECT_ID=bqsm-host
export TF_SA=bq-snapshot-mgr-terraform
export COMPUTE_REGION=europe-west3
export DATA_REGION=eu
export BUCKET_NAME=${PROJECT_ID}-bq-snapshot-mgr
export BUCKET=gs://${BUCKET_NAME}
export DOCKER_REPO_NAME=docker-repo
export CONFIG=bqsm
export VARS=dev.tfvars

export SA_DISPATCHER_EMAIL=dispatcher@${PROJECT_ID}.iam.gserviceaccount.com
export SA_CONFIGURATOR_EMAIL=configurator@${PROJECT_ID}.iam.gserviceaccount.com
export SA_SNAPSHOTER_BQ_EMAIL=snapshoter-bq@${PROJECT_ID}.iam.gserviceaccount.com
export SA_SNAPSHOTER_GCS_EMAIL=snapshoter-gcs@${PROJECT_ID}.iam.gserviceaccount.com
export SA_TAGGER_EMAIL=tagger@${PROJECT_ID}.iam.gserviceaccount.com

export DISPATCHER_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-dispatcher-service:latest
export CONFIGURATOR_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-configurator-service:latest
export SNAPSHOTER_BQ_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-snapshoter-bq-service:latest
export SNAPSHOTER_GCS_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-snapshoter-gcs-service:latest
export TAGGER_IMAGE=${COMPUTE_REGION}-docker.pkg.dev/${PROJECT_ID}/${DOCKER_REPO_NAME}/bqsm-tagger-service:latest
```

If you're contributing to the project for the first time, create a gcloud
profile   
```
gcloud config configurations create $CONFIG
gcloud config set project $PROJECT_ID
gcloud config set account $ACCOUNT
gcloud config set compute/region $COMPUTE_REGION
```

If you created a gcloud profile before, just activate it
```
gcloud config configurations activate $CONFIG
```

Auth to GCP with the new profile
```
gcloud auth login
gcloud auth application-default login
```

## Deploying Services
After applying changes to the Java code under [Services](services)
one must re-deploy the code as container images to GCP run: 
```
./scripts/deploy_services.sh
```

## Deploying Terraform
After applying changes to the Terraform code under [Terraform](terraform)
or to re-deploy the Cloud Run services with the latest published container images run:  
```
./scripts/deploy_terraform.sh
``` 

## Deploying Full Stack
To deploy both Services and Terraform (e.g. to publish changes in Java code to Cloud Run)
```
./scripts/deploy_all.sh
```

## Prepare folders with required permissions
```
./scripts/prepare_data_folders.sh foldernumber1 foldernumber2
```

## Prepare data projects with required permissions
```
./scripts/prepare_data_projects.sh "project1" "project2"
```

## Linting

The repo is using Git Actions to lint the code using super-linter when creating pull requests.
This is to ensure the code base follows the Google guidelines for publishing.

However, one don't need to wait until creating a PR to run the linter, this could be done locally as well

* Pull the super-linter docker image
```
docker pull github/super-linter:latest
```
* Run super-linter using the same flags as in Git Actions
```
run
docker run \
-e RUN_LOCAL=true \
-e USE_FIND_ALGORITHM=true \
-e VALIDATE_GOOGLE_JAVA_FORMAT=true \
-e VALIDATE_ALL_CODEBASE=true \
-e VALIDATE_TERRAFORM_TFLINT=true \
-e VALIDATE_TERRAFORM_TERRASCAN=true \
-v $(pwd):/tmp/lint github/super-linter
```
