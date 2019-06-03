package com.example.adminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

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
                nextRespoStage();
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

    public void nextRespoStage() {
        if(fetchData.studentArrayListRespoStage.size() != 0) {
            Toast.makeText(getApplicationContext(), "NEEEEEEEXT ! Respo Stage", Toast.LENGTH_SHORT).show();
            fetchData.studentArrayListRespoStage.remove(0);
            if(fetchData.studentArrayListRespoStage.size() != 0) {
                prenomRespoStage.setText("Prenom : " + fetchData.studentArrayListRespoStage.get(0).fName);
                nomRespoStage.setText("Nom : " + fetchData.studentArrayListRespoStage.get(0).lName);
            }
            else {
                prenomRespoStage.setText("Prenom : ");
                nomRespoStage.setText("Nom : ");
                Toast.makeText(getApplicationContext(), "Plus d'étudiants !", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            prenomRespoStage.setText("Prenom : ");
            nomRespoStage.setText("Nom : ");
            Toast.makeText(getApplicationContext(), "Plus d'étudiants !", Toast.LENGTH_SHORT).show();
        }

    }

    public void nextTuteur() throws IOException, JSONException {
        Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
        //fetchData.deleteCurrentUser(3);
    }
}
