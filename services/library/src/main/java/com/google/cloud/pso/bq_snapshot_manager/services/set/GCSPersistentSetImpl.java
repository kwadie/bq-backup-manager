package com.google.cloud.pso.bq_snapshot_manager.services.set;

import com.google.cloud.storage.*;

public class GCSPersistentSetImpl implements PersistentSet {

    private Storage storage;
    private String bucketName;

    public GCSPersistentSetImpl(String bucketName) {
        // Instantiates a client
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucketName = bucketName;
    }

    @Override
    public void add(String key) {
        BlobId blobId = BlobId.of(bucketName, key);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo);
    }

    public void remove(String key) {
        BlobId blobId = BlobId.of(bucketName, key);
        storage.delete(blobId);
    }

    @Override
    public boolean contains(String key) {
        BlobId blobId = BlobId.of(bucketName, key);
        Blob blob = storage.get(blobId);
        return blob != null;
    }
}
