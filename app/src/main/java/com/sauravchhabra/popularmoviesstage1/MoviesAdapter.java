package com.sauravchhabra.popularmoviesstage1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mPosters;
    private String[] mPosterUrl;

    private static final String API_URL = "http://image.tmdb.org/t/p/w185";
    private final String LOG_TAG = getClass().toString();

    public MoviesAdapter(Context context, ArrayList<String> moviesList) {
        mContext = context;
        mPosters = moviesList;

        try {
            mPosterUrl = new String[mPosters.size()];
            for (int i = 0; i < mPosterUrl.length; i++) {
                mPosterUrl[i] = API_URL + mPosters.get(i);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in Adapter", e);
        }
    }

    @Override
    public int getCount() {
        return mPosterUrl.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup viewGroup) {
        ImageView imageView;

        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(420, 500));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 8, 0, 8);
        } else {
            imageView = (ImageView) view;
        }
        Picasso.get().load(mPosterUrl[position]).into(imageView);

        return imageView;
    }
}
