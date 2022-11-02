WITH counts AS (
  SELECT
  run_id,
  timestamp,
  COUNT(DISTINCT tracking_id) AS total_count,
  SUM(CASE WHEN status = 'Success' THEN 1 ELSE 0 END) success_count,
  SUM(CASE WHEN status = 'Failed' THEN 1 ELSE 0 END) failed_count,
  SUM(CASE WHEN status = 'Retrying' THEN 1 ELSE 0 END) retrying_count,
  SUM(CASE WHEN status = 'In Progress' THEN 1 ELSE 0 END) in_progress_count,
  FROM ${project}.${dataset}.${v_run_summary}
  GROUP BY 1,2
)

SELECT
run_id,
timestamp,
total_count,
STRUCT(
  success_count + failed_count AS complete_count,
  retrying_count + in_progress_count AS incomplete_count,
  CASE WHEN total_count > 0 THEN (success_count + failed_count) / total_count ELSE null END AS completion_coverage
) AS progress,
STRUCT(
  success_count,
  failed_count,
  retrying_count,
  in_progress_count
) AS details,
STRUCT(
  (success_count + failed_count + retrying_count + in_progress_count) AS total_count,
  (success_count + failed_count + retrying_count + in_progress_count) - total_count AS variance
) AS cross_checkes
FROM counts
ORDER BY run_id DESC