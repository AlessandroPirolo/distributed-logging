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

## pirale.sharedLogger.publisher

An API for publish lists of logs to a MQTT broker.

### Interfaces

#### LogRecordSerializer

Log serializer for serializing and deserielizing log. It provides two method:
- `toByteArray(logRecord: LogRecord)`: transform a `LogRecord` in a `ByteArray`, necessary for sending data to the MQTT broker.
- `parseFrom(arr: ByteArray)`: trasform a `ByteArray` in a `LogRecord`.

#### SharedLogger

A shared logger. It provides one method:
- `put(logRecord: LogRecord)`: send to a MQTT client the log to publish.

### Classes

#### LogRecord

Data class for representing a log.

#### SharedLoggerFactory

Factory for creating a shared logger. It provides one method:
- `create()`: creates a shared logger according to the properties chosen by the user.

## pirale.sharedLogger.publisher.impl

Internal implementations of shared loggers.

### Classes

#### DummySharedLogger

A dummy shared logger which simply print in console the log we send.

#### SimpleSharedLogger

A simple shared logger which send a single log to a MQTT client class.

#### QueudSharedLogger

A shared logger which send a list of log to a MQTT client class. 

The class uses channels and flows for concurrently publishing logs and receiving logs to publish. Furthermore, in case of connection issues, the shared logger starts to discard the logs and keep count of them, and, after the connection has been reestablished, it tries to publish an error log to the broker.

## pirale.sharedLogger.publisher.mqtt

This package contains the MQTT client class.

### Classes

#### LogRecordListMqttClient

Class representing a MQTT client. It has one public method:
- `publish(logs: MutableList<LogRecord>)`: serialize and publish a list of `LogRecord` to a MQTT broker.

## pirale.sharedLogger.publisher.serialization

This package contains the log serializer.

### Classes

#### LogRecordPBSerializer

Concrete log serializer class implementing `LogRecordSerializer` and its methods.

## pirale.sharedLogger.subscriber

An API for receiving logs from a MQTT broker.

### Objects

#### MqttSubscriber

A subscriber to a MQTT broker.
  
`MqttSubscriber` is an actor used to model a Finite State Machine (FSM). The possible states are the following:
- `Running`: corresponds to receive the event `Start()` and it simply receives continuously logs from the broker until a different event is sent. Logs which are receveid are then saved in the filesytem according to the configuration file `logback.xml`.
- `Stopped`: corresponds to receive the event `Stop()`. The actor is paused until it receives some events.

### Abstract classes

#### LogSerializer

Log serializer for serializing and deserielizing log. It provides two method:
- `toByte(logRecord: LogRecord)`: transform a `LogRecord` in a `Array[Byte]`.
- `parseFrom(arr: Array[Byte])`: trasform a `Array[Byte]` in a `LogRecord`, necessary for receiving data from the MQTT broker.

### Classes

#### LogRecord

Case class for representing a log.

## pirale.sharedLogger.subscriber.serialization

### Classes

#### LogRecordPBSerializer

Concrete log serializer class implementing `LogSerializer` and its methods.

# Example of usage

# Bugs

# License

The license is Eclipse Public License 2.0, see LICENSE.txt.
