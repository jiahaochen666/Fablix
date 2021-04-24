package com.team83.androidfablix;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
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

public class MovieListActivity extends AppCompatActivity {
    private String url;
    private ArrayList<Movie> movieList;
    private MovieListViewAdapter adapter;
    private Button prev;
    private Button next;
    private int total;
    private int frontpage = 0;

    public MovieListActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);

        prev.setEnabled(false);

        BuildListView(frontpage);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next.setEnabled(true);
                if (frontpage - 20 >= 0) {
                    frontpage -= 20;
                    BuildListView(frontpage);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev.setEnabled(true);
                if (frontpage + 20 < total) {
                    frontpage += 20;
                    BuildListView(frontpage);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void BuildListView(int frontpage) {
        movieList = new ArrayList<>();
        url = "https://3.23.114.109:8443/cs122b-spring20-project1/api/";
        String jsondata = getIntent().getStringExtra("keys");
        try {
            JSONObject data = new JSONObject(jsondata);
            url += "movielist?title=" + data.getString("title") + "&year=" + data.getString("year")
                    + "&director=" + data.getString("director") + "&actor=" + data.getString("actor") +
                    "&frontpage=" + frontpage + "&size=20&sort=rating&order=Desc1Desc2";
        } catch (JSONException e) {
            Log.d("URLError", e.toString());
        }
        final RequestQueue queue = NetworkManager.sharedManager(MovieListActivity.this).queue;
        final StringRequest MovieRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i = 0; i < jsonResponse.length() - 1; i++) {
                        JSONObject movie = (JSONObject) jsonResponse.get(i);
                        String title = movie.getString("title");
                        String id = movie.getString("id");
                        String year = movie.getString("year");
                        String director = movie.getString("director");
                        ArrayList<String> genres = new ArrayList<>();
                        ArrayList<String> stars = new ArrayList<>();
                        JSONArray raw_genres = movie.getJSONArray("moviegenres");
                        JSONArray raw_stars = movie.getJSONArray("moviestars");
                        for (int j = 0; j < raw_genres.length(); j++) {
                            genres.add((String) raw_genres.get(j));
                        }
                        for (int j = 0; j < raw_stars.length(); j++) {
                            JSONObject star = (JSONObject) raw_stars.get(j);
                            stars.add(star.getString("name"));
                        }
                        movieList.add(new Movie(id, title, year, director, genres, stars));
                    }
                    total = ((JSONObject) jsonResponse.get(jsonResponse.length() - 1)).getInt("total");

                    adapter = new MovieListViewAdapter(movieList, MovieListActivity.this);
                    ListView listView = findViewById(R.id.movielist);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Movie movie = movieList.get(position);
                            String movieId = movie.getId();
                            Intent listPage = new Intent(MovieListActivity.this, One.class);
                            listPage.putExtra("movieId", movieId);
                            startActivity(listPage);
                        }
                    });
                } catch (JSONException e) {
                    Log.d("URLParseError", e.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("movielist.error", error.toString());
                    }
                }) {
        };
        queue.add(MovieRequest);

        if (total != 0) {
            if (frontpage - 20 < 0) {
                prev.setEnabled(false);
            }
        }

        if (frontpage != 0) {
            if (frontpage + 20 > total) {
                next.setEnabled(false);
            }
        }
    }
}

