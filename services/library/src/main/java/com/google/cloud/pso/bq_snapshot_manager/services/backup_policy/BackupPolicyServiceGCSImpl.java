package com.google.cloud.pso.bq_snapshot_manager.services.backup_policy;

import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.BackupPolicy;
import com.google.cloud.storage.*;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BackupPolicyServiceGCSImpl implements BackupPolicyService {


    private Storage storage;
    private String bucketName;

    public BackupPolicyServiceGCSImpl(String bucketName) {
        // Instantiates a client
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucketName = bucketName;
    }


    public void createOrUpdateBackupPolicyForTable(TableSpec tableSpec, BackupPolicy backupPolicy) throws IOException {

        String filePath = tableToGcsKey(tableSpec);
        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        byte[] content = backupPolicy.toJson().getBytes(StandardCharsets.UTF_8);
        storage.createFrom(blobInfo, new ByteArrayInputStream(content));
    }


    public @Nullable BackupPolicy getBackupPolicyForTable(TableSpec tableSpec) {
        String filePath = tableToGcsKey(tableSpec);
        BlobId blobId = BlobId.of(bucketName, filePath);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            return null;
        } else {
            byte[] contentBytes = storage.readAllBytes(blobId);
            String contentStr = new String(contentBytes, StandardCharsets.UTF_8);
            return BackupPolicy.fromJson(contentStr);
        }
    }

    @Override
    public void shutdown() {
        // do nothing
    }

    public String tableToGcsKey(TableSpec tableSpec) {
        return String.format("%s/%s" , tableSpec.toHivePartitionPostfix(), "backup_policy.json");
    }


}