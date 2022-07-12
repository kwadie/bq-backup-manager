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
export SA_SNAPSHOTER_EMAIL=snapshoter@${PROJECT_ID}.iam.gserviceaccount.com
export SA_TAGGER_EMAIL=tagger@${PROJECT_ID}.iam.gserviceaccount.com
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
