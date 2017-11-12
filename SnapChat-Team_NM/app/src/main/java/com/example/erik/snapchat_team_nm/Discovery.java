package com.example.erik.snapchat_team_nm;

/**
 * Created by Erik on 2016/10/18.
 */
public class Discovery {
    String coverImageUrl;
    String name;
    String url;
    String type;

    public Discovery(String coverImageUrl, String name, String url,String type) {
        this.coverImageUrl = coverImageUrl;
        this.name = name;
        this.url = url;
        this.type = type;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
