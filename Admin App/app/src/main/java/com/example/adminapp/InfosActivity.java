package com.example.adminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class InfosActivity extends AppCompatActivity {

    static TextView textMenu;
    static TextView prenom;
    static TextView nom;
    private Button nextButton;
    private String myList;
    private int id;
    private DisplayData displayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        textMenu = (TextView)findViewById(R.id.textMenu);
        prenom = (TextView)findViewById(R.id.prenom);
        nom = (TextView)findViewById(R.id.nom);
        nextButton = (Button)findViewById(R.id.nextButton);

        myList = (String) getIntent().getSerializableExtra("name");
       // id = (int) getIntent().getSerializableExtra("id");

        textMenu.setText("Liste " + myList);
        displayData = new DisplayData();
        //displayData.execute(id);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nextButtonEvent();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void nextButtonEvent() throws IOException, JSONException {
        Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
        //deleteCurrentUser(id);
    }


    void deleteCurrentUser(final int id) throws IOException, JSONException {
        // Instantiate the RequestQueue.
        URL url = new URL("http://yursilv.alwaysdata.net/api/queues/" + id + "/nextTicket");
        DeleteData deleteData = new DeleteData();
        //deleteData.execute(url);
        DisplayData displayData = new DisplayData();
        //displayData.execute(id);
    }
}