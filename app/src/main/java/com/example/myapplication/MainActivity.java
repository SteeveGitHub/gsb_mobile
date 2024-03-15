package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    Button login;
    ProgressBar progressBar;
    RequestQueue requestQueue;

    private static final String LOGIN_URL = "https://trincal.alwaysdata.net/api/login.php";
    private static final String STATUS = "status";
    private static final int SUCCESS_STATUS = 200;

    public static final String EXTRA_MESSAGE = "com.example.myapplication.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.login_progress);
        requestQueue = Volley.newRequestQueue(this);

        login.setOnClickListener(v -> {
            String userVar = username.getText().toString().trim();
            String passVar = password.getText().toString().trim();
            if (userVar.isEmpty() || passVar.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username and Password cannot be blank", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar
                loginRequest(userVar, passVar);
            }
        });
    }

    private void loginRequest(String userVar, String passVar) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                response -> {
                    progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                    try {
                        if (response != null && !response.trim().isEmpty()) {
                            JSONObject jsonObject = new JSONObject(response.trim());
                            Log.d("Response", jsonObject.toString());
                            if (jsonObject.has(STATUS) && jsonObject.getInt(STATUS) == SUCCESS_STATUS) {
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, PostLoginActivity.class);
                                String message = jsonObject.toString();
                                intent.putExtra(EXTRA_MESSAGE, message);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Empty server response", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Failed to parse server response", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE); // Hide the ProgressBar
                    Toast.makeText(MainActivity.this, "Failed to connect to server. Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", userVar);
                params.put("password", passVar);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
