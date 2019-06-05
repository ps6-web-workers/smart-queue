package com.example.studentapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.studentapp.Models.Ticket;
import com.example.studentapp.Models.User;
import com.example.studentapp.Utils.Mqtt;
import com.example.studentapp.Utils.UserLocalStore;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "MainActivityNotificationChannel";
    private static final int NOTIFICATION_ID = 1;
    private LinearLayout refresh_btn;
    private ImageView refresh_icon;
    private TextView status;
    private final Gson gson = new Gson();
    private User userStored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mqtt mqtt = new Mqtt();
        mqtt.currentTicket("2");

        final UserLocalStore userLocalStore = new UserLocalStore(this);
        userLocalStore.storeUserData(new User("yury", 1, "Yury", "Silvestrov-Henocq"));
        userStored = userLocalStore.getStoredUser();

        this.status = this.findViewById(R.id.status);
        this.refresh_btn = this.findViewById(R.id.btn);
        this.refresh_icon = this.findViewById(R.id.refresh_icon);

//        refresh.setVisibility(View.GONE);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.press_refresh_animation);
                refresh_icon.startAnimation(animation);
                refreshText();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void refreshText() {
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                // Instantiate the RequestQueue.
                String url = "http://yursilv.alwaysdata.net/api/queues/" + userStored.getAbonnement() + "/currentTicket";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                /*Type data_type = new TypeToken<ArrayList<Queue>>(){}.getType();
                                ArrayList<Queue> data = gson.fromJson(response.toString(), data_type);
                                StringBuilder str = new StringBuilder();
                                for (Queue q : data) {
                                    str.append(q.toString() + "\n");
                                }*/
                                Ticket currentTicket = gson.fromJson(response.toString(), Ticket.class);
                                if (currentTicket.getUserLogin().equals(userStored.getLoggin())) {
                                    status.setText("Oui vous êtes attendu");
                                    showNotification("Vous êtes attendu", "Dépêchez vous!");
                                } else {
                                    status.setText("Non vous n'êtes pas attendu");
                                }
                                Log.d(TAG, currentTicket.toString());
                                Log.d(TAG, userStored.toString());
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                status.setText("Oups! ça marche pas très bien...");
                                Log.e(TAG, error.toString());
                            }
                        });
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Main activity notification channel";
            String description = "Notification channel of the main activity";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification(String title, String content) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, CHANNEL_ID)
                .setSmallIcon(R.drawable.check)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Log.d("Main Activity:", "test notification pushed");
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}
