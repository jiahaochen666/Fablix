package com.team83.androidfablix;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Movie {
    private String id;
    private String title;
    private String year;
    private String director;
    private ArrayList<String> genres;
    private ArrayList<String> stars;

    public Movie(String id, String title, String year, String director, ArrayList<String> genres, ArrayList<String> stars) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<String> getStars() {
        return stars;
    }

    public String getYear() {
        return year;
    }

    public String getId(){
        return id;
    }
}
