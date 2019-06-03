package com.example.adminapp;

import android.os.AsyncTask;

import org.json.JSONArray;
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
    String data ="";
    String dataParsed="";
    String singleParsed="";
    ArrayList<Student> studentArrayListBRI = new ArrayList<>();
    ArrayList<Student> studentArrayListRespoStage = new ArrayList<>();
    ArrayList<Student> studentArrayListTuteur = new ArrayList<>();

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
                singleParsed =  "Id : " + jsonObject.get("id") + "\n"
                        + "Name : " + jsonObject.get("name") + "\n";
                JSONArray myTickets = (JSONArray) jsonObject.get("tickets");
                for(int j = 0; j < myTickets.length(); j++) {
                    JSONObject myTicket = (JSONObject) myTickets.get(j);
                    singleParsed += "TicketID : " + myTicket.get("ticketId") + "\n"
                            + "userFirstName : " + myTicket.get("userFirstName") + "\n"
                            + "userLastName : " + myTicket.get("userLastName") + "\n";
                    System.out.println(jsonObject.get("name"));
                    if(jsonObject.get("name").equals("BRI")) {
                        studentArrayListBRI.add(new Student((String) myTicket.get("userFirstName"), (String) myTicket.get("userLastName")));
                    }

                    if(jsonObject.get("name").equals("Responsable de stage")) {
                        studentArrayListRespoStage.add(new Student((String) myTicket.get("userFirstName"), (String) myTicket.get("userLastName")));
                    }

                    if(jsonObject.get("name").equals("Tuteur de stage")) {
                        studentArrayListTuteur.add(new Student((String) myTicket.get("userFirstName"), (String) myTicket.get("userLastName")));
                    }
                }
                dataParsed += singleParsed + "\n";
            }

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
        MainActivity.prenomBRI.setText("Prénom : " + studentArrayListBRI.get(0).getfName());
        MainActivity.nomBRI.setText("Nom : " + studentArrayListBRI.get(0).getlName());
        MainActivity.prenomRespoStage.setText("Prénom : " + studentArrayListRespoStage.get(0).getfName());
        MainActivity.nomRespoStage.setText("Nom : " + studentArrayListRespoStage.get(0).getlName());
        MainActivity.prenomTuteur.setText("Prénom : " + studentArrayListTuteur.get(0).getfName());
        MainActivity.nomTuteur.setText("Nom : " + studentArrayListTuteur.get(0).getlName());
    }
}
