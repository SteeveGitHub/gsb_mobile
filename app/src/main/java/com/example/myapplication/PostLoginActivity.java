package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import org.json.JSONObject;
import org.json.JSONException;

public class PostLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_login);

        Intent intent = getIntent();
        String userInformation = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        try {
            assert userInformation != null;

            JSONObject jsonObject = new JSONObject(userInformation);
            String numero = jsonObject.getString("numero");
            String adresse = jsonObject.getString("adresse");
            String cp = jsonObject.getString("cp");
            String email = jsonObject.getString("email");
            String cv_car = jsonObject.getString("cv_car");
            String nom = jsonObject.getString("nom");
            String prenom = jsonObject.getString("prenom");

            TextView nomTextView = findViewById(R.id.nomTextView);
            TextView prenomTextView = findViewById(R.id.prenomTextView);
            TextView emailTextView = findViewById(R.id.emailTextView);
            TextView numeroTextView = findViewById(R.id.numeroTextView);
            TextView adresseTextView = findViewById(R.id.adresseTextView);
            TextView cpTextView = findViewById(R.id.cpTextView);
            TextView cvCarTextView = findViewById(R.id.cvCarTextView);

            numeroTextView.setText(numero);
            adresseTextView.setText(adresse);
            cpTextView.setText(cp);
            emailTextView.setText(email);
            cvCarTextView.setText(cv_car);
            nomTextView.setText(nom);
            prenomTextView.setText(prenom);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}