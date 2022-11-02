WITH dispatched_tables AS
(
  SELECT DISTINCT
  jsonPayload.dispatched_tablespec_project AS tablespec_project,
  jsonPayload.dispatched_tablespec_dataset AS tablesspec_dataset,
  jsonPayload.dispatched_tablespec_table AS tablespec_table,
  jsonPayload.dispatched_tablespec AS tablespec,
  FROM `${project}.${dataset}.${logging_table}`
  WHERE jsonPayload.global_app_log = 'DISPATCHED_REQUESTS_LOG'
)
, configurator AS
(
  SELECT
  jsonPayload.unified_target_table AS tablespec,
  jsonPayload.unified_run_id AS run_id,
  jsonPayload.unified_tracking_id AS tracking_id,
  jsonPayload.unified_is_successful AS configurator_is_successful,
  jsonPayload.unified_error AS configurator_error,
  jsonPayload.unified_is_retryable_error AS configurator_is_retryable_error,
  CAST(JSON_VALUE(jsonPayload.unified_input_json, '$.isForceRun') AS BOOL) AS is_force_run,
  CAST(JSON_VALUE(jsonPayload.unified_output_json, '$.isBackupTime') AS BOOL) AS is_backup_time,
  timestamp AS configurator_log_ts
  FROM `${project}.${dataset}.${logging_table}`
  WHERE jsonPayload.global_app_log = 'UNIFIED_LOG'
  AND jsonPayload.unified_component = "2"
)
, bq_snapshoter AS
(
  SELECT
  jsonPayload.unified_tracking_id AS tracking_id,
  jsonPayload.unified_is_successful AS bq_snapshoter_is_successful,
  jsonPayload.unified_error AS bq_snapshoter_error,
  jsonPayload.unified_is_retryable_error AS bq_snapshoter_is_retryable_error
  FROM `${project}.${dataset}.${logging_table}`
  WHERE jsonPayload.global_app_log = 'UNIFIED_LOG'
  AND jsonPayload.unified_component = "3"
)
, tagger AS
(
  SELECT
  jsonPayload.unified_tracking_id AS tracking_id,
  jsonPayload.unified_is_successful AS taggerr_is_successful,
  jsonPayload.unified_error AS tagger_error,
  jsonPayload.unified_is_retryable_error AS tagger_is_retryable_error
  FROM `${project}.${dataset}.${logging_table}`
  WHERE jsonPayload.global_app_log = 'UNIFIED_LOG'
  AND jsonPayload.unified_component = "4"
)
, denormalized AS
(
SELECT
d.tablespec,
d.tablespec_project,
d.tablesspec_dataset,
d.tablespec_table,
RANK() OVER (PARTITION BY c.tablespec ORDER BY c.run_id DESC) AS run_rank,
c.run_id,
TIMESTAMP_MILLIS(CAST(SUBSTR(c.run_id, 0, 13) AS INT64)) AS run_start_ts,
c.configurator_log_ts,
c.tracking_id,
c.is_force_run,
c.is_backup_time,
CAST(c.configurator_is_successful AS BOOL) AS configurator_is_successful,
CAST(bs.bq_snapshoter_is_successful AS BOOL) AS bq_snapshoter_is_successful,
CAST(t.taggerr_is_successful AS BOOL) AS taggerr_is_successful,
c.configurator_error,
bs.bq_snapshoter_error,
t.tagger_error,
CAST(c.configurator_is_retryable_error AS BOOL) AS configurator_is_retryable_error,
CAST(bs.bq_snapshoter_is_retryable_error AS BOOL) AS bq_snapshoter_is_retryable_error,
CAST(t.tagger_is_retryable_error AS BOOL) AS tagger_is_retryable_error,

FROM dispatched_tables d
-- no tracking_id at this point, use the tablespec to get all runs for that tablespec
LEFT JOIN configurator c ON d.tablespec = c.tablespec
-- then join using tracking_id
LEFT JOIN bq_snapshoter bs ON c.tracking_id = bs.tracking_id
LEFT JOIN tagger t ON c.tracking_id = t.tracking_id
)

SELECT
    d.tablespec,
    STRUCT(d.tablespec_project, d.tablesspec_dataset, d.tablespec_table) AS table_info,
ARRAY_AGG (
    STRUCT(
    d.run_rank,
    d.run_id,
    d.configurator_log_ts AS execution_ts,
    d.run_start_ts,
    d.tracking_id,
    d.is_force_run,
    d.is_backup_time,
    -- run is considered successful when all steps complete successfully or if the configurator completes successfully but it's not backup time for this table
    (d.configurator_is_successful AND d.bq_snapshoter_is_successful AND d.taggerr_is_successful) OR (d.configurator_is_successful AND NOT d.is_backup_time) AS is_successful_run,
    COALESCE(configurator_error, bq_snapshoter_error, tagger_error) AS run_error,
    (COALESCE(d.configurator_is_retryable_error, FALSE) OR COALESCE(d.bq_snapshoter_is_retryable_error, FALSE) OR COALESCE(d.tagger_is_retryable_error, FALSE)) AS run_has_retryable_error,
    CASE
      WHEN d.configurator_error IS NOT NULL THEN 'configurator'
      WHEN d.bq_snapshoter_error IS NOT NULL THEN 'bigquery_snapshoter'
      WHEN d.tagger_error IS NOT NULL THEN 'tagger'
    END AS failed_component,
    STRUCT(d.configurator_is_successful,d.bq_snapshoter_is_successful,d.taggerr_is_successful) AS components_is_successful_details,
    STRUCT(d.configurator_error,d.bq_snapshoter_error,d.tagger_error) AS components_error_details,
    STRUCT(d.configurator_is_retryable_error,d.bq_snapshoter_is_retryable_error,d.tagger_is_retryable_error) AS components_is_retryable_error_details
  ) ORDER BY run_rank ASC, configurator_log_ts DESC) AS runs

FROM denormalized d
GROUP BY d.tablespec, d.tablespec_project, d.tablesspec_dataset, d.tablespec_table







