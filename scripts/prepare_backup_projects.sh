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

# run this script for all projects that are used to store backups

for project in "$@"
do

  echo "Preparing data project ${project} .."

  # BigQuery Snapshoter needs to create snapshot tables and manipulate them
  gcloud projects add-iam-policy-binding "${project}" \
     --member="serviceAccount:${SA_SNAPSHOTER_BQ_EMAIL}" \
     --role="roles/bigquery.dataEditor"

    # BigQuery Snapshoter needs to create snapshot jobs
  gcloud projects add-iam-policy-binding "${project}" \
     --member="serviceAccount:${SA_SNAPSHOTER_BQ_EMAIL}" \
     --role="roles/bigquery.jobUser"





  #TODO Add roles for Tagger

done
