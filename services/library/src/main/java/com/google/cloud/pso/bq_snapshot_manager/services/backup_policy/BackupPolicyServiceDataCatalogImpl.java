package com.google.cloud.pso.bq_snapshot_manager.services.backup_policy;


import com.google.cloud.Timestamp;
import com.google.cloud.datacatalog.v1.*;
import com.google.cloud.pso.bq_snapshot_manager.entities.TableSpec;
import com.google.cloud.pso.bq_snapshot_manager.entities.backup_policy.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BackupPolicyServiceDataCatalogImpl implements BackupPolicyService {

    private final DataCatalogClient dataCatalogClient;
    private String backupPolicyTagTemplateId;

    public BackupPolicyServiceDataCatalogImpl(String backupPolicyTagTemplateId) throws IOException {
        dataCatalogClient = DataCatalogClient.create();
        this.backupPolicyTagTemplateId = backupPolicyTagTemplateId;
    }

    public void shutdown() {
        dataCatalogClient.shutdown();
    }

    public void createOrUpdateBackupPolicyForTable(TableSpec tableSpec, BackupPolicy backupPolicy) {

        // API Call
        String parent = getBigQueryEntryName(tableSpec);

        // API CALL
        DataCatalogClient.ListTagsPagedResponse response = dataCatalogClient.listTags(parent);

        List<Tag> allTags = new ArrayList<>();
        for (DataCatalogClient.ListTagsPage l : response.iteratePages()) {
            allTags.addAll(l.getResponse().getTagsList());
        }

        Tag tag = findTag(
                allTags,
                backupPolicyTagTemplateId
        );

        if (tag == null) {
            // create a new tag
            dataCatalogClient.createTag(parent, backupPolicy.toDataCatalogTag(backupPolicyTagTemplateId, null));
        } else {
            // update existing tag referencing the existing tag.name
            dataCatalogClient.updateTag(
                    backupPolicy.toDataCatalogTag(
                            backupPolicyTagTemplateId,
                            tag.getName()
                    )
            );
        }
    }

    public Tag findTag(List<Tag> tags, String tagTemplateName) {

        List<Tag> foundTags = tags.stream().filter(t -> t.getTemplate().equals(tagTemplateName))
                .collect(Collectors.toList());

        // if more than one tag is found use the first one
        return foundTags.size() >= 1 ? foundTags.get(0) : null;
    }


    /**
     * Return the attached backup policy tag template or null if no template is attached
     *
     * @param tableSpec
     * @return
     * @throws IllegalArgumentException
     */
    public @Nullable BackupPolicy getBackupPolicyForTable(TableSpec tableSpec) throws IllegalArgumentException {

        Map<String, TagField> tagTemplate = getTagFieldsMap(tableSpec, backupPolicyTagTemplateId);

        if (tagTemplate == null) {
            // no backup tag template is attached to this table
            return null;
        } else {
            return BackupPolicy.fromMap(convertTagFieldMapToStrMap(tagTemplate));
        }
    }

    public Tag getTag(TableSpec tableSpec, String templateId) {
        // API Call
        String parent = getBigQueryEntryName(tableSpec);
        // API CALL
        DataCatalogClient.ListTagsPagedResponse response = dataCatalogClient.listTags(parent);

        // TODO: handle multiple pages
        List<Tag> tags = response.getPage().getResponse().getTagsList();

        for (Tag tagTemplate : tags) {
            if (tagTemplate.getTemplate().equals(templateId)) {
                return tagTemplate;
            }
        }
        return null;
    }

    public Map<String, TagField> getTagFieldsMap(TableSpec tableSpec, String templateId) {

        Tag tag = getTag(tableSpec, templateId);
        return tag == null ? null : tag.getFieldsMap();
    }

    public String getBigQueryEntryName(TableSpec tableSpec) {
        LookupEntryRequest lookupEntryRequest =
                LookupEntryRequest.newBuilder()
                        .setLinkedResource(tableSpec.toDataCatalogLinkedResource()).build();

        // API Call
        return dataCatalogClient.lookupEntry(lookupEntryRequest).getName();
    }

    public static Map<String, String> convertTagFieldMapToStrMap(Map<String, TagField> tagFieldMap) {

        Map<String, String> strMap = new HashMap<>(tagFieldMap.size());
        for (Map.Entry<String, TagField> entry : tagFieldMap.entrySet()) {
            String strValue = "";
            if (entry.getValue().hasBoolValue()) {
                strValue = String.valueOf(entry.getValue().getBoolValue());
            }
            if (entry.getValue().hasStringValue()) {
                strValue = entry.getValue().getStringValue();
            }
            if (entry.getValue().hasDoubleValue()) {
                strValue = String.valueOf(entry.getValue().getDoubleValue());
            }
            if (entry.getValue().hasEnumValue()) {
                strValue = entry.getValue().getEnumValue().getDisplayName();
            }
            if (entry.getValue().hasTimestampValue()) {
                strValue = Timestamp.ofTimeSecondsAndNanos(
                        entry.getValue().getTimestampValue().getSeconds(),
                        entry.getValue().getTimestampValue().getNanos()
                ).toString();
            }
            if (entry.getValue().hasRichtextValue()) {
                strValue = entry.getValue().getRichtextValue();
            }
            strMap.put(entry.getKey(), strValue);
        }
        return strMap;
    }


}