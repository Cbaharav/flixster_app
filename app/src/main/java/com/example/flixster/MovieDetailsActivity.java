package com.example.flixster;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class MovieDetailsActivity extends YouTubeBaseActivity {

    public final static String YT_API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String YT_API_KEY_PARAM = "api_key";

    //the movie to display
    Movie movie;

    //the view objects
//    TextView tvTitle;
//    TextView tvOverview;
//    RatingBar rbVoteAverage;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;


    //Intent i;
    AsyncHttpClient client;
    String videoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //resolve the view objects
//        tvTitle = (TextView) findViewById(R.id.tvTitle);
//        tvOverview = (TextView) findViewById(R.id.tvOverview);
//        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        ButterKnife.bind(this);

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
                    playTrailer();

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

    public void playTrailer() {
//        Log.d("Carmel", "title clicked");
//        //videoId is returning null
//        if(videoId == null) finish();
//        else {
//            i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
//            i.putExtra("videoId", videoId);
//            startActivity(i);
//        }
        Log.d("Carmel", "In movie trailer activity");

        if(videoId == null) finish();
        else {
            //resolve the player view from the layout
            YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.player);

            //initialize with API key stored in secrets.xml
            playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    //do any work here to cue video, play video, etc.
                    youTubePlayer.cueVideo(videoId);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    //log the error
                    Log.e("MovieTrailerActivity", "Error initializing YouTube player");
                }
            });
        }
    }



}
