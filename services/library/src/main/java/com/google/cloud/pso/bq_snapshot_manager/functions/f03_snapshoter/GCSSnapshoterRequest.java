package com.google.cloud.pso.bq_snapshot_manager.functions.f03_snapshoter;

import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.GCSSnapshotFormat;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableOperationRequest;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;

import java.util.Objects;

public class GCSSnapshoterRequest extends TableOperationRequest {

    private final String storagePath;
    private final GCSSnapshotFormat exportFormat;

    public GCSSnapshoterRequest(TableSpec targetTable, String runId, String trackingId, String storagePath, GCSSnapshotFormat exportFormat) {
        super(targetTable, runId, trackingId);
        this.storagePath = storagePath;
        this.exportFormat = exportFormat;
    }

    @Override
    public String toString() {
        return "GCSSnapshoterRequest{" +
                "storagePath='" + storagePath + '\'' +
                ", exportFormat=" + exportFormat +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GCSSnapshoterRequest that = (GCSSnapshoterRequest) o;
        return storagePath.equals(that.storagePath) &&
                exportFormat == that.exportFormat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(storagePath, exportFormat);
    }
}
