package ua.mintmalory.githubfeed;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.mintmalory.githubfeed.api.GitHubService;
import ua.mintmalory.githubfeed.model.UserInfo;

public class MainActivity extends AppCompatActivity {
    public static final String ENDPOINT = "https://api.github.com";
	public static final String USER_INFO_EXTRA_TAG = "ua.mintmalory.githubfeed.MainActivity.USER_INFO_EXTRA_TAG";
    private Button mGetUserInfoBtn;
    private EditText mUserNameEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mGetUserInfoBtn = (Button) findViewById(R.id.getUserInfo_btn);
        mUserNameEditTxt = (EditText) findViewById(R.id.userName_editText);
        final View containerView = findViewById(R.id.ll);

        if (mGetUserInfoBtn != null) {
            mGetUserInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isOnline()) {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ENDPOINT)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        GitHubService service = retrofit.create(GitHubService.class);

                        Call<UserInfo> call = service.getUserInfo(mUserNameEditTxt == null ? "" : mUserNameEditTxt.getText().toString());

                        call.enqueue(new Callback<UserInfo>() {
                            @Override
                            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                                if (response.code() == 200) {

									Intent i = new Intent(getApplicationContext(), DetailedUserInfoActivity.class);
									i.putExtra(USER_INFO_EXTRA_TAG, response.body());
									startActivity(i);
									
                                } else {

                                    Snackbar.make(containerView, "Sorry! ", Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserInfo> call, Throwable t) {
                            
							}
                        });
                    } else {
						//TODO: add string resourse
                        Snackbar.make(containerView, "No Internet connection", Snackbar.LENGTH_LONG).show();
                    }
                }

                private boolean isOnline() {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                        return true;
                    } else {
                        return false;
                    }
                }

            });
        }

    }
}
