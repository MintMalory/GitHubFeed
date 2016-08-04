package ua.mintmalory.githubfeed;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;

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
    public static final String REPOS_LIST_EXTRA_TAG = "ua.mintmalory.githubfeed.DetailedUserInfoActivity.REPOS_LIST_EXTRA_TAG";
    private UserInfo userInfo;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mUserNameTitleCollapsed;
    private TextView mUserNameTitle;
    private TextView mUserBioInfo;
    private AppBarLayout mAppBarLayout;
    private CircleImageView mAvatarView;
    private RecyclerView mReposRecyclerView;
    private ArrayList<RepositoryInfo> mReposList;
    private Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_user_info);

        initActivityViews();

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            userInfo = (UserInfo) intent.getExtras().getSerializable(MainActivity.USER_INFO_EXTRA_TAG);
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(GitHubService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            getReposList();
        } else {
            userInfo = (UserInfo) savedInstanceState.get(MainActivity.USER_INFO_EXTRA_TAG);
            mReposList = (ArrayList<RepositoryInfo>) savedInstanceState.get(REPOS_LIST_EXTRA_TAG);
            mReposRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
            mReposRecyclerView.setAdapter(new RecyclerAdapter(mReposList));
        }

        mUserNameTitleCollapsed.setText(userInfo.getUserLogin());
        mUserNameTitle.setText(userInfo.getUserLogin());
        mUserBioInfo.setText(userInfo.getUserBiographyInfo());
        mAppBarLayout.addOnOffsetChangedListener(this);
        mReposRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        Picasso.with(this)
                .load(userInfo.getUserAvatarUrl())
                .error(R.drawable.error_img)
                .into(mAvatarView);

        startAlphaAnimation(mUserNameTitleCollapsed, 0, View.INVISIBLE);
    }

    private void getReposList() {
        final View rootView = findViewById(R.id.rootView_coordinatorLayout);
        if (isOnline()) {
            GitHubService service = mRetrofit.create(GitHubService.class);
            Call<ArrayList<RepositoryInfo>> call = service.getRepositoryInfo(userInfo.getUserLogin());

            call.enqueue(new Callback<ArrayList<RepositoryInfo>>() {
                @Override
                public void onResponse(Call<ArrayList<RepositoryInfo>> call, Response<ArrayList<RepositoryInfo>> response) {
                    if (response.code() == 200) {
                        mReposList = response.body();
                        mReposRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                        mReposRecyclerView.setAdapter(new RecyclerAdapter(mReposList));
                    } else {
                        Snackbar.make(rootView, getString(R.string.bad_code_response_error_msg) + response.code(), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<RepositoryInfo>> call, Throwable t) {
                    Snackbar.make(rootView, getString(R.string.network_error_msg), Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(rootView, getString(R.string.no_internet_error_msg), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MainActivity.USER_INFO_EXTRA_TAG, userInfo);
        outState.putParcelableArrayList(REPOS_LIST_EXTRA_TAG, mReposList);
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

    private void initActivityViews() {
        mUserNameTitleCollapsed = (TextView) findViewById(R.id.main_title_textview);
        mReposRecyclerView = (RecyclerView) findViewById(R.id.repos_recyclerView);
        mAvatarView = (CircleImageView) findViewById(R.id.avatar_img);
        mUserBioInfo = (TextView) findViewById(R.id.userBio_textView);
        mUserNameTitle = (TextView) findViewById(R.id.userName_textView);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_title_linearlayout);
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
                startAlphaAnimation(mUserNameTitleCollapsed, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(mUserNameTitleCollapsed, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
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
