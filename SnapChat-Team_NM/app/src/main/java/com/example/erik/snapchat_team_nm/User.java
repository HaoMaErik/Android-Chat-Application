package com.example.erik.snapchat_team_nm;

/**
 * Created by Erik on 2016/10/1.
 */
public class User {
    String userName;
    String addDate;
    String userID;
    String location;

    public User(String userName, String addDate, String userID){
        this.userName = userName;
        this.addDate = addDate;
        this.userID = userID;
    }

    public User(String userName, String userID){
        this.userName = userName;
        this.userID = userID;
    }

    public String getAddDate() {
        return addDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
