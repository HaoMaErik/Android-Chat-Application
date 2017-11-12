package com.example.erik.snapchat_team_nm;

import java.io.Serializable;

/**
 * Created by Erik on 2016/10/15.
 */
public class Emoji{
    int imageUri;

    public Emoji(int imageUri){
        this.imageUri = imageUri;
    }

    public int getImageUri() {
        return imageUri;
    }

}
