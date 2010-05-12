Seam JMS
=============

http://www.seamframework.org/Seam3/JMSModule

Seam extends the CDI programming model into the messaging world by allowing you
to inject JMS resources into your beans. Further, Seam bridges the CDI event 
bus over JMS; this gives you the benefits of CDI-style type-safety for 
inter-application communication.

The general goals can be divided into two categories: injection of JMS 
resources and forwarding of events.

Injection of JMS Resources
 - Connection
 - Session
 - Destination (Topic/Queue)
 - Message Producer
 - Message Consumer

Event Bridge
 - Egress: Routes CDI events to JMS destinations
 - Ingress: Fires CDI events based on the reception of JMS messages