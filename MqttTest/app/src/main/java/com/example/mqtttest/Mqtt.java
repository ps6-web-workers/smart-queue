package com.example.mqtttest;

import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Mqtt implements MqttCallback {
    private int qos;
    private AppCompatActivity main;
    private MqttConnectOptions connOpts;
    private String broker;
    private String clientId;
    private MemoryPersistence persistence;

    public Mqtt(AppCompatActivity main) {
        this.main = main;
        qos = 2;
        broker = "tcp://test.mosquitto.org";
        clientId = MqttClient.generateClientId();
        persistence = new MemoryPersistence();
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
    }

    private MqttMessage newMessage(String content) {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        return message;
    }

    public void currentTicket(String queueId) {
        String topic = "currentTicketRequest";
        publish(topic, queueId);
    }

    private void publish(String topic, String message) {
        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
            client.connect(connOpts);
            client.setCallback(this);
            client.subscribe("queuesResponsePretty");
            MqttMessage mqttMessage = newMessage(message);
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void queues() {
        String topic = "queuesRequest";
        publish(topic, "");
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        TextView htmlToTextView = main.findViewById(R.id.queues);
        htmlToTextView.setText(HtmlCompat.fromHtml(new String(message.getPayload()), 0));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}