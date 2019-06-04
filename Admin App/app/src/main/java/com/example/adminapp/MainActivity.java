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
import com.example.adminapp.Utils.SingletonRequestQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private SingletonRequestQueue singleton;
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
                nextBRI();
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

    public void nextBRI() {

        if(fetchData.studentArrayListBRI.size() != 0) {
            Toast.makeText(getApplicationContext(), "NEEEEEEEXT ! BRI", Toast.LENGTH_SHORT).show();
            fetchData.studentArrayListBRI.remove(0);
            if(fetchData.studentArrayListBRI.size() != 0) {
                prenomBRI.setText("Prenom : " + fetchData.studentArrayListBRI.get(0).fName);
                nomBRI.setText("Nom : " + fetchData.studentArrayListBRI.get(0).lName);
            }
            else {
                prenomBRI.setText("Prenom : ");
                nomBRI.setText("Nom : ");
                Toast.makeText(getApplicationContext(), "Plus d'étudiants !", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            prenomBRI.setText("Prenom : ");
            nomBRI.setText("Nom : ");
            Toast.makeText(getApplicationContext(), "Plus d'étudiants !", Toast.LENGTH_SHORT).show();
        }

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
        singleton = SingletonRequestQueue.getInstance(this);
        String url = "http://yursilv.alwaysdata.net/api/queues/" + id + "/nextTicket";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Type data_type = new TypeToken<ArrayList<Queue>>(){}.getType();
                        ArrayList<Queue> queues = gson.fromJson(response.toString(), data_type);
                        StringBuilder str = new StringBuilder();
                        for (Queue q : queues) {
                            str.append(q.toString() + "\n");
                           /* Student currentStudent = q.getStudents().;
                            switch (q.getId()){
                                case 1 :
                                    prenomBRI.setText(currentStudent.getfName());
                                    nomBRI.setText(currentStudent.getlName());
                                    break;
                                case 2 :
                                    prenomRespoStage.setText(currentStudent.getfName());
                                    nomRespoStage.setText(currentStudent.getlName());
                                    break;
                                case 3 :
                                    prenomTuteur.setText(currentStudent.getfName());
                                    nomTuteur.setText(currentStudent.getlName());
                                    break;
                            }*/
                        }
                        Log.d(TAG, String.valueOf(str));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                      //  status.setText("Oups! ça marche pas très bien...");
                        Log.d(TAG, error.toString());
                    }
                });


        // Access the RequestQueue through your singleton class.
        singleton.addToRequestQueue(jsonArrayRequest);

        /*
        URL deleteURL = deleteUserURL(id);
        HttpURLConnection httpURLConnection = (HttpURLConnection) deleteURL.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line="";

        while(line != null) {
            line = bufferedReader.readLine();
            data = data + line;
        }


        JSONArray jsonArray = new JSONArray(data);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            System.out.println(jsonObject + "\n");
            JSONObject jsonObject1 = (JSONObject) jsonObject.get("tickets");
            System.out.println(jsonObject.get("userFirstName"));
        }
        httpURLConnection.disconnect();*/
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
