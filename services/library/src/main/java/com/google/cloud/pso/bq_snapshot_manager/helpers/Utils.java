/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pso.bq_snapshot_manager.helpers;

import com.google.cloud.datacatalog.v1.TagField;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Utils {

    private static String getOrFail(Map<String, String> map, String key) {
        String field = map.get(key);
        if (field == null) {
            throw new IllegalArgumentException(String.format(
                    "Key '%s' is not found in Map.",
                    key
            ));
        } else {
            return field;
        }
    }

    public static BackupPolicy parseBackupTagTemplateMap(Map<String, String> tagTemplate) throws IllegalArgumentException {

        // parse common settings
        String cron = getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_cron.toString());

        BackupMethod method = BackupMethod.fromString(
                getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_method.toString())
        );

        BackupConfigSource configSource = BackupConfigSource.fromString(
                getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.config_source.toString())
        );

        TimeTravelOffsetDays timeTravelOffsetDays = TimeTravelOffsetDays.fromString(
                getOrFail(tagTemplate, DataCatalogBackupPolicyTagFields.backup_time_travel_offset_days.toString())
        );

        // parse BQ snapshot settings
        String bqSnapshotStorageProject = getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.bq_snapshot_storage_project.toString());
        String bqSnapshotStorageDataset = getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.bq_snapshot_storage_dataset.toString());
        String bqSnapshotExpirationDays = getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.bq_snapshot_expiration_days.toString());
        String gcsSnapshotStorageLocation = getOrFail(tagTemplate,
                DataCatalogBackupPolicyTagFields.gcs_snapshot_storage_location.toString());

        return new BackupPolicy(
                cron,
                method,
                timeTravelOffsetDays,
                Double.valueOf(bqSnapshotExpirationDays),
                bqSnapshotStorageProject,
                bqSnapshotStorageDataset,
                gcsSnapshotStorageLocation,
                configSource
        );
    }

    public static List<String> tokenize(String input, String delimiter, boolean required) {
        List<String> output = new ArrayList<>();

        if(input.isBlank() && required){
            throw new IllegalArgumentException(String.format(
                    "Input string '%s' is blank.",
                    input
            ));
        }

        if(input.isBlank() && !required){
            return output;
        }

        StringTokenizer tokens = new StringTokenizer(input, delimiter);
        while (tokens.hasMoreTokens()) {
            output.add(tokens.nextToken().trim());
        }
        if (required && output.size() == 0) {
            throw new IllegalArgumentException(String.format(
                    "No tokens found in string: '%s' using delimiter '%s'",
                    input,
                    delimiter
            ));
        }
        return output;
    }

    public static String getConfigFromEnv(String config, boolean required){
        String value = System.getenv().getOrDefault(config, "");

        if(required && value.isBlank()){
            throw new IllegalArgumentException(String.format("Missing environment variable '%s'",config));
        }

        return value;
    }

    /**
     *
     * @param policyTagId e.g. projects/<project>/locations/<location>/taxonomies/<taxonomyID>/policyTags/<policyTagID
     * @return e.g. projects/<project>/locations/<location>/taxonomies/<taxonomyID>
     */
    public static String extractTaxonomyIdFromPolicyTagId(String policyTagId){

        List<String> tokens = tokenize(policyTagId, "/", true);
        int taxonomiesIndex = tokens.indexOf("taxonomies");
        return String.join("/", tokens.subList(0,taxonomiesIndex+2));
    }

    public static String getArgFromJsonParams(JsonObject requestJson, String argName, boolean required) {

        String arg = "";

        // check in Json
        if (requestJson != null && requestJson.has(argName)) {
            arg = requestJson.get(argName).getAsString();
        }

        // validate it exists
        if(required) {
            if (arg.isBlank())
                throw new IllegalArgumentException(String.format("%s is required", argName));
        }

        return arg;
    }

    public static List<String> getArgFromJsonParamsAsList(JsonObject requestJson, String argName, boolean required) {

        JsonArray  jsonArray = new JsonArray();

        // check in Json
        if (requestJson != null && requestJson.has(argName)) {
            jsonArray = requestJson.get(argName).getAsJsonArray();
        }

        // validate it exists
        if(required) {
            if (jsonArray.size() == 0)
                throw new IllegalArgumentException(String.format("%s is required", argName));
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return new Gson().fromJson(jsonArray, listType);
    }

}
