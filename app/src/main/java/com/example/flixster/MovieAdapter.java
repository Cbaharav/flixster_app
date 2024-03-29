package com.example.flixster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.example.flixster.models.Config;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    //list of movies
    ArrayList<Movie> movies;
    //config needed for image urls
    Config config;
    //context for rendering
    Context context;

    //initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    //creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //get the context and create the inflater
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, viewGroup, false);
        //return a new ViewHolder
        return new ViewHolder(movieView);
    }

    //binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //get the movie data at the specified position
        Movie movie = movies.get(i);


        //determine the current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        //build url for poster image
        String imageUrl = null;

        //if in portrait mode, load the poster image
        if(isPortrait) {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            //load the backdrop image
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
        }

        //populate the view with the movie data
        viewHolder.tvTitle.setText(movie.getTitle());
        viewHolder.tvOverview.setText(movie.getOverview());

        //get the correct placeholder and imageview for the current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? viewHolder.ivPosterImage : viewHolder.ivBackdropImage;

        //load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);

        float voteAverage = movie.getVoteAverage().floatValue();
        if(voteAverage >= 8f) {
            viewHolder.itemView.setBackgroundColor(Color.GREEN);
        }

        if(voteAverage <= 5f) {
            viewHolder.itemView.setBackgroundColor(Color.RED);
        }
    }

    //returns the total number of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    //create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;
//        @BindView(R.id.ivPosterImage) ImageView ivPosterImage;
//        @BindView(R.id.ivBackdropImage) ImageView ivBackdropImage;
//        @BindView(R.id.tvTitle) TextView tvTitle;
//        @BindView(R.id.tvOverview) TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            //add this as the itemView's onClickListener
            itemView.setOnClickListener(this);
//            ButterKnife.bind(this, itemView);
        }

        //when user clicks on a row, show MovieDetailsActivity for the selected movie
        @Override
        public void onClick(View v) {
            //gets item position
            int position = getAdapterPosition();
            //make sure position is valid, actually exists in the view
            if(position != RecyclerView.NO_POSITION) {
                //get the movie at the position
                Movie movie = movies.get(position);
                //create intent for the new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                //serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                //show activity
                context.startActivity(intent);
            }
        }
    }
}