package com.example.adminapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

import java.util.ArrayList;


public class MenuActivity extends AppCompatActivity {

    private Intent intent;
    private ListView namesView;
    private MqttAndroidClient mqttAndroidClient;
    private final String serveurUri = "tcp://test.mosquitto.org";
    private String clientId = MqttClient.generateClientId();
    private final String subscriptionTopic = "QueuesResponse";
    private final String publishTopic = "androidAdminCurrentTicketRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        namesView = findViewById(R.id.namelisteid);

        namesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentliste = new Intent(MenuActivity.this, InfosActivity.class);
                String name = (String) parent.getItemAtPosition(position);
                intentliste.putExtra("name",name);
                intentliste.putExtra("id",position+1);
                publishMessage(position+1);
                startActivity(intentliste);
            }
        });

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serveurUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.d("yfjgilb", "Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    Log.d("gcjhgcj", "Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("hgchgc", "The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                Log.d("hgchjgcvh", "Incoming message payload: " + payload);
                String mes = new String(message.getPayload());
                String submes = mes.substring(1,mes.length()-1);
                String [] namessplit = submes.split(",");
                ArrayList<String> namesub = new ArrayList<>() ;

                for (String a:namessplit
                ) {
                    System.out.println(a);
                    String b = a.substring(1,a.length()-1);
                    namesub.add(b);
                }
                refreshList(namesub);
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
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("hjvjhcjh", "Failed to connect to: " + serveurUri);
                }
            });
        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }


    public void subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("gsdgzgz","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("gsdgzgz","Failed to subscribe");
                }
            });


        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
        //publishMessage();

    }

    public void publishMessage(int queueId){

        try {
            MqttMessage message = new MqttMessage();
            String publishMessage = Integer.toString(queueId);
            message.setPayload(publishMessage.getBytes());
            mqttAndroidClient.publish(publishTopic, message);
            Log.d("afafa","Message Published");
            if(!mqttAndroidClient.isConnected()){
                Log.d("kjfkujvk",mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void refreshList(ArrayList<String> names){
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MenuActivity.this,android.R.layout.simple_list_item_1,names);
        namesView.setAdapter(arrayAdapter);
    }
}