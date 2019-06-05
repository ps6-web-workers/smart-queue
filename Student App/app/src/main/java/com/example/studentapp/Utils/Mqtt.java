package com.example.studentapp.Utils;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Mqtt {
    private int qos;
    private MqttClient client;

    public Mqtt() {
        qos = 2;
        String broker = "tcp://test.mosquitto.org";
        String clientId = MqttClient.generateClientId();
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);
        } catch(MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttMessage newMessage(String content) {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        return message;
    }

    public void currentTicket(String queueId) {
        String topic = "currentTicketRequest";
        MqttMessage message = newMessage(queueId);

        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}