package com.example.studentapp.Utils;

import android.content.Context;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Mqtt {
    public static Mqtt mqtt = null;
    public final String TAG = "MQTT";
    private int qos;
    private MqttConnectOptions connOpts;
    private String broker;
    private String clientId;
    private MqttAndroidClient client;
    private MqttCallback mqttCallback;
    private List<Publish> publishQueue;
    private List<Subscribe> subscribeList;

    public static void initialize(Context context) {
        if (mqtt == null) {
            mqtt = new Mqtt(context);
        }
    }

    public Mqtt(Context context) {
        publishQueue = new ArrayList<>();
        subscribeList = new ArrayList<>();

        qos = 2;
        broker = "tcp://172.20.10.14:1883";
        clientId = MqttClient.generateClientId();
        connOpts = new MqttConnectOptions();
        connOpts.setAutomaticReconnect(true);
        connOpts.setCleanSession(true);

        try {
            client = new MqttAndroidClient(context, broker, clientId);
            client.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    client.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopics();
                    publishToTopics();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Failed to connect to: " + broker);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                try {
                    client.connect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                for (int i = 0; i < subscribeList.size(); i++) {
                    Subscribe s = subscribeList.get(i);
                    if (s.topic.equals(topic))  {
                        s.executer.execute(new String(message.getPayload()));
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };

        client.setCallback(mqttCallback);
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToTopics() {
        for (final Subscribe topic : subscribeList) {
            try {
                client.subscribe(topic.topic, qos, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "Subscribed! --> " + topic.topic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "Failed to subscribe");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void publishToTopics() {
        for (Publish publish : publishQueue) {
            try {
                client.publish(publish.topic, publish.message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private MqttMessage newMqttMessage(String content) {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        return message;
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = newMqttMessage(message);
            if (client.isConnected()) {
                client.publish(topic, mqttMessage);
            } else {
                Publish publish = new Publish(topic, mqttMessage);
                publishQueue.add(publish);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(final String topic, Executer executer) {
        try {
            if (client.isConnected()) {
                client.subscribe(topic, qos, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "Subscribed! --> " + topic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "Failed to subscribe");
                    }
                });
            } else {
                Subscribe subscribe = new Subscribe(topic, executer);
                subscribeList.add(subscribe);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private class Publish {
        String topic;
        MqttMessage message;

        Publish(String topic, MqttMessage message) {
            this.topic = topic;
            this.message = message;
        }
    }

    private class Subscribe {
        String topic;
        Executer executer;

        Subscribe(String topic, Executer executer) {
            this.topic = topic;
            this.executer = executer;
        }
    }
}