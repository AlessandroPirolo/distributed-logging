## Table of content
[General Info](#general-info)

[Technologies](#technologies)

# General Info
The project consists in a distributed log aggregation system based on publish/subscribe model in which each component send its logs to a central component. 
Then, the central component takes care of arranging the logs in chronological order.

The system is resilient and the receiving of logs is guaranteed, meaning that if a log has been sent and no response has been given, then the component 
must keep sending the log until it receives an acknowledgement message. 

# Technologies
The project is developed in **Kotlin** and **Scala**. And it uses **Eclipse** **Mosquito** message broker through which each component
can communicate with the central one.
