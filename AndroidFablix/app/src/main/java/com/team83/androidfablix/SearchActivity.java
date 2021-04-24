package com.team83.androidfablix;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private EditText title;
    private EditText year;
    private EditText director;
    private EditText star;
    private Button search;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        title = findViewById(R.id.title);
        search = findViewById(R.id.search);

        url = "https://3.23.114.109:8443/cs122b-spring20-project1/api/";

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RequestQueue queue = NetworkManager.sharedManager(SearchActivity.this).queue;
                final StringRequest SearchRequest = new StringRequest(Request.Method.POST, url + "main", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("search.success", response);
                        Intent listPage = new Intent(SearchActivity.this, MovieListActivity.class);
                        listPage.putExtra("keys", response);
                        startActivity(listPage);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("search.error", error.toString());
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Post request form data
                        final Map<String, String> params = new HashMap<>();
                        params.put("title", title.getText().toString());
                        System.out.println(params.toString());
                        return params;
                    }
                };
                queue.add(SearchRequest);

            }
        });
    }
}
