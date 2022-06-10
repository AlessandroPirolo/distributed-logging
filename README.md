## Table of content
[General Info](#general-info)

[Technologies](#technologies)

# General Info
The project consists in a distributed log aggregation system based on publish/subscribe model in which each component send its logs to a central component. 
Then, the central component takes care of arranging the logs in chronological order.

The system is resilient and the receiving of logs is guaranteed, meaning that if a log has been sent and no response has been given, then the component 
must keep sending the log until it receives an acknowledgement message. 

# Technologies
[Kotlin](https://kotlinlang.org/): version 1.7.0

[Scala](https://www.scala-lang.org/): version 3.1.2

[Eclipse Mosquitto](https://mosquitto.org/): version 2.0.14
