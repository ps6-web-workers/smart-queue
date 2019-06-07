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
import android.widget.Toast;

import com.example.studentapp.Models.User;
import com.example.studentapp.Utils.Executer;
import com.example.studentapp.Utils.Mqtt;
import com.example.studentapp.Utils.UserLocalStore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "MainActivityNotificationChannel";
    private static final int NOTIFICATION_ID = 1;
    private LinearLayout refresh_btn;
    private ImageView refresh_icon;
    private TextView status;
    private LinearLayout add_btn;
    private Animation refresh_animation;
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
                String[] str = messageFromMqtt.split(",");
                if (Integer.parseInt(str[0]) == userStored.getAbonnement()) {
                    if (str[1].equals(userStored.getLoggin())) {
                        refreshText(0);
                    } else {
                        refreshText(1);
                    }
                } else {
                    refreshText(1);
                }
            }
        });
        Mqtt.mqtt.subscribe("androidUpdateEtudiantStatus", new Executer() {
            @Override
            public void execute(String messageFromMqtt) {
                String[] result = gson.fromJson(messageFromMqtt, String[].class);
                Log.d(TAG, Arrays.toString(result));
                if (result[0] != null && result[1] != null) {
                    if (Integer.parseInt(result[0]) == userStored.getAbonnement() && result[1].equals(userStored.getLoggin())) {
                        refreshText(0);
                    }
                } else {
                    refreshText(1);
                }
            }
        });
//        Mqtt.mqtt.subscribe("androidIsSubcribedResponse", new Executer() {
//            @Override
//            public void execute(String messageFromMqtt) {
//                Log.d(TAG, messageFromMqtt);
//                JsonObject obj = gson.fromJson(messageFromMqtt, JsonObject.class);
//                int id = gson.fromJson(obj.get("id"), Integer.class);
//                String loggin = gson.fromJson(obj.get("loggin"), String.class);
//                boolean state = gson.fromJson(obj.get("state"), Boolean.class);
//                if (userStored.getAbonnement() == id && userStored.getLoggin().equals(loggin)) {
//                    subcribtion = state;
//                    demandIfCurrent();
//                }
//                Log.d(TAG, subcribtion.toString());
//            }
//        });

        createNotificationChannel();
        refresh_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.press_refresh_animation);

        final UserLocalStore userLocalStore = new UserLocalStore(this);
        userLocalStore.storeUserData(new User("yury", 2, "Yury", "Silvestrov-Henocq"));

        userStored = userLocalStore.getStoredUser();
        publishMessage = "{\"queue\": " + userStored.getAbonnement() + "}";

        this.status = (TextView)this.findViewById(R.id.status);

        this.refresh_btn = (LinearLayout) this.findViewById(R.id.btn);
        this.refresh_icon = (ImageView) this.findViewById(R.id.refresh_icon);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh_icon.startAnimation(refresh_animation);
                Mqtt.mqtt.publish("androidCurrentTicketRequest", publishMessage);
            }
        });

        this.queue_list = (LinearLayout)this.findViewById(R.id.queue_list);

        Mqtt.mqtt.publish("androidCurrentTicketRequest", publishMessage);

        Mqtt.mqtt.publish("androidRequestQueueList", "");
        Mqtt.mqtt.subscribe("androidRequestQueueResponse", new Executer() {
            @Override
            public void execute(String messageFromMqtt) {
                JsonArray jsonArray = gson.fromJson(messageFromMqtt, JsonArray.class);
                for(JsonElement queue : jsonArray) {
                    JsonObject obj = queue.getAsJsonObject();
                    int id = gson.fromJson(obj.get("id"), Integer.class);
                    String name = gson.fromJson(obj.get("name"), String.class);

                    final Button item = new Button(getApplicationContext());
                    item.setText(name.substring(0,3));
                    item.setTag(id);
                    item.setPadding(10, 0, 10, 0);
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userStored.setAbonnement(Integer.parseInt(item.getTag().toString()));
                            userLocalStore.storeUserData(userStored);
                            Mqtt.mqtt.publish("androidCurrentTicketRequest", publishMessage);
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
                demandInscription();
            }
        });
    }

    public void demandInscription() {
        publishMessage = "{\"queue\": " + userStored.getAbonnement() + ", \"userLogin\": \"" + userStored.getLoggin() + "\"}";
        Mqtt.mqtt.publish("androidInscriptionRequest", publishMessage);
        Toast.makeText(this, "Demande d'inscription envoyé", Toast.LENGTH_SHORT).show();
    }

    public void demandIfCurrent() {
        publishMessage = "{\"queue\": " + userStored.getAbonnement() + ", \"loggin\": \"" + userStored.getLoggin() + "\"}";
        Mqtt.mqtt.publish("androidCurrentTicketRequest", publishMessage);
    }

//    public void demandIfSubcribed(){
//        publishMessage = "{\"queue\": " + userStored.getAbonnement() + ", \"loggin\": \"" + userStored.getLoggin() + "\"}";
//        Mqtt.mqtt.publish("androidIsSubcribedRequest", publishMessage);
//    }

    private void refreshText(int message) {
        refresh_icon.startAnimation(refresh_animation);
        switch (message) {
            case 0:
                status.setText("Oui vous êtes attendu");
                showNotification("Vous êtes attendu", "Dépêchez vous!");
                break;
            case 1:
                status.setText("Non vous n'êtes pas attendu");
                break;
//            case 2:
//                status.setText("Vous n'êtes pas inscrit dans cette liste");
//                break;
            default:
                status.setText("Non vous n'êtes pas attendu");
                break;
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
                .setPriority(NotificationCompat.PRIORITY_MAX);
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
