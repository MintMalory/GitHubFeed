package ua.mintmalory.githubfeed.api;

import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.Path;
import ua.mintmalory.githubfeed.model.UserInfo;

public interface GitHubService {

    @GET("/users/{userName}")
    Call<UserInfo> getUserInfo(@Path("userName") String userName);

}
