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

import com.google.cloud.Timestamp;

import java.util.UUID;

public class TrackingHelper {

    private static final String heartBeatRunSuffix = "-H";
    private static final Integer suffixLength = 2;

    public static String generateHeartBeatRunId(){
        return generateRunId(heartBeatRunSuffix);
    }

    private static String generateRunId(String suffix){
        return String.format("%s%s", System.currentTimeMillis(), suffix);
    }

    public static String parseRunIdAsPrefix(String runId){
        // currentTimeMillis() will always be 13 chars between Sep 9 2001 at 01:46:40.000 UTC and Nov 20 2286 at 17:46:39.999 UTC
        return runId.substring(0, (13 + suffixLength));
    }

    public static Long parseRunIdAsMilliSeconds(String runId){
        // currentTimeMillis() will always be 13 chars between Sep 9 2001 at 01:46:40.000 UTC and Nov 20 2286 at 17:46:39.999 UTC
        return Long.valueOf(runId.substring(0, 13));
    }

    public static Timestamp parseRunIdAsTimestamp(String runId){
        return Timestamp.ofTimeSecondsAndNanos(
                parseRunIdAsMilliSeconds(runId)/1000,
                0
        );
    }

    public static String generateTrackingId (String runId){
        return String.format("%s-%s", runId, UUID.randomUUID().toString());
    }
}
