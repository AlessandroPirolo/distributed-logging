## Table of content
-[General Info](#general-info)

-[Technologies](#technologies)

-[API docs](#api-docs)

-[Example of usage](#example-of-usage)

-[License](#license)

# General Info
The project consists in a distributed log aggregation system based on publish/subscribe model in which each component send its logs to a central component. 
Then, the central component takes care of the logs in the filesystem.

The system is partially resilient, meaning that both subscriber and publisher automatically reconnect to the broker after connection issues occure, but the subscriber isn' able to receive messages anymore, see [Bugs](#bugs) for further details.

# Technologies
-[Kotlin](https://kotlinlang.org/): version 1.7.0

-[Scala](https://www.scala-lang.org/): version 3.1.2

-[Eclipse Mosquitto](https://mosquitto.org/): version 2.0.14

# API docs

# Example of usage

# Bugs

# License

The license is Eclipse Public License 2.0, see LICENSE.txt.
