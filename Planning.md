


# Caso d'uso principale

L'attore utlizza un'applicazione che periodicamente invia log di record ad un sistema centralizzato. Successivamente l'attore ispeziona i log inviati.

# Caso d'uso principale del client
L'applicazione utilizza una chiamata per inviare un record di log (non ritorna nulla)

# Caso d'uso principale del server 
La ibreria invia messaggi ad un endpoint prestabilito (con garanzia di consegna)

# Requisiti non funzionali:

* livello di aggregazione dei logging parametrizzato (quanti messaggi ogni quanto, dipendente dalla dimensione);

* dimensione massima log record;

* garanzia di ricezione (sistema di aknowledgement). Se messaggi persi, loggare l'informazione (counter);

* il log record deve supportare il message level e i tag

* il sistema deve supportare differenti tecnologie di archiviazione dei record 

# Architettura

Client/Server (publisher/subscriber) utilizzo del message broker (MQTT con QoS level 1). L'utilizzo di qos 1 implica l'utilizzo di UID nel log.
Definizione del record in Protobuf (versione già compresa) con dentro UID, timestamp


