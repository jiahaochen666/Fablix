package com.team83.androidfablix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private ArrayList<Movie> movies;

    public MovieListViewAdapter(ArrayList<Movie> movies, Context context) {
        super(context, R.layout.row, movies);
        this.movies = movies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = movies.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.row, parent, false);
        TextView title = view.findViewById(R.id.title);
        TextView year = view.findViewById(R.id.year);
        TextView director = view.findViewById(R.id.director);
        TextView genres = view.findViewById(R.id.genres);
        TextView stars = view.findViewById(R.id.stars);
        title.setText(movie.getTitle());
        year.setText("Published year: " + movie.getYear());
        director.setText("Director: " + movie.getDirector());
        genres.setText("Genres: " + movie.getGenres().toString());
        stars.setText("Stars: " + movie.getStars().toString());
        if(position % 2 == 0)
            view.setBackgroundColor(0xffcccccc);

        return view;
    }
}
