import akka.actor.ActorSystem
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttQoS, MqttSubscriptions}
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence



object MqttSubscriber {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("QuickStart")

    val connectionSettings: MqttConnectionSettings = MqttConnectionSettings(
      "tcp://localhost:1883",
      "test-scala-client",
      new MemoryPersistence
    )

    MqttSource.atLeastOnce(
      connectionSettings
        .withClientId(clientId = "id")
        .withCleanSession(true),
      MqttSubscriptions("ciao", MqttQoS.AtLeastOnce),
      bufferSize = 8
    ).runForeach { m =>
      println(m.message.payload.toString())
      m.ack()
    }

  }

}
