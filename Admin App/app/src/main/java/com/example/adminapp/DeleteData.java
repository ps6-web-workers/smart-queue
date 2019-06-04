package com.example.adminapp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;

public class DeleteData extends AsyncTask<URL, Integer, Long> {

        private JSONObject jsonObject;
        Student currentStudentBRI;
        Student currentStudentRespo;
        Student currentStudentTuteur;

        public DeleteData() {
        }

        protected Long doInBackground(URL... urls) {
            long totalSize = 0;
            for (URL url : urls) {
                requestContent(url);
                totalSize++;
            }
            return totalSize;
        }

        public void requestContent(URL url) {

            HttpURLConnection urlConnection = null;

            try {

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                String data="";
                while(line != null) {
                    line = bufferedReader.readLine();
                    data += line;
                }

            } catch (Exception e) {
                System.out.println(e);
            }
        }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
/*        MainActivity.prenomBRI.setText("Prénom : " + currentStudentBRI.getfName());
        MainActivity.nomBRI.setText("Nom : " + currentStudentBRI.getlName());
        MainActivity.prenomRespoStage.setText("Prénom : " + currentStudentRespo.getfName());
        MainActivity.nomRespoStage.setText("Nom : " + currentStudentRespo.getlName());
        MainActivity.prenomTuteur.setText("Prénom : " + currentStudentTuteur.getfName());
        MainActivity.nomTuteur.setText("Nom : " + currentStudentTuteur.getlName());*/
    }

    private void refresh() {
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
    }

    URL currentUserURL(int id) throws MalformedURLException {
        return new URL("http://yursilv.alwaysdata.net/api/queues/" + id + "/currentTicket");
    }
}
