package com.google.cloud.pso.bq_snapshot_manager.services.catalog;


import com.google.cloud.datacatalog.v1.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.cloud.pso.bq_snapshot_manager.helpers.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataCatalogServiceImpl implements DataCatalogService {

    private final DataCatalogClient dataCatalogClient;

    public DataCatalogServiceImpl() throws IOException {
        dataCatalogClient = DataCatalogClient.create();
    }

    public void shutdown(){
        dataCatalogClient.shutdown();
    }

    public BackupPolicy getBackupPolicyTag(TableSpec tableSpec, String backupPolicyTagTemplateId) throws IOException, IllegalArgumentException {

        Map<String, TagField> tagTemplate = getTagTemplate(tableSpec, backupPolicyTagTemplateId);

        if(tagTemplate == null){
            // no backup tag template is attached to this table
            return null;
        }else{
            return Utils.parseBackupTagTemplateMap(
                    convertTagFieldMapToStrMap(tagTemplate)
            );
        }
    }

    public Map<String, TagField> getTagTemplate (TableSpec tableSpec, String backupPolicyTagTemplateId) throws IOException {

        LookupEntryRequest lookupEntryRequest =
                LookupEntryRequest.newBuilder()
                        .setLinkedResource(tableSpec.toDataCatalogLinkedResource()).build();

        // API Call
        Entry tableEntry = dataCatalogClient.lookupEntry(lookupEntryRequest);

        // API Call
        DataCatalogClient.ListTagsPagedResponse response = dataCatalogClient.listTags(tableEntry.getName());

        // TODO: handle multiple pages
        List<Tag> tags = response.getPage().getResponse().getTagsList();

        for (Tag tagTemplate: tags){
            if (tagTemplate.getTemplate().equals(backupPolicyTagTemplateId)){
                return tagTemplate.getFieldsMap();
            }
        }
        return null;
    }

    public static Map<String, String> convertTagFieldMapToStrMap(Map<String, TagField> tagFieldMap){

        Map<String, String> strMap = new HashMap<>(tagFieldMap.size());
        for(Map.Entry<String, TagField> entry: tagFieldMap.entrySet()){
            String strValue = "";
            if(entry.getValue().hasBoolValue()){
                strValue = String.valueOf(entry.getValue().getBoolValue());
            }
            if(entry.getValue().hasStringValue()){
                strValue = String.valueOf(entry.getValue().getStringValue());
            }
            if(entry.getValue().hasDoubleValue()){
                strValue = String.valueOf(entry.getValue().getDoubleValue());
            }
            if(entry.getValue().hasEnumValue()){
                strValue = String.valueOf(entry.getValue().getEnumValue().getDisplayName());
            }
            if(entry.getValue().hasTimestampValue()){
                strValue = String.valueOf(entry.getValue().getTimestampValue());
            }
            if(entry.getValue().hasRichtextValue()){
                strValue = String.valueOf(entry.getValue().getRichtextValue());
            }
            strMap.put(entry.getKey(), strValue);
        }
        return strMap;
    }

}
