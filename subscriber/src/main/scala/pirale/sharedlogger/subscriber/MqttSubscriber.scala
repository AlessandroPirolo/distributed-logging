package pirale.sharedlogger.subscriber

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttQoS, MqttSubscriptions}
import akka.stream.scaladsl.{Keep, Sink}
import akka.stream.{KillSwitches, Materializer, SystemMaterializer, UniqueKillSwitch}
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import pirale.sharedlogger.subscriber.serialization.LogPBSerializer

import scala.concurrent.duration.FiniteDuration


object MqttSubscriber {

  sealed trait Event

  final case class Start() extends Event

  final case class Stop() extends Event

  final case class Quit() extends Event

  sealed trait Data

  final case class ToStop(killSwitch: UniqueKillSwitch, mat: Materializer) extends Data

  //implicit private val system: ActorSystem = ActorSystem("QuickStart")

  private val serializer: LogSerializer = new LogPBSerializer()

  private val connectionSettings: MqttConnectionSettings = MqttConnectionSettings(
    "tcp://localhost:1883",
    "test-scala-client",
    new MemoryPersistence
  )

  private val graph = MqttSource.atLeastOnce(
    connectionSettings
      .withClientId(clientId = "id")
      .withCleanSession(true)
      //.withAutomaticReconnect(true)
      //.withConnectionTimeout(FiniteDuration(50, "millis")),
      ,
    MqttSubscriptions("ciao", MqttQoS.AtLeastOnce),
    bufferSize = 8
  ).viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.foreach(m => {
      val log = serializer.parseFrom(m.message.payload.toArrayUnsafe())
      println(log)
    })
    )(Keep.left)

  def apply(): Behavior[Event] = Behaviors.setup { context =>
    idle(SystemMaterializer(context.system).materializer)
  }

  private def idle(implicit mat: Materializer): Behavior[Event] = Behaviors.receiveMessage[Event] {
    case Start() =>
      println("Running")
      println("Press s to stop")
      val killSwitch = graph.run()
      running(ToStop(killSwitch, mat))
    case Quit() =>
      println("Killing the service...")
      Behaviors.stopped
    case _ =>
      println("Behavior unhandled")
      Behaviors.unhandled
  }

  private def running(data: ToStop): Behavior[Event] =
    Behaviors.receiveMessage[Event] {
      case Stop() =>
        println("Stopped")
        println("Press v to restart")
        data.killSwitch.shutdown()
        idle(data.mat)
      case Quit() =>
        println("Killing the service...")
        Behaviors.stopped
      case _ =>
        println("Behavior unhandled")
        Behaviors.unhandled
    }


}
