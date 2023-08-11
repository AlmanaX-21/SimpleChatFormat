package me.almana.simplechatformat.utils;

public class ChatModelClass {

    public ChatModelClass(String luckPermsGroup, String messageFormat) {
        this.luckPermsGroup = luckPermsGroup;
        this.messageFormat = messageFormat;
    }

    private String luckPermsGroup;
    private String messageFormat;

    public String getLuckPermsGroup() {
        return luckPermsGroup;
    }

    public void setLuckPermsGroup(String luckPermsGroup) {
        this.luckPermsGroup = luckPermsGroup;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }
}
