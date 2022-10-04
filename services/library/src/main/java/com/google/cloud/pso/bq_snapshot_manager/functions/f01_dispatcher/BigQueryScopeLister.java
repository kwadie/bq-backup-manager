package com.google.cloud.pso.bq_snapshot_manager.functions.f01_dispatcher;

import com.google.cloud.pso.bq_snapshot_manager.entities.NonRetryableApplicationException;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.helpers.LoggingHelper;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;
import com.google.cloud.pso.bq_snapshot_manager.services.scan.ResourceScanner;

import java.util.ArrayList;
import java.util.List;

public class BigQueryScopeLister {

    private final ResourceScanner resourceScanner;
    private LoggingHelper logger;
    private final String runId;

    public BigQueryScopeLister(ResourceScanner resourceScanner,
                               LoggingHelper logger,
                               String runId){
        this.resourceScanner = resourceScanner;
        this.logger = logger;
        this.runId = runId;
    }

    /**
     * List and filer down all tables that should be in scope based on BigQueryScope object
     * <p>
     * Detecting which resources to list is done bottom up TABLES > DATASETS > PROJECTS > FOLDERS
     * where lower levels configs (e.g. Tables) ignore higher level configs (e.g. Datasets)
     * For example:
     * If TABLES_INCLUDE list is provided:
     * * List only these tables
     * * SKIP tables in TABLES_EXCLUDE list
     * * IGNORE all other INCLUDE lists
     * If DATASETS_INCLUDE list is provided:
     * * List only tables in these datasets
     * * SKIP datasets in DATASETS_EXCLUDE
     * * SKIP tables in TABLES_EXCLUDE
     * * IGNORE all other INCLUDE lists
     * If PROJECTS_INCLUDE list is provided:
     * * List only datasets and tables in these projects
     * * SKIP datasets in DATASETS_EXCLUDE
     * * SKIP tables in TABLES_EXCLUDE
     * * IGNORE all other INCLUDE lists
     * If FOLDERS_INCLUDE list is provided:
     * * List only projects, datasets and tables in these folders
     * * SKIP projects in PROJECTS_EXCLUDE
     * * SKIP datasets in DATASETS_EXCLUDE
     * * SKIP tables in TABLES_EXCLUDE
     * * IGNORE all other INCLUDE lists
     *
     * @param bqScope
     * @return List<TableSpec>
     * @throws NonRetryableApplicationException
     */
    public List<TableSpec> listTablesInScope(BigQueryScope bqScope) throws NonRetryableApplicationException {

        List<TableSpec> tablesInScope;

        if (!bqScope.getTableIncludeList().isEmpty()) {
            tablesInScope = processTables(
                    bqScope.getTableIncludeList(),
                    bqScope.getTableExcludeList());
        } else {

            if (!bqScope.getDatasetIncludeList().isEmpty()) {
                tablesInScope = processDatasets(
                        bqScope.getDatasetIncludeList(),
                        bqScope.getDatasetExcludeList(),
                        bqScope.getTableExcludeList());
            } else {
                if (!bqScope.getProjectIncludeList().isEmpty()) {
                    tablesInScope = processProjects(
                            bqScope.getProjectIncludeList(),
                            bqScope.getProjectExcludeList(),
                            bqScope.getDatasetExcludeList(),
                            bqScope.getTableExcludeList());
                } else {
                    if (!bqScope.getFolderIncludeList().isEmpty()) {
                        tablesInScope = processFolders(
                                bqScope.getFolderIncludeList(),
                                bqScope.getProjectExcludeList(),
                                bqScope.getDatasetExcludeList(),
                                bqScope.getTableExcludeList());
                    } else {
                        throw new NonRetryableApplicationException("At least one of of the following params must be not empty [tableIncludeList, datasetIncludeList, projectIncludeList, folderIncludeList]");
                    }
                }
            }
        }

        return tablesInScope;
    }

    private List<TableSpec> processTables(List<String> tableIncludeList,
                                          List<String> tableExcludeList
    ) {
        List<TableSpec> output = new ArrayList<>();

        for (String table : tableIncludeList) {
            try {
                if (!tableExcludeList.contains(table)) {
                    output.add(TableSpec.fromSqlString(table));
                } else {
                    logger.logInfoWithTracker(runId, String.format("Table %s is excluded", table));
                }
            } catch (Exception ex) {
                // log and continue
                logger.logFailedDispatcherEntityId(runId, table, ex);
            }
        }
        return output;
    }

    private List<TableSpec> processDatasets(List<String> datasetIncludeList,
                                            List<String> datasetExcludeList,
                                            List<String> tableExcludeList
    ) {

        List<String> tablesIncludeList = new ArrayList<>();

        for (String dataset : datasetIncludeList) {

            try {

                if (!datasetExcludeList.contains(dataset)) {

                    List<String> tokens = Utils.tokenize(dataset, ".", true);
                    String projectId = tokens.get(0);
                    String datasetId = tokens.get(1);

                    // get all tables under dataset
                    List<String> datasetTables = resourceScanner.listTables(projectId, datasetId);
                    tablesIncludeList.addAll(datasetTables);

                    if (datasetTables.isEmpty()) {
                        String msg = String.format(
                                "No Tables found under dataset '%s'",
                                dataset);

                        logger.logWarnWithTracker(runId, msg);
                    } else {
                        logger.logInfoWithTracker(runId, String.format("Tables found in dataset %s : %s", dataset, datasetTables));
                    }
                } else {
                    logger.logInfoWithTracker(runId, String.format("Dataset %s is excluded", dataset));
                }
            } catch (Exception exception) {
                // log and continue
                logger.logFailedDispatcherEntityId(runId, dataset, exception);
            }
        }
        return processTables(tablesIncludeList, tableExcludeList);
    }


    private List<TableSpec> processProjects(
            List<String> projectIncludeList,
            List<String> projectExcludeList,
            List<String> datasetExcludeList,
            List<String> tableExcludeList
    ) {

        List<String> datasetIncludeList = new ArrayList<>();

        logger.logInfoWithTracker(runId, String.format("Will process projects %s", projectIncludeList));

        for (String project : projectIncludeList) {
            try {
                if (!projectExcludeList.contains(project)) {

                    logger.logInfoWithTracker(runId, String.format("Inspecting project %s", project));

                    // get all datasets in this project
                    List<String> projectDatasets = resourceScanner.listDatasets(project);
                    datasetIncludeList.addAll(projectDatasets);

                    if (projectDatasets.isEmpty()) {
                        String msg = String.format(
                                "No datasets found under project '%s' or no enough permissions to list BigQuery resources.",
                                project);

                        logger.logWarnWithTracker(runId, msg);
                    } else {

                        logger.logInfoWithTracker(runId, String.format("Datasets found in project %s : %s", project, projectDatasets));
                    }
                } else {
                    logger.logInfoWithTracker(runId, String.format("Project %s is excluded", project));
                }

            } catch (Exception exception) {
                // log and continue
                logger.logFailedDispatcherEntityId(runId, project, exception);
            }

        }
        return processDatasets(datasetIncludeList, datasetExcludeList, tableExcludeList);
    }

    private List<TableSpec> processFolders(
            List<Long> folderIncludeList,
            List<String> projectExcludeList,
            List<String> datasetExcludeList,
            List<String> tableExcludeList
    ) {

        List<String> projectIncludeList = new ArrayList<>();

        logger.logInfoWithTracker(runId, String.format("Will process folders %s", folderIncludeList));

        for (Long folder : folderIncludeList) {
            try {

                logger.logInfoWithTracker(runId, String.format("Inspecting folder %s", folder));

                // get all projects in this folder
                List<String> folderProjects = resourceScanner.listProjects(folder);
                projectIncludeList.addAll(folderProjects);

                if (folderProjects.isEmpty()) {
                    String msg = String.format(
                            "No projects found under folder '%s' or no enough permissions to list.",
                            folder);

                    logger.logWarnWithTracker(runId, msg);
                } else {

                    logger.logInfoWithTracker(runId, String.format("Projects found in folder %s : %s", folder, folderProjects));
                }

            } catch (Exception exception) {
                // log and continue
                logger.logFailedDispatcherEntityId(runId, folder.toString(), exception);
            }

        }
        return processProjects(projectIncludeList, projectExcludeList, datasetExcludeList, tableExcludeList);
    }

}