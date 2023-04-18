package com.google.cloud.pso.bq_snapshot_manager.services.backup_policy;



import com.google.cloud.datastore.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BackupPolicyServiceFireStoreImpl implements BackupPolicyService {


    private static final String KIND = "BigQueryBackupPolicy";
    private Datastore datastore;

    public BackupPolicyServiceFireStoreImpl() {
        datastore = DatastoreOptions.getDefaultInstance().getService();
    }


    public void createOrUpdateBackupPolicyForTable(TableSpec tableSpec, BackupPolicy backupPolicy) {

        Key backupPolicyKey = datastore.newKeyFactory().setKind(KIND).newKey(tableSpec.toSqlString());
        Entity backupPolicyEntity = backupPolicy.toFireStoreEntity(backupPolicyKey);
        datastore.put(backupPolicyEntity);
    }


    public @Nullable BackupPolicy getBackupPolicyForTable(TableSpec tableSpec) {
        Key backupPolicyKey = datastore.newKeyFactory().setKind(KIND).newKey(tableSpec.toSqlString());
        Entity backupPolicyEntity = datastore.get(backupPolicyKey);
        if(backupPolicyEntity == null){
            // table doesn't have a backup policy stored
            return null;
        }else{
            Map<String, String> propertiesMap = entityToStrMap(backupPolicyEntity);
            return BackupPolicy.fromMap(propertiesMap);
        }
    }

    @Override
    public void shutdown() {
        // do nothing
    }

    public Map<String, String> entityToStrMap (Entity entity){
        Map<String, String> strMap = new HashMap<>(entity.getProperties().size());

        for (String key: entity.getProperties().keySet()) {
            strMap.put(key, entity.getValue(key).get().toString());
        }
        return strMap;
    }


}
