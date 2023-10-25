package com.example.shareapp.controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shareapp.R;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.User;

import java.util.List;

public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostAdapter.FeedPostViewHolder> {
    private List<Post> mListPost;
    public FeedPostAdapter(List<Post> mListPost) {
        this.mListPost = mListPost;
    }
    private View mView;
    @NonNull
    @Override
    public FeedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        return new FeedPostViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedPostViewHolder holder, int position) {
        Post post = mListPost.get(position);
        if(post == null)
            return;

        Glide.with(mView).load(post.getImage()).into(holder.imvImage);
        if(post.getUser().getAvata() != null)
            Glide.with(mView).load(post.getUser().getAvata()).into(holder.imvImagePoster);
        holder.tvTitle.setText(post.getTitle());
        holder.tvFullNamePoster.setText(post.getUser().fullName);
        holder.tvUploadedAt.setText(post.convertCreatedAtToDateTime());
    }

    @Override
    public int getItemCount() {
        return mListPost.size();
    }

    public class FeedPostViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvImage, imvImagePoster;
        private TextView tvTitle, tvFullNamePoster, tvUploadedAt;
        public FeedPostViewHolder(@NonNull View itemView) {
            super(itemView);

            imvImage = itemView.findViewById(R.id.item_post_imv_image);
            imvImagePoster = itemView.findViewById(R.id.imv_image_poster);
            tvTitle = itemView.findViewById(R.id.item_post_tv_title);
            tvFullNamePoster = itemView.findViewById(R.id.tv_full_name_poster);
            tvUploadedAt = itemView.findViewById(R.id.item_post_tv_uploaded_at);
        }
    }
}
