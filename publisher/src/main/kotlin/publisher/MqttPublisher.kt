package publisher

import com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener
import org.eclipse.paho.client.mqttv3.*
import kotlinx.coroutines.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent


fun main() {

    var client: MqttClient = MqttClient("tcp://127.0.0.1:1883", MqttClient.generateClientId())
    val topic = "ciao"
    val qos = 1

    println("Press ENTER to quit")

    /** Trying to connect to the broker **/
    try {
        val option = MqttConnectOptions()
        option.isCleanSession = true
        client.connect(option)
        client.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                println("conn Comple")
                println("Connected to: $serverURI")
            }

            override fun connectionLost(cause: Throwable) {
                println("The Connection was lost.")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                println("Incoming message from $topic: $message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })
    } catch (e: MqttException) {
        println("e")
        e.printStackTrace()
    }

    addKeyListener(object : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            if (e.keyCode == KeyEvent.VK_ENTER) {
                client.close()
                client.disconnect()
            }
        }
    })


            /** Subscription to topic **/
    client.subscribe(topic, qos)

    val publications = GlobalScope.launch {
        while (true) {
            /** Trying to publish a message **/
            try {
                val msg = "ciao"
                val message = MqttMessage()
                message.payload = msg.toByteArray()
                client.publish(topic, message.payload, 1, true)
                println("$msg published to $topic")
            } catch (e: MqttException) {
                println("Error Publishing to $topic: " + e.message)
                e.printStackTrace()
            }
            delay(2000L)
        }
    }



}



