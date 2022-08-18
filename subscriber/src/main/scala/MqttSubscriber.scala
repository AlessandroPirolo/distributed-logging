import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.{KillSwitches, Materializer, SystemMaterializer, UniqueKillSwitch}
import akka.stream.alpakka.mqtt.scaladsl.MqttSource
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttQoS, MqttSubscriptions}
import akka.stream.scaladsl.{Keep, Sink}
import akka.util.ByteString
import com.google.protobuf.timestamp.Timestamp
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.time.{Instant, ZoneId}
import protobuf.log.Log.Tag
import protobuf.log.Log.LogLevel
import protobuf.log.Log

object MqttSubscriber {

  sealed trait Event

  final case class Start() extends Event
  final case class Stop() extends Event
  final case class Quit() extends Event

  sealed trait Data

  final case class Tostop(killSwitch: UniqueKillSwitch, materializer: Materializer) extends Data
  final case class LogRecord(logLevel: Int, msg: String, tags: Map[String, String], timestamp: Timestamp) extends Data {
    private val tmpTags: List[Tag] = tags.map( el => Tag(el._1, el._2)).toList

    private val tmpLogLevel = logLevel match {
      case 0 => LogLevel.TRACE
      case 1 => LogLevel.DEBUG
      case 2 => LogLevel.INFORMATION
      case 3 => LogLevel.WARNING
      case 4 => LogLevel.ERROR
      case 5 => LogLevel.CRITICAL
      case 6 => LogLevel.NONE
      case _ => LogLevel.INFORMATION
    }

    private val log = Log(tmpLogLevel, msg, tmpTags, Some(timestamp))

    override def toString: String = {
      val date = Instant
        .ofEpochSecond(log.timeStamp.get.seconds, log.timeStamp.get.nanos)
        .atZone(ZoneId.of("Europe/Rome"))
        .toLocalDateTime

      log.logLevel.toString +
        " " + date.toString +
        " " + log.msg +
        " " + log.tag.toList
    }
  }

  private def parseFrom(byteString: ByteString): LogRecord = {
    val tmpLog = Log.parseFrom(byteString.toArrayUnsafe())
    LogRecord(tmpLog.logLevel.value, tmpLog.msg, tmpLog.tag.map(tag => tag.key -> tag.value).toMap, log.timeStamp)
  }

  //implicit private val system: ActorSystem = ActorSystem("QuickStart")

  private val connectionSettings: MqttConnectionSettings = MqttConnectionSettings(
    "tcp://localhost:1883",
    "test-scala-client",
    new MemoryPersistence
  )

  private val graph = MqttSource.atLeastOnce(
    connectionSettings
      .withClientId(clientId = "id")
      .withCleanSession(true),
    MqttSubscriptions("ciao", MqttQoS.AtLeastOnce),
    bufferSize = 8
  ).viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.foreach(m => {
      val log = parseFrom(m.message.payload)
      println(log.toString)
    })
    )(Keep.left)

  def apply(): Behavior[Event] = Behaviors.setup{ context =>
    idle(SystemMaterializer(context.system).materializer)
  }

  private def idle(implicit materializer: Materializer): Behavior[Event] = Behaviors.receiveMessage[Event] {
    case Start() =>
      println("Running")
      println("Press s to stop")
      val killSwitch = graph.run()
      running(Tostop(killSwitch, materializer))
    case Quit() =>
      println("Killing the service...")
      Behaviors.stopped
    case _ =>
      println("Behavior unhandled")
      Behaviors.unhandled
  }

  private def running(data: Tostop) : Behavior[Event] =
    Behaviors.receiveMessage[Event] {
      case Stop() =>
        println("Stopped")
        println("Press v to restart")
        data.killSwitch.shutdown()
        idle(data.materializer)
      case Quit() =>
        println("Killing the service...")
        Behaviors.stopped
      case _ =>
        println("Behavior unhandled")
        Behaviors.unhandled
    }




}