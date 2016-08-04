package ua.mintmalory.githubfeed.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfo implements Serializable {
    @SerializedName("login")
    private String mUserLogin;

    @SerializedName("avatar_url")
    private String mUserAvatarUrl;

    @SerializedName("bio")
    private String mUserBiographyInfo;

    public String getUserLogin() {
        return mUserLogin;
    }

    public void setUserLogin(String newUserLogin) {
        mUserLogin = newUserLogin;
    }

    public String getUserAvatarUrl() {
        return mUserAvatarUrl;
    }

    public void setUserAvatarUrl(String newUserAvatarUrl) {
        mUserAvatarUrl = newUserAvatarUrl;
    }

    public String getUserBiographyInfo() {
        return mUserBiographyInfo;
    }

    public void setUserBiographyInfo(String newUserBiographyInfo) {
        mUserBiographyInfo = newUserBiographyInfo;
    }

    public String toString() {
        return "login: " + mUserLogin + "\navatar_url: " + mUserAvatarUrl + "\nbio: " + mUserBiographyInfo;
    }
}
