package com.example.adminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class InfosActivity extends AppCompatActivity {

    static TextView textMenu;
    static TextView prenom;
    static TextView nom;
    private Button nextButton;
    private String myList;
    private int id;
   // private DisplayData displayData;

    private MqttAndroidClient mqttAndroidClient;
    private final String serveurUri = "tcp://test.mosquitto.org";
    private String clientId = MqttClient.generateClientId();
    final String subscriptionTopic = "androidAdminCurrentTicketResponse";
    final String publishTopic = "androidNextTicketRequest";
    final String publishtopicCurrent = "androidAdminCurrentTicketRequest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);
        textMenu = findViewById(R.id.textMenu);
        prenom = findViewById(R.id.prenom);
        nom = findViewById(R.id.nom);
        nextButton = findViewById(R.id.nextButton);

        myList = (String) getIntent().getSerializableExtra("name");
        id = (int) getIntent().getSerializableExtra("id");

        textMenu.setText("Liste " + myList);
        //displayData = new DisplayData();
        //displayData.execute(id);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nextButtonEvent();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                refreshName(namesub);
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


    private void nextButtonEvent() throws IOException, JSONException {
        Toast.makeText(getApplicationContext(), "Deleted !", Toast.LENGTH_SHORT).show();
        //deleteCurrentUser(id);
        publishMessage(id);
    }


    void deleteCurrentUser(final int id) throws IOException, JSONException {
        // Instantiate the RequestQueue.
        URL url = new URL("http://yursilv.alwaysdata.net/api/queues/" + id + "/nextTicket");
        DeleteData deleteData = new DeleteData();
        //deleteData.execute(url);
        DisplayData displayData = new DisplayData();
        //displayData.execute(id);
    }

    public void publishMessage(int queueId){

        try {
            MqttMessage message = new MqttMessage();
            String publishMessage = Integer.toString(queueId);
            message.setPayload(publishMessage.getBytes());
            mqttAndroidClient.publish(publishTopic,message);
            mqttAndroidClient.publish(publishtopicCurrent, message);
            Log.d("Publishes","Message Published");
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
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

    }

    public void refreshName(ArrayList<String> names){
        prenom.setText(names.get(0));
        nom.setText(names.get(1));
    }
}