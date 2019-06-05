package com.example.studentapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentapp.Models.User;
import com.example.studentapp.Utils.Mqtt;
import com.example.studentapp.Utils.UserLocalStore;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private static final String CHANNEL_ID = "MainActivityNotificationChannel";
    private static final int NOTIFICATION_ID = 1;
    private LinearLayout refresh_btn;
    private ImageView refresh_icon;
    private TextView status;
    private final Gson gson = new Gson();
    private User userStored;

    private MqttAndroidClient mqttAndroidClient;
    private final String serveurUri = "tcp://test.mosquitto.org:1883";
    private String clientId = MqttClient.generateClientId();
    private final String subscriptionTopic = "androidCurrentTicketResponse";
    private final String publishTopic = "androidCurrentTicketRequest";
    private String publishMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serveurUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.d(TAG, "Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    Log.d(TAG, "Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                Log.d(TAG, "Incoming message payload: " + payload);
                refreshText(Integer.parseInt(payload));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                    demandIfCurrent();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to connect to: " + serveurUri);
                }
            });
        } catch (MqttException ex){
            ex.printStackTrace();
        }

        createNotificationChannel();

        Mqtt mqtt = new Mqtt();
        mqtt.currentTicket("2");

        final UserLocalStore userLocalStore = new UserLocalStore(this);
        userLocalStore.storeUserData(new User("yury", 1, "Yury", "Silvestrov-Henocq"));
        userStored = userLocalStore.getStoredUser();

        publishMessage = "{\"queue\": " + userStored.getAbonnement() + ", \"loggin\": \"" + userStored.getLoggin() + "\"}";

        this.status = (TextView)this.findViewById(R.id.status);
        this.refresh_btn = (LinearLayout) this.findViewById(R.id.btn);
        this.refresh_icon = (ImageView) this.findViewById(R.id.refresh_icon);

//        refresh.setVisibility(View.GONE);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.press_refresh_animation);
                refresh_icon.startAnimation(animation);
                demandIfCurrent();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed! --> " + subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to subscribe");
                }
            });

        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void demandIfCurrent(){
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            mqttAndroidClient.publish(publishTopic, message);
            Log.d(TAG, "Message Published");
            if(!mqttAndroidClient.isConnected()){
                Log.d(TAG, mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshText(int message) {
//        this.status.setText(result);
        if (message == 0) {
            status.setText("Oui vous êtes attendu");
            showNotification("Vous êtes attendu", "Dépêchez vous!");
        } else {
            status.setText("Non vous n'êtes pas attendu");
        }
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                // Instantiate the RequestQueue.
//                String url = "http://yursilv.alwaysdata.net/api/queues/" + userStored.getAbonnement() + "/currentTicket";
//
//                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                /*Type data_type = new TypeToken<ArrayList<Queue>>(){}.getType();
//                                ArrayList<Queue> data = gson.fromJson(response.toString(), data_type);
//                                StringBuilder str = new StringBuilder();
//                                for (Queue q : data) {
//                                    str.append(q.toString() + "\n");
//                                }*/
//                                Ticket currentTicket = gson.fromJson(response.toString(), Ticket.class);
//                                if (currentTicket.getUserLogin().equals(userStored.getLoggin())) {
//                                    status.setText("Oui vous êtes attendu");
//                                    showNotification("Vous êtes attendu", "Dépêchez vous!");
//                                } else {
//                                    status.setText("Non vous n'êtes pas attendu");
//                                }
//                                Log.d(TAG, currentTicket.toString());
//                                Log.d(TAG, userStored.toString());
//                            }
//                        }, new Response.ErrorListener() {
//
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // TODO: Handle error
//                                status.setText("Oups! ça marche pas très bien...");
//                                Log.e(TAG, error.toString());
//                            }
//                        });
//
//                // Access the RequestQueue through your singleton class.
//                singleton.addToRequestQueue(jsonObjectRequest);
//            }
//        });
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
