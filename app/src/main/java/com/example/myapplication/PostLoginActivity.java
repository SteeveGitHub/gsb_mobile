package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostLoginActivity extends AppCompatActivity {

    private ArrayList<JSONObject> fraisList;
    private FraisAdapter fraisAdapter;

    private ArrayList<JSONObject> horsForfaitList;
    private FraisAdapter horsForfaitAdapter;

    public class FraisAdapter extends ArrayAdapter<JSONObject> {
        public FraisAdapter(Context context, ArrayList<JSONObject> fraisList) {
            super(context, 0, fraisList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            JSONObject frais = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.frais_item, parent, false);
            }

            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            TextView montantTextView = convertView.findViewById(R.id.montantTextView);
            TextView libelleTextView = convertView.findViewById(R.id.libelleTextView);

            try {
                assert frais != null;
                if (frais.has("date_debut")) {

                    dateTextView.setText(frais.getString("date_debut"));
                } else {
                    dateTextView.setText("Aucune date spécifiée");
                }
                if (frais.has("montantRestant"))  {
                    montantTextView.setText("Montant restant : " + frais.getString("montantRestant") + "€");
                } else {
                    montantTextView.setText("Aucun montant trouvé");
                }
                if (frais.has("comment")) {
                    libelleTextView.setText(frais.getString("comment"));
                } else {
                    libelleTextView.setText("Pas de commentaire trouvé");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    private void fetchFrais(String token) {
        String url = "https://trincal.alwaysdata.net/api/findFees.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("API Response", response); // Ajoutez cette ligne pour imprimer la réponse de l'API

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray fraisForfaitArray = jsonResponse.getJSONArray("fraisForfait");
                        JSONArray fraisHorsForfaitArray = jsonResponse.getJSONArray("fraisHorsForfait");

                        for (int i = 0; i < fraisForfaitArray.length(); i++) {
                            JSONObject frais = fraisForfaitArray.getJSONObject(i);
                            fraisList.add(frais);
                        }

                        for (int i = 0; i < fraisHorsForfaitArray.length(); i++) {
                            JSONObject frais = fraisHorsForfaitArray.getJSONObject(i);
                            horsForfaitList.add(frais);
                        }

                        fraisAdapter.notifyDataSetChanged(); // Assurez-vous que cette ligne est bien exécutée
                        horsForfaitAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(PostLoginActivity.this, "Failed to parse frais", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(PostLoginActivity.this, "Failed to fetch frais", Toast.LENGTH_LONG).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_login); // Assurez-vous que activity_post_login.xml existe dans res/layout

        ListView fraisListView = findViewById(R.id.fraisListView);
        ListView horsForfaitListView = findViewById(R.id.horsForfaitListView);

        fraisList = new ArrayList<>();
        horsForfaitList = new ArrayList<>();

        fraisAdapter = new FraisAdapter(this, fraisList); // Changez 'adapter' en 'fraisAdapter'
        horsForfaitAdapter = new FraisAdapter(this, horsForfaitList);

        fraisListView.setAdapter(fraisAdapter);
        horsForfaitListView.setAdapter(horsForfaitAdapter);

        Intent intent = getIntent();
        String userInformation = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        try {
            assert userInformation != null;

            JSONObject jsonObject = new JSONObject(userInformation);
            String token = jsonObject.getString("token");
            fetchFrais(token);

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

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}