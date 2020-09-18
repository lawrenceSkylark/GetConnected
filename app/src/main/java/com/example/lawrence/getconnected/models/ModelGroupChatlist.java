package com.example.lawrence.getconnected.models;

public class ModelGroupChatlist {

    String groupId,groupTitle,groupDescriptions,groupIcon,timestamp,
    CreatedBy;

    public ModelGroupChatlist(String groupId, String groupTitle, String groupDescriptions, String groupIcon, String timestamp, String createdBy) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDescriptions = groupDescriptions;
        this.groupIcon = groupIcon;
        this.timestamp = timestamp;
        CreatedBy = createdBy;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getGroupDescriptions() {
        return groupDescriptions;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public ModelGroupChatlist() {

    }
}
