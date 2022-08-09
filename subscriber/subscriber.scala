val connectionSettings = MqttConnectionSettings(
  "tcp://localhost:1883", 
  "scala-client", 
  new MemoryPersistence 
)