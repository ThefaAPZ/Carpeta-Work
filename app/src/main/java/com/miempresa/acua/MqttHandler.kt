package com.miempresa.acua

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttHandler {
    private lateinit var client: MqttClient
    fun connect(brokerUrl: String, clientId: String) {
        try {
            val persistence = MemoryPersistence()
            client = MqttClient(brokerUrl, clientId, persistence)
            val connectOptions = MqttConnectOptions()
            connectOptions.isCleanSession = true
            client.connect(connectOptions)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    fun disconnect() {
        try {
            client.disconnect()
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    fun subscribe(topic: String) {
        try {
            client.subscribe(topic)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    fun setCallback(callback: MqttCallback) {
        client.setCallback(callback)
    }
    fun publish(topic: String, message: String) {
        try {
            val mqttMessage = MqttMessage(message.toByteArray())
            client.publish(topic, mqttMessage)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
    fun setMessageListener(listener: (String, MqttMessage) -> Unit) {
        this.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                // Aquí podemos manejar los errores de conexión o puede dejarlo vacío
            }
            override fun messageArrived(topic: String?, message: MqttMessage?)
            {
                message?.let {
                    listener.invoke(topic ?: "", it)
                }
            }
            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // Aquí podemos manejar la entrega completa o sino déjelo vacío
            }
        })
    }
}
