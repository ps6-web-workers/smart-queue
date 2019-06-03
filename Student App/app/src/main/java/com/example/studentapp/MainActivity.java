package com.example.studentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.studentapp.Models.Queue;
import com.example.studentapp.Models.Ticket;
import com.example.studentapp.Models.User;
import com.example.studentapp.Utils.SingletonRequestQueue;
import com.example.studentapp.Utils.UserLocalStore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private TextView status;
    private final Gson gson = new Gson();
    private SingletonRequestQueue singleton;
    private StringRequest stringRequest;
    private User userStored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final UserLocalStore userLocalStore = new UserLocalStore(this);
        userLocalStore.storeUserData(new User("romain", 1, "Romain", "Giuntini"));
        userStored = userLocalStore.getStoredUser();

        this.status = (TextView)this.findViewById(R.id.status);
        final Button refresh = (Button)this.findViewById(R.id.btn);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshText();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (singleton.getRequestQueue() != null) {
            singleton.getRequestQueue().cancelAll(TAG);
        }
    }

    private void refreshText() {
        // Instantiate the RequestQueue.
        singleton = SingletonRequestQueue.getInstance(this);
        String url = "http://yursilv.alwaysdata.net/api/queues/" + userStored.getAbonnement() + "/currentTicket";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        /*Type data_type = new TypeToken<ArrayList<Queue>>(){}.getType();
                        ArrayList<Queue> data = gson.fromJson(response.toString(), data_type);
                        StringBuilder str = new StringBuilder();
                        for (Queue q : data) {
                            str.append(q.toString() + "\n");
                        }*/
                        Ticket currentTicket = gson.fromJson(response.toString(), Ticket.class);
                        if (currentTicket.getTicketId() == userStored.getAbonnement() && currentTicket.getUserLogin().equals(userStored.getLoggin())) {
                            status.setText("Oui vous êtes attendu");
                        } else {
                            status.setText("Non toujours");
                        }
                        Log.d(TAG, currentTicket.toString());
                        Log.d(TAG, userStored.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        status.setText("Oups! ça marche pas très bien...");
                        Log.d(TAG, error.toString());
                    }
                });

        // Access the RequestQueue through your singleton class.
        singleton.addToRequestQueue(jsonObjectRequest);
    }

}
