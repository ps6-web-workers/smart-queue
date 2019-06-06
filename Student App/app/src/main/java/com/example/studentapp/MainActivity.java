package com.example.studentapp;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentapp.Models.User;
import com.example.studentapp.Utils.Executer;
import com.example.studentapp.Utils.Mqtt;
import com.example.studentapp.Utils.UserLocalStore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "MainActivityNotificationChannel";
    private static final int NOTIFICATION_ID = 1;
    private LinearLayout refresh_btn;
    private ImageView refresh_icon;
    private TextView status;
    private LinearLayout add_btn;
    private final Gson gson = new Gson();
    private User userStored;
    private LinearLayout queue_list;
    private String publishMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mqtt.initialize(this);

        Mqtt.mqtt.subscribe("androidCurrentTicketResponse", new Executer() {
            @Override
            public void execute(String messageFromMqtt) {
                refreshText(Integer.parseInt(messageFromMqtt));
            }
        });
        Mqtt.mqtt.subscribe("androidUpdateEtudiantStatus", new Executer() {
            @Override
            public void execute(String messageFromMqtt) {
                String[] result = gson.fromJson(messageFromMqtt, String[].class);
                if (result[0] != null && result[1] != null) {
                    if (Integer.parseInt(result[0]) == userStored.getAbonnement() && result[1].equals(userStored.getLoggin())) {
                        refreshText(0);
                    }
                } else {
                    refreshText(1);
                }
            }
        });

        createNotificationChannel();

        final UserLocalStore userLocalStore = new UserLocalStore(this);
        userLocalStore.storeUserData(new User("yury", 1, "Yury", "Silvestrov-Henocq"));

        userStored = userLocalStore.getStoredUser();

        this.status = (TextView)this.findViewById(R.id.status);

        this.refresh_btn = (LinearLayout) this.findViewById(R.id.btn);
        this.refresh_icon = (ImageView) this.findViewById(R.id.refresh_icon);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.press_refresh_animation);
                refresh_icon.startAnimation(animation);
                demandIfCurrent("" + userStored.getAbonnement());
            }
        });

        this.queue_list = (LinearLayout)this.findViewById(R.id.queue_list);

        demandIfCurrent("" + userStored.getAbonnement());

        Mqtt.mqtt.publish("androidRequestQueueList", "");
        Mqtt.mqtt.subscribe("androidRequestQueueResponse", new Executer() {
            @Override
            public void execute(String messageFromMqtt) {
                Log.d(TAG, messageFromMqtt);
                JsonArray jsonArray = gson.fromJson(messageFromMqtt, JsonArray.class);
                Log.d(TAG, jsonArray.toString());
                for(JsonElement queue : jsonArray) {
                    JsonObject obj = queue.getAsJsonObject();
                    int id = gson.fromJson(obj.get("id"), Integer.class);
                    String name = gson.fromJson(obj.get("name"), String.class);

                    final Button item = new Button(getApplicationContext());
                    item.setText(name);
                    item.setTag(id);
                    item.setPadding(10, 0, 10, 0);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userStored.setAbonnement(Integer.parseInt(item.getTag().toString()));
                            userLocalStore.storeUserData(userStored);
                            demandIfCurrent("" + userStored.getAbonnement());
                            refresh_list();
                        }
                    });
                    queue_list.addView(item);
                }
                refresh_list();
            }
        });

        this.add_btn = (LinearLayout)this.findViewById(R.id.add_btn);
        this.add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.add_btn = (LinearLayout)this.findViewById(R.id.add_btn);
        this.add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demandInscription("" + userStored.getAbonnement());
            }
        });
    }

    public void demandInscription(String queueId) {
        publishMessage = "{\"queue\": " + queueId + ", \"userLogin\": \"" + userStored.getLoggin() + "\"}";
        Mqtt.mqtt.publish("androidInscriptionRequest", publishMessage);
    }

    public void demandIfCurrent(String queueId){
        publishMessage = "{\"queue\": " + queueId + ", \"loggin\": \"" + userStored.getLoggin() + "\"}";
        Mqtt.mqtt.publish("androidCurrentTicketRequest", publishMessage);
    }

    private void refreshText(int message) {
        if (message == 0) {
            status.setText("Oui vous êtes attendu");
            showNotification("Vous êtes attendu", "Dépêchez vous!");
        } else {
            status.setText("Non vous n'êtes pas attendu");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Main activity notification channel";
            String description = "Notification channel of the main activity";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
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

    private void refresh_list() {
        final int childCount = queue_list.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = queue_list.getChildAt(i);
            if (Integer.parseInt(v.getTag().toString()) == userStored.getAbonnement()) {
                v.setBackgroundColor(getResources().getColor(R.color.colorDark));
            } else {
                v.setBackgroundColor(getResources().getColor(R.color.colorLight));
            }
        }
    }
}
