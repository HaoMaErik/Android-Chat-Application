package com.example.erik.snapchat_team_nm;

/**
 * Created by Erik on 2016/10/15.
 */
public class StoriesData {

    String sendTime;
    String content;
    String messageFromName;
    String messageFromID;
    String type;

    public StoriesData(String sendTime, String content, String messageFromName, String messageFromID, String type) {
        this.sendTime = sendTime;
        this.content = content;
        this.messageFromName = messageFromName;
        this.messageFromID = messageFromID;
        this.type = type;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getContent() {
        return content;
    }

    public String getMessageFromName() {
        return messageFromName;
    }

    public String getMessageFromID() {
        return messageFromID;
    }

    public String getType() {
        return type;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessageFromName(String messageFromName) {
        this.messageFromName = messageFromName;
    }

    public void setMessageFromID(String messageFromID) {
        this.messageFromID = messageFromID;
    }

    public void setType(String type) {
        this.type = type;
    }

}

