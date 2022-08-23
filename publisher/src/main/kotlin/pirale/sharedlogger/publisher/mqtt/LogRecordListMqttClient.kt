package pirale.sharedlogger.publisher.mqtt

import org.eclipse.paho.client.mqttv3.*
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.LogRecordSerializer

class LogRecordListMqttClient(
    private val client: IMqttClient,
    private val topic: String,
    private val options: MqttConnectOptions,
    private val serializer: LogRecordSerializer) {


    /*object ActionListener: IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            errorCount = 0

            println("Success")
            println("***** ${logQueue.count()} log records have been sent ******")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            println("Error, failure")

            errorCount += 1 /*logQueue.count()*/
            errorLog = LogRecord(3,"$errorCount has been lost due to connection error", mapOf("type" to "logs removed"), LocalDateTime.now())
            println("---------------------------------------")
            println(errorLog)
            println("---------------------------------------")
        }
    }*/
    init {
        connect()
    }

    private fun connect() {
        try {
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    println("conn Completed")
                    println("Connected to: $serverURI")
                }

                override fun connectionLost(cause: Throwable) {
                    println("The Connection was lost.")
                    cause.printStackTrace();
                    println("Trying to reconnect...")
                    //client.reconnect()
                }

                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    /*println("****************************************")
                    println("Incoming message from ${message.payload}")*/
                    println("****************************************")
                }

                /* Called when an ack is received*/
                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    //println("On delivery complete" + token.message.payload.toString())
                    println(token.isComplete)
                }
            })

            client.connect(options)

        } catch (e: MqttException) {
            println("reason: "+e.reasonCode);
            println("msg: "+e.message);
            println("loc: "+e.localizedMessage);
            println("cause: "+e.cause);
            println("excep: $e");
        }

        client.subscribe(topic, 1)
        println("Subscription done")
    }

    fun publish(logQueue: MutableList<LogRecord>) {
        val message = MqttMessage()
        /*val payload = logQueue.remove().map { lr ->
                serializer.toByteArray(lr)
            }.reduce{ b1,b2 -> b1 + b2}*/
        //val el = logQueue.remove()[0]
        //val payload = serializer.toByteArray(el)
        message.payload = serializer.toByteArray(logQueue[0])

        println("Publishing...")
        if (client.isConnected) {
            println("is connected")
            client.publish(topic, message.payload, 1, false)
        }
        else {
            client.reconnect()
        }
    }

    fun disconnect() {
        client.disconnect()
    }
}