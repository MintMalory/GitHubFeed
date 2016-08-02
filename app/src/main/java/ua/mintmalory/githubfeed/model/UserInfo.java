package ua.mintmalory.githubfeed.model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by mintmalory on 02.08.16.
 */
public class UserInfo implements Serializable{
    private String login;
    private String avatar_url;
    private String bio;
    private String repos_url;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }

    public String toString() {
        return "login: " + login + "\navatar_url: " + avatar_url + "\nbio: " + bio + "\nrepos_url: " + repos_url;
    }
}
