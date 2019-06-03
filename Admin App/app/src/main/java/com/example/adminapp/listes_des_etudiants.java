package com.example.adminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class listes_des_etudiants extends AppCompatActivity {
    private TextView nomdelaliste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listes_des_etudiants);
        nomdelaliste = findViewById(R.id.textView);
        nomdelaliste.setText(String.valueOf(getIntent().getSerializableExtra("nomdelaliste")));
    }
}
