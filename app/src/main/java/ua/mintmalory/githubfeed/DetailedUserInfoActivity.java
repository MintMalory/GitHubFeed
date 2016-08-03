package ua.mintmalory.githubfeed;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.mintmalory.githubfeed.api.GitHubService;
import ua.mintmalory.githubfeed.model.RepositoryInfo;
import ua.mintmalory.githubfeed.model.UserInfo;

public class DetailedUserInfoActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private UserInfo userInfo;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mUseTitle;
    private TextView mUserNameTitle;
    private TextView mUserBioInfo;
    private AppBarLayout mAppBarLayout;
    private CircleImageView mAvatarView;
    private RecyclerView mReposRecyclerView;
    private ArrayList<RepositoryInfo> mReposList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_user_info);

        bindActivity();

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            userInfo = (UserInfo) intent.getExtras().getSerializable(MainActivity.USER_INFO_EXTRA_TAG);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MainActivity.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            GitHubService service = retrofit.create(GitHubService.class);

            Call<ArrayList<RepositoryInfo>> call = service.getRepositoryInfo(userInfo.getUserLogin());

            call.enqueue(new Callback<ArrayList<RepositoryInfo>>() {
                @Override
                public void onResponse(Call<ArrayList<RepositoryInfo>> call, Response<ArrayList<RepositoryInfo>> response) {
                    if (response.code() == 200) {
                        mReposList = response.body();
                        mReposRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                        mReposRecyclerView.setAdapter(new RecyclerAdapter(mReposList));
                    } else {
                        Toast.makeText(getApplication(), "error " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<RepositoryInfo>> call, Throwable t) {
                    Toast.makeText(getApplication(), "fail", Toast.LENGTH_LONG).show();

                }
            });
        } else {
            userInfo = (UserInfo) savedInstanceState.get(MainActivity.USER_INFO_EXTRA_TAG);
            mReposList = (ArrayList<RepositoryInfo>) savedInstanceState.get("TTT");
            mReposRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
            mReposRecyclerView.setAdapter(new RecyclerAdapter(mReposList));
        }

        mUseTitle.setText(userInfo.getUserLogin());
        mUserNameTitle.setText(userInfo.getUserLogin());
        mUserBioInfo.setText(userInfo.getUserBiographyInfo());
        mAppBarLayout.addOnOffsetChangedListener(this);
        Picasso.with(this)
                .load(userInfo.getUserAvatarUrl())
                .into(mAvatarView);

        startAlphaAnimation(mUseTitle, 0, View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MainActivity.USER_INFO_EXTRA_TAG, userInfo);
        outState.putParcelableArrayList("TTT",mReposList);

    }

    private void bindActivity() {
        mUseTitle = (TextView) findViewById(R.id.main_textview_title);
        mReposRecyclerView = (RecyclerView) findViewById(R.id.repos_recyclerView);
        mAvatarView = (CircleImageView) findViewById(R.id.avatar_img);
        mUserBioInfo = (TextView) findViewById(R.id.userBio_textView);
        mUserNameTitle = (TextView) findViewById(R.id.userName_textView);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mUseTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mUseTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
