package com.example.adminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.adminapp.Models.Queue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    Button nextButton;
    public static TextView prenomBRI;
    public static TextView nomBRI;
    public static TextView data;
    public static TextView nomRespoStage;
    public static TextView prenomRespoStage;
    public static TextView nomTuteur;
    public static TextView prenomTuteur;
    Button nextButtonTuteur;
    Button nextButtonRespoStage;
    FetchData fetchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nextButton  = (Button)findViewById(R.id.nextButton);
        prenomBRI = (TextView)findViewById(R.id.prenomBRI);
        nomBRI = (TextView)findViewById(R.id.nomBRI);
        prenomRespoStage = (TextView)findViewById(R.id.prenomRespoStage);
        nomRespoStage = (TextView)findViewById(R.id.nomRespoStage);
        nextButtonRespoStage = (Button)findViewById(R.id.nextButtonRespoStage);
        prenomTuteur = (TextView)findViewById(R.id.prenomTuteur);
        nomTuteur = (TextView)findViewById(R.id.nomTuteur);
        nextButtonTuteur = (Button)findViewById(R.id.nextButtonTuteur);
        fetchData = new FetchData();
        fetchData.execute();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nextBRI();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        nextButtonRespoStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nextRespoStage();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        nextButtonTuteur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nextTuteur();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void nextBRI() throws IOException, JSONException {
        Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
        deleteCurrentUser(1);

    }

    public void nextRespoStage() throws IOException, JSONException {
        Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
        //fetchData.deleteCurrentUser(3);
        deleteCurrentUser(2);
    }

    public void nextTuteur() throws IOException, JSONException {
        Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
        //fetchData.deleteCurrentUser(3);
        deleteCurrentUser(3);
        //fetchData.execute();
    }

    void deleteCurrentUser(final int id) throws IOException, JSONException {
        // Instantiate the RequestQueue.
        URL url = new URL("http://yursilv.alwaysdata.net/api/queues/" + id + "/nextTicket");
        DeleteData deleteData = new DeleteData();
        deleteData.execute(url);
/*        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
*//*        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line="";
        String data="";
        while(line != null) {
            line = bufferedReader.readLine();
            data += line;
        }*/
        FetchData fetchData = new FetchData();
        fetchData.execute();
    }

   /* public void nextCurrentStudent(int id){
        singleton = SingletonRequestQueue.getInstance(getApplicationContext());
        String url = "http://yursilv.alwaysdata.net/api/queues/" + id + "/currentTicket";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                                /Type data_type = new TypeToken<ArrayList<Queue>>(){}.getType();
                                ArrayList<Queue> data = gson.fromJson(response.toString(), data_type);
                                StringBuilder str = new StringBuilder();
                                for (Queue q : data) {
                                    str.append(q.toString() + "\n");
                                }/
                        Student currentStudent = gson.fromJson(response.toString(), Student.class);
                        prenomBRI.setText("Prenom : " + currentStudent.getfName());
                        nomBRI.setText("Nom : " + currentStudent.getlName());
                        Log.d(TAG, currentStudent.toString());
                       // Log.d(TAG, userStored.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                       // status.setText("Oups! ça marche pas très bien...");
                        Log.d(TAG, error.toString());
                    }
                });

        // Access the RequestQueue through your singleton class.
        singleton.addToRequestQueue(jsonObjectRequest);
    }*/
}
