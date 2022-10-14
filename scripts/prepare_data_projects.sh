#!/bin/bash

#
# Copyright 2022 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

for project in "$@"
do

  echo "Preparing data project ${project} .."

  # Dispatcher permissions
  # Dispatcher needs to list datasets and tables in a project and know the location of datasets
  gcloud projects add-iam-policy-binding "${project}" \
      --member="serviceAccount:${SA_DISPATCHER_EMAIL}" \
     --role="roles/bigquery.metadataViewer"

  # Configurator permissions
  gcloud projects add-iam-policy-binding "${project}" \
    --member="serviceAccount:${SA_CONFIGURATOR_EMAIL}" \
    --role="roles/bigquery.metadataViewer"

  gcloud projects add-iam-policy-binding "${project}" \
    --member="serviceAccount:${SA_CONFIGURATOR_EMAIL}" \
    --role="roles/datacatalog.viewer"

  # BigQuery Snapshoter needs to create snapshot jobs and read table data
  gcloud projects add-iam-policy-binding "${project}" \
     --member="serviceAccount:${SA_SNAPSHOTER_BQ_EMAIL}" \
     --role="roles/bigquery.jobUser"

  gcloud projects add-iam-policy-binding "${project}" \
     --member="serviceAccount:${SA_SNAPSHOTER_BQ_EMAIL}" \
     --role="roles/bigquery.dataViewer"




  #TODO Add roles for Tagger

done
