package ua.mintmalory.githubfeed;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.mintmalory.githubfeed.model.RepositoryInfo;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<RepositoryInfo> mReposList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mRepoNameTextView;
        public TextView mRepoLanguageTextView;

        public ViewHolder(View v) {
            super(v);
            mRepoNameTextView = (TextView) v.findViewById(R.id.repoName_textView);
            mRepoLanguageTextView = (TextView) v.findViewById(R.id.repoLanguage_textView);
        }
    }


    public RecyclerAdapter(List<RepositoryInfo> reposList) {
        mReposList = reposList;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mRepoNameTextView.setText(mReposList.get(position).getRepoName());
        holder.mRepoLanguageTextView.setText(mReposList.get(position).getRepoLanguage());
    }

    @Override
    public int getItemCount() {
        return mReposList.size();
    }
}