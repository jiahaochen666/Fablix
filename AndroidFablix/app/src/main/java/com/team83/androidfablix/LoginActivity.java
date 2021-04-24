package com.team83.androidfablix;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private TextView message;
    private Button loginButton;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.user_email);
        password = findViewById(R.id.user_password);
        message = findViewById(R.id.message);
        loginButton = findViewById(R.id.login_button);

        url = "https://3.23.114.109:8443/cs122b-spring20-project1/api/";

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setText("Trying to login");
                // Use the same network queue across our application
                final RequestQueue queue = NetworkManager.sharedManager(LoginActivity.this).queue;
                //request type is POST
                final StringRequest loginRequest = new StringRequest(Request.Method.POST, url + "login", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //TODO should parse the json response to redirect to appropriate functions.
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.get("message").equals("success")) {
                                Log.d("login.success", response);
                                //initialize the activity(page)/destination
                                Intent listPage = new Intent(LoginActivity.this, SearchActivity.class);
                                //without starting the activity/page, nothing would happen
                                startActivity(listPage);
                            }
                            else{
                                message.setText(jsonResponse.get("message").toString());
                            }
                        } catch (JSONException e) {
                            Log.d("Error", e.toString());
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("login.error", error.toString());
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        // Post request form data
                        final Map<String, String> params = new HashMap<>();
                        params.put("email", username.getText().toString());
                        params.put("password", password.getText().toString());

                        return params;
                    }
                };

                // !important: queue.add is where the login request is actually sent
                queue.add(loginRequest);

            }
        });
    }
}
