package com.team83.androidfablix;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class One extends AppCompatActivity {
    private String url;
    private TextView title;
    private TextView year;
    private TextView director;
    private TextView genres;
    private TextView stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        url = "https://3.23.114.109:8443/cs122b-spring20-project1/api/singlemovie?movieid=" + getIntent().getStringExtra("movieId");

        title = findViewById(R.id.movieTitle);
        year = findViewById(R.id.year);
        director = findViewById(R.id.movie_director);
        genres = findViewById(R.id.movie_genres);
        stars = findViewById(R.id.movie_stars);

        System.out.println(url);

        final RequestQueue queue = NetworkManager.sharedManager(One.this).queue;
        final StringRequest OneRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject movie_info = (JSONObject) jsonResponse.get(0);
                    String movie_title = movie_info.getString("title");
                    String movie_year = movie_info.getString("year");
                    String movie_director = movie_info.getString("director");
                    ArrayList<String> movie_stars = new ArrayList<>();
                    JSONArray response_stars = (JSONArray) jsonResponse.get(1);
                    for(int i = 0; i < response_stars.length(); i++){
                        movie_stars.add(((JSONObject)response_stars.get(i)).getString("name"));
                    }
                    JSONArray movie_genres = (JSONArray) jsonResponse.get(2);
                    title.setText(movie_title);
                    year.setText("Published year: " + movie_year);
                    director.setText("Director: " + movie_director);
                    genres.setText("Genres: " + movie_genres.toString());
                    stars.setText("Stars: " + movie_stars.toString());
                    System.out.println(movie_stars.toString());
                } catch (JSONException e) {
                    Log.d("Error", e.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("SingleMovie.error", error.toString());
                    }
                });

        queue.add(OneRequest);
    }
}
