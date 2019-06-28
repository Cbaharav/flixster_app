package com.example.flixster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flixster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;


public class MovieDetailsActivity extends AppCompatActivity {

    public final static String YT_API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String YT_API_KEY_PARAM = "api_key";

    //the movie to display
    Movie movie;

    //the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    Intent i;
    AsyncHttpClient client;
    String videoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);

        //unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        //set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        //vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        getVideoId();
    }

    public void getVideoId() {
        client = new AsyncHttpClient();
        String movie_id = Integer.toString(movie.getId());
        videoId = null;

        //create the url
        String url = String.format("%s/movie/%s%s", YT_API_BASE_URL, movie_id, "/videos?");
        //set the request parameters
        RequestParams params = new RequestParams();
        params.put(YT_API_KEY_PARAM, getString(R.string.api_key)); //API key, always required
        //execute a GET request expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");

                    //get key from first result object
                    videoId = results.getJSONObject(0).getString("key");
                    Log.d("Carmel", "successfully got key");

                } catch (JSONException e) {
                    Log.e("ParsingFailure", "Failed to parse now playing movies");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("DataFailure", "Failed to get data from now playing endpoint");
            }
        });
    }

    public void playTrailer(View v) {
        Log.d("Carmel", "title clicked");
        //videoId is returning null
        if(videoId == null) finish();
        else {
            i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
            i.putExtra("videoId", videoId);
            startActivity(i);
        }
    }



}
