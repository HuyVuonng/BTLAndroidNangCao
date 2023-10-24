package com.example.shareapp.controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareapp.R;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;

import java.util.List;

public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder> {
    private List<Post> mListPost;
    public FeedPostAdapter(List<Post> mListPost) {
        this.mListPost = mListPost;
    }
    @NonNull
    @Override
    public FeedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        return new FeedPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedPostViewHolder holder, int position) {
        Post post = mListPost.get(position);
        if(post == null)
            return;

        holder.tvTitle.setText(post.getTitle());
        holder.tvUploader.setText(post.getUser().fullName);
        holder.tvUploadedAt.setText(post.convertCreatedAtToDateTime());
    }

    @Override
    public int getItemCount() {
        return mListPost.size();
    }

    public class FeedPostViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;
        private TextView tvTitle, tvUploader, tvUploadedAt;
        public FeedPostViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.item_post_iv_image);
            tvTitle = itemView.findViewById(R.id.item_post_tv_title);
            tvUploader = itemView.findViewById(R.id.item_post_tv_uploader);
            tvUploadedAt = itemView.findViewById(R.id.item_post_tv_uploaded_at);
        }
    }
}
