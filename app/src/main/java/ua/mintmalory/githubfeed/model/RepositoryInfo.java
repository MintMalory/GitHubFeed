package ua.mintmalory.githubfeed.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RepositoryInfo implements Parcelable {
    @SerializedName("name")
    private String mRepoName;

    @SerializedName("language")
    private String mRepoLanguage;

    protected RepositoryInfo(Parcel in) {
        mRepoName = in.readString();
        mRepoLanguage = in.readString();
    }

    public static final Creator<RepositoryInfo> CREATOR = new Creator<RepositoryInfo>() {
        @Override
        public RepositoryInfo createFromParcel(Parcel in) {
            return new RepositoryInfo(in);
        }

        @Override
        public RepositoryInfo[] newArray(int size) {
            return new RepositoryInfo[size];
        }
    };

    public String getRepoName() {
        return mRepoName;
    }

    public void setRepoName(String newRepoName) {
        mRepoName = newRepoName;
    }

    public String getRepoLanguage() {
        return mRepoLanguage;
    }

    public void setRepoLanguage(String newRepoLanguage) {
        mRepoLanguage = newRepoLanguage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRepoName);
        dest.writeString(mRepoLanguage);
    }
}
