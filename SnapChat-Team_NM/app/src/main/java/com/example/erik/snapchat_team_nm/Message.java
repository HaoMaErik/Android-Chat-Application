package com.example.erik.snapchat_team_nm;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erik on 2016/10/4.
 */
public class Message {

    String sendTime;
    String content;
    String messageFromName;
    String messageFromID;
    String type;
    public Map<String, Boolean> stars = new HashMap<>();

    public Message(String sendTime, String content,String messageFromName, String messageFromID, String type){

        this.sendTime = sendTime;
        this.content = content;
        this.messageFromName = messageFromName;
        this.messageFromID = messageFromID;
        this.type = type;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("from", messageFromID);
        result.put("namefrom", messageFromName);
        result.put("content", content);
        result.put("sendTime", sendTime);
        result.put("type", type);

        return result;
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
