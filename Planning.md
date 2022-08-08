Requisiti non funzionali:

1 livello di aggregazione dei logging parametrizzato (quanti messaggi ogni quanto, dipendente dalla dimensione);

2 dimensione massima log record;

3 garanzia di ricezione (sistema di aknowledgement). Se messaggi persi, loggare l'informazione (counter);

4 Il log record deve supportare il message level e i tag

5 il sistema deve supportare differenti tecnologie di archiviazione dei record 


Caso d'uso principale

L'attore utlizza un'applicazione che periodicamente invia log di record ad un sistema centralizzato. Successivamente l'attore ispeziona i log inviati.


Architettura

Client/Server (publisher/subscriber) utilizzo del message broker (MQTT con QoS level 1). L'utilizzo di qos 1 implica l'utilizzo di UID nel log.
Definizione del record in Protobuf (versione già compresa) con dentro UID, timestamp

Caso d'uso principale del client ----> l'applicazione utilizza una chiamata per inviare un record di log (non ritorna nulla)


Caso d'uso principale del server ----> la libreria invia messaggi ad un endpoint prestabilito (con garanzia di consegna)
