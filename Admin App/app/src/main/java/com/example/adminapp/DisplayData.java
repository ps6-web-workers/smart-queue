package com.example.adminapp;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DisplayData extends AsyncTask<Integer, Void, Void> {

    private Student currentStudent;
    private JSONObject jsonObject;

    DisplayData() {
    }

    @Override
    protected Void doInBackground(Integer... integers) {
            for (Integer integer : integers) {
                    try {
                        URL myURL = currentUserURL(integer);
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
                        jsonObject = new JSONObject(data);
                        currentStudent = new Student((String)jsonObject.get("userFirstName"), (String)jsonObject.get("userLastName"));
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
        if(currentStudent != null) {
            InfosActivity.prenom.setText("Prénom : " + currentStudent.getfName());
            InfosActivity.nom.setText("Nom : " + currentStudent.getlName());
            try {
                if(jsonObject.get("status").equals("active")) {
                    //InfosActivity.status.setText("Active");
                    //InfosActivity.status.setTextColor(0x008000);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            InfosActivity.prenom.setText("Prénom : ");
            InfosActivity.nom.setText("Nom : ");
        }
    }

    private URL currentUserURL(int id) throws MalformedURLException {
        return new URL("http://yursilv.alwaysdata.net/api/queues/" + id + "/currentTicket");
    }
}
