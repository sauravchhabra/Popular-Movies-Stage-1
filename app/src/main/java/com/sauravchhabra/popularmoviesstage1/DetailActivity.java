package com.sauravchhabra.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DetailActivity extends AppCompatActivity {

    int id;
    TextView mTitle, mAvgRating, mReleaseDate, mPlot, mErrorMessage;
    ImageView mPoster;
    ProgressBar mProgressbar;

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        //Check is device has an active internet connection
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Up button to go to MainActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = findViewById(R.id.detail_title_tv);
        mAvgRating = findViewById(R.id.detail_rating_tv);
        mReleaseDate = findViewById(R.id.detail_released_tv);
        mPlot = findViewById(R.id.detail_plot_tv);
        mPoster = findViewById(R.id.detail_poster_iv);
        mErrorMessage = findViewById(R.id.tv_error_message_display_detail);
        mProgressbar = findViewById(R.id.pb_loading_indicator_detail);
        mProgressbar.setVisibility(View.VISIBLE);

        id = getIntent().getIntExtra(MainActivity.MOVIE_KEY, 0);

        FetchMovie fetchMovie = new FetchMovie();
        fetchMovie.execute();
    }

    public class FetchMovie extends AsyncTask<Void, Void, Void>{
        String LOG_TAG = "FetchMovieAsyncTask";
        String title, releaseDate, avgRating, plot, imageUrl;
        String api_key_label = "?api_key=";

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            String movieJson = null;
            InputStream inputStream = null;
            StringBuilder stringBuilder = new StringBuilder();
            try{
                URL url = new URL(getString(R.string.api_url) + Integer.toString(id) +
                        api_key_label + getString(R.string.api_key));
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                inputStream = httpURLConnection.getInputStream();
                if (inputStream == null){
                    return null;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line + "\n");
                } if(stringBuilder.length() == 0){
                    return null;
                }
                movieJson = stringBuilder.toString();
                JSONObject result = new JSONObject(movieJson);
                title = result.optString("title");
                releaseDate = result.optString("release_date");
                avgRating = result.optString("vote_average");
                plot = result.optString("overview");
                imageUrl = getString(R.string.api_poster_url) + result.optString("poster_path");
            } catch (Exception e){
                e.printStackTrace();
                mErrorMessage.setVisibility(View.VISIBLE);

            } finally{
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }if (bufferedReader!=null){
                    try{
                        bufferedReader.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgressbar.setVisibility(View.GONE);
            mErrorMessage.setVisibility(View.GONE);

            if (!isConnected()) {
                mErrorMessage.setVisibility(View.VISIBLE);
                mErrorMessage.setText(R.string.no_connection);
            } else {

                mErrorMessage.setVisibility(View.GONE);
                getSupportActionBar().setTitle(title);
            }
            mTitle.setText(title);
            mAvgRating.setText("User Rating: " + avgRating);
            mPlot.setText("Overview: " + "\n" + plot);
            mReleaseDate.setText("Released On: " + releaseDate);
            mPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(imageUrl).into(mPoster);
            super.onPostExecute(aVoid);
        }
    }
}
