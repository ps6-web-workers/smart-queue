package com.example.adminapp;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.adminapp.Models.Queue;
import com.example.adminapp.Utils.SingletonRequestQueue;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FetchData extends AsyncTask<Void, Void, Void> {
    String data ="";
    String dataParsed="";
    String singleParsed="";
    ArrayList<Student> studentArrayListBRI = new ArrayList<>();
    ArrayList<Student> studentArrayListRespoStage = new ArrayList<>();
    ArrayList<Student> studentArrayListTuteur = new ArrayList<>();
    Student currentStudentBRI;
    Student currentStudentRespo;
    Student currentStudentTuteur;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://yursilv.alwaysdata.net/api/queues");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
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


            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                URL thisUrl = null;
                if(jsonObject.get("name").equals("BRI")) {
                    thisUrl = currentUserURL(1);
                }
                if(jsonObject.get("name").equals("Responsable de stage")) {
                    thisUrl = currentUserURL(2);
                }
                if(jsonObject.get("name").equals("Tuteur de stage")) {
                    thisUrl = currentUserURL(3);
                }

                HttpURLConnection httpURLConnection1 = (HttpURLConnection) thisUrl.openConnection();
                httpURLConnection1.setRequestMethod("GET");
                httpURLConnection1.setRequestProperty("User-Agent", "Mozilla/5.0");
                InputStream inputStream1 = httpURLConnection1.getInputStream();
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1));
                String line1="";
                String data1="";
                while(line1 != null) {
                    line1 = bufferedReader1.readLine();
                    data1 = data1 + line1;
                }
                JSONObject jsonObject1 = new JSONObject(data1);
                if(jsonObject.get("name").equals("BRI")) {
                    currentStudentBRI = new Student((String) jsonObject1.get("userFirstName"), (String) jsonObject1.get("userLastName"));
                }
                if(jsonObject.get("name").equals("Responsable de stage")) {
                    currentStudentRespo = new Student((String) jsonObject1.get("userFirstName"), (String) jsonObject1.get("userLastName"));
                }
                if(jsonObject.get("name").equals("Tuteur de stage")) {
                    currentStudentTuteur = new Student((String) jsonObject1.get("userFirstName"), (String) jsonObject1.get("userLastName"));
                }
                httpURLConnection1.disconnect();
            }
            //deleteCurrentUser(3);

            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //MainActivity.data.setText(this.dataParsed);
        MainActivity.prenomBRI.setText("Prénom : " + currentStudentBRI.getfName());
        MainActivity.nomBRI.setText("Nom : " + currentStudentBRI.getlName());
        MainActivity.prenomRespoStage.setText("Prénom : " + currentStudentRespo.getfName());
        MainActivity.nomRespoStage.setText("Nom : " + currentStudentRespo.getlName());
        MainActivity.prenomTuteur.setText("Prénom : " + currentStudentTuteur.getfName());
        MainActivity.nomTuteur.setText("Nom : " + currentStudentTuteur.getlName());
    }

    URL currentUserURL(int id) throws MalformedURLException {
        return new URL("http://yursilv.alwaysdata.net/api/queues/" + id + "/currentTicket");
    }

    URL deleteUserURL(int id) throws MalformedURLException {
        return new URL("http://yursilv.alwaysdata.net/api/queues/" + id + "/nextTicket");
    }

}
