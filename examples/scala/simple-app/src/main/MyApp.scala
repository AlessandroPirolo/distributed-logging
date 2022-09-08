import akka.actor.typed.ActorSystem
import scala.io.StdIn.readLine

object MyApp extends App {

  val mqttSubscriber: ActorSystem[MqttSubscriber.Event] =
    ActorSystem(MqttSubscriber(), "mqttSubscriber")

  println("press v to start, s to stop and q to quit")

  while (true) {
    var i = readLine()
    i match {
      case "s" => mqttSubscriber ! MqttSubscriber.Stop()
      case "v" => mqttSubscriber ! MqttSubscriber.Start()
      case "q" => {
        mqttSubscriber ! MqttSubscriber.Quit()
        sys.exit(0)
      }
      case _ => println("Behavior unhandled")
    }
  }
}
