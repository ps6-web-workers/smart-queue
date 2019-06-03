package com.example.adminapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView queues ;
    private List<String> queuesnames ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queues = findViewById(R.id.queues);
        queuesnames = new ArrayList<>();
        queuesnames.add("BRI");
        queuesnames.add("Responsable de stage");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,queuesnames);
        queues.setAdapter(adapter);
        queues.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,listes_des_etudiants.class);
                intent.putExtra("nomdelaliste",queuesnames.get(position));
                startActivity(intent);
            }
        });
    }
}
