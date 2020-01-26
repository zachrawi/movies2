package com.zachrawi.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Movie> {
    private Context mContext;
    private int mResource;
    private ArrayList<Movie> mMovies;

    private static class ViewHolder {
        ImageView poster;
    }

    public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Movie> movies) {
        super(context, resource, movies);

        mContext = context;
        mResource = resource;
        mMovies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // 1) Layout Inflater
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        // 2) ViewHolder
        ViewHolder viewHolder = new ViewHolder();

        // 3) check convertView
        if (convertView == null) {
            convertView = layoutInflater.inflate(mResource, parent, false);

            viewHolder.poster = convertView.findViewById(R.id.ivPoster);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 4) ambil data
        Movie movie = mMovies.get(position);

        // 5) render ke view
        Picasso
                .with(mContext)
                .load(movie.getPoster())
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.poster);

        return convertView;
    }
}
