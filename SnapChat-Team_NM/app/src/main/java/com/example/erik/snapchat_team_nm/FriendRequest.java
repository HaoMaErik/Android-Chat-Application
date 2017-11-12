package com.example.erik.snapchat_team_nm;

/**
 * Created by Erik on 2016/10/9.
 */
public class FriendRequest {
    String requestID;
    String requestName;

    public FriendRequest(String requestID, String requestName) {
        this.requestID = requestID;
        this.requestName = requestName;
    }

    public String getRequestID() {
        return requestID;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }
}
