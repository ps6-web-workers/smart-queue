package com.example.adminapp;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FetchData extends AsyncTask<Void, Void, Void> {
    /*String data ="";
    String dataParsed="";
    String singleParsed="";
    ArrayList<Student> studentArrayListBRI = new ArrayList<>();
    ArrayList<Student> studentArrayListRespoStage = new ArrayList<>();
    ArrayList<Student> studentArrayListTuteur = new ArrayList<>();*/
    Student currentStudentBRI;
    Student currentStudentRespo;
    Student currentStudentTuteur;

    @Override
    protected Void doInBackground(Void... voids) {
        for(int i = 1; i < 4; i++) {
            try {
                URL myURL = currentUserURL(i);
                HttpURLConnection httpURLConnection = (HttpURLConnection) myURL.openConnection();
                httpURLConnection.setRequestMethod("GET");
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                String data="";
                while(line != null) {
                    line = bufferedReader.readLine();
                    data += line;
                }
                JSONObject jsonObject = new JSONObject(data);
                if(i == 1) currentStudentBRI = new Student((String)jsonObject.get("userFirstName"), (String)jsonObject.get("userLastName"));
                if(i == 2) currentStudentRespo = new Student((String)jsonObject.get("userFirstName"), (String)jsonObject.get("userLastName"));
                if(i == 3) currentStudentTuteur = new Student((String)jsonObject.get("userFirstName"), (String)jsonObject.get("userLastName"));
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
