package com.example.adminapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button briButton;
    private Button respoButton;
    private Button tuteurButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        briButton = (Button)findViewById(R.id.briButton);
        respoButton = (Button)findViewById(R.id.respoButton);
        tuteurButton = (Button)findViewById(R.id.tuteurButton);

        briButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToBRI();
            }
        });

        respoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRespo();
            }
        });

        tuteurButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTuteur();
            }
        });
    }


    private void goToBRI() {
        intent = new Intent(MenuActivity.this, InfosActivity.class);
        intent.putExtra("name", "BRI");
        intent.putExtra("id", 1);
        startActivity(intent);
    }

    private void goToRespo() {
        intent = new Intent(MenuActivity.this, InfosActivity.class);
        intent.putExtra("name", "RESPO");
        intent.putExtra("id", 2);
        startActivity(intent);
    }

    private void goToTuteur() {
        intent = new Intent(MenuActivity.this, InfosActivity.class);
        intent.putExtra("name", "TUTEUR");
        intent.putExtra("id", 3);
        startActivity(intent);
    }
}
