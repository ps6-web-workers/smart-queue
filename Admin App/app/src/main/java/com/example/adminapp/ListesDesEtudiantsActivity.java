package com.example.adminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListesDesEtudiantsActivity extends AppCompatActivity {

    private ListView students;
    private List<String> namestudents = new ArrayList<>();
    private Button nextbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listes_des_etudiants);
        nextbutton = findViewById(R.id.nextbutton);
        students = findViewById(R.id.students);
        namestudents.add("Paul");
        namestudents.add("Nouamane");
        namestudents.add("Romain");
        namestudents.add("Yury");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,namestudents);
        students.setAdapter(adapter);

    }

    public void nextmethod(){
    }
}
