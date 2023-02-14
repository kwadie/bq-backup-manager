package com.google.cloud.pso.bq_snapshot_manager.services.map;

import com.google.cloud.storage.*;

import java.nio.charset.StandardCharsets;

public class GcsPersistentMapImpl implements PersistentMap{

    private Storage storage;
    private String bucketName;

    public GcsPersistentMapImpl(String bucketName) {
        // Instantiates a client
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucketName = bucketName;
    }

    @Override
    public void put(String key, String value) {
        BlobId blobId = BlobId.of(bucketName, key);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        storage.create(blobInfo, value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String get(String key) {
        BlobId blobId = BlobId.of(bucketName, key);
        Blob blob = storage.get(blobId);
        byte [] content = blob.getContent();
        return new String(content, StandardCharsets.UTF_8);
    }

}
