package com.google.cloud.pso.bq_snapshot_manager.services.catalog;


import com.google.cloud.datacatalog.v1.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;
import com.google.protobuf.FieldMask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataCatalogServiceImpl implements DataCatalogService {

    private final DataCatalogClient dataCatalogClient;

    public DataCatalogServiceImpl() throws IOException {
        dataCatalogClient = DataCatalogClient.create();
    }

    public void shutdown(){
        dataCatalogClient.shutdown();
    }

    public Tag createOrUpdateBackupPolicyTag(TableSpec tableSpec, BackupPolicy backupPolicy, String backupPolicyTagTemplateId){

        // API Call
        String parent = getBigQueryEntryName(tableSpec);

        // API CALL
        DataCatalogClient.ListTagsPagedResponse response = dataCatalogClient.listTags(parent);

        // TODO: handle multiple pages
        List<Tag> allTags = response.getPage().getResponse().getTagsList();

        Tag tag = findTag(
                allTags,
                backupPolicyTagTemplateId
        );

        if(tag == null){
            // create a new tag
            return dataCatalogClient.createTag(parent, backupPolicy.toDataCatalogTag(backupPolicyTagTemplateId, null));
        }else{
            // update existing tag referencing the existing tag.name
            return  dataCatalogClient.updateTag(
                    backupPolicy.toDataCatalogTag(
                            backupPolicyTagTemplateId,
                            tag.getName()
                    )
            );
        }
    }

    public Tag findTag(List<Tag> tags, String tagTemplateName){

        List<Tag> foundTags = tags.stream().filter(t -> t.getTemplate().equals(tagTemplateName))
                .collect(Collectors.toList());

        // if more than one tag is found use the first one
        return foundTags.size() >= 1? foundTags.get(0): null;
    }


    public void updateBackupPolicyTag(TableSpec tableSpec, BackupPolicy backupPolicy, String backupPolicyTagTemplateId){

        // API Call
        Tag tag = getTag(tableSpec, backupPolicyTagTemplateId);

        // convert the backup policy to a data catalog Tag and link it to the existing tag by name
        Tag policyTag = backupPolicy.toDataCatalogTag(backupPolicyTagTemplateId, tag.getName());

        // API Call
        dataCatalogClient.updateTag(policyTag);
    }

    public void createBackupPolicyTag(TableSpec tableSpec, BackupPolicy backupPolicy, String backupPolicyTagTemplateId){

        // API Call
        String parent = getBigQueryEntryName(tableSpec);

        // API Call
        Tag tag = dataCatalogClient.createTag(parent, backupPolicy.toDataCatalogTag(backupPolicyTagTemplateId, null));
    }

    /**
     * Return the attached backup policy tag template or null if no template is attached
     * @param tableSpec
     * @param backupPolicyTagTemplateId
     * @return
     * @throws IllegalArgumentException
     */
    public BackupPolicy getBackupPolicyTag(TableSpec tableSpec, String backupPolicyTagTemplateId) throws IllegalArgumentException {

        Map<String, TagField> tagTemplate = getTagFieldsMap(tableSpec, backupPolicyTagTemplateId);

        if(tagTemplate == null){
            // no backup tag template is attached to this table
            return null;
        }else{
            return BackupPolicy.fromMap(convertTagFieldMapToStrMap(tagTemplate), false);
        }
    }

    public Tag getTag(TableSpec tableSpec, String templateId){
        // API Call
        String parent = getBigQueryEntryName(tableSpec);
        // API CALL
        DataCatalogClient.ListTagsPagedResponse response = dataCatalogClient.listTags(parent);

        // TODO: handle multiple pages
        List<Tag> tags = response.getPage().getResponse().getTagsList();

        for (Tag tagTemplate: tags){
            if (tagTemplate.getTemplate().equals(templateId)){
                return tagTemplate;
            }
        }
        return null;
    }

    public Map<String, TagField> getTagFieldsMap(TableSpec tableSpec, String templateId) {

        Tag tag = getTag(tableSpec, templateId);
        return tag == null? null: tag.getFieldsMap();
    }

    public String getBigQueryEntryName(TableSpec tableSpec){
        LookupEntryRequest lookupEntryRequest =
                LookupEntryRequest.newBuilder()
                        .setLinkedResource(tableSpec.toDataCatalogLinkedResource()).build();

        // API Call
        return dataCatalogClient.lookupEntry(lookupEntryRequest).getName();
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
