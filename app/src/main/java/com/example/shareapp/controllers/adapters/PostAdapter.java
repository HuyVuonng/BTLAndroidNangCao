package com.example.shareapp.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shareapp.models.Post;

import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter {
    private Context context;
    private int resource;
    private ArrayList<Post> posts;

    public PostAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Post> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.posts = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        // @todo: set item view logic here
        return convertView;
    }
}
