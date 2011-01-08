
 Seam JMS Module
 3.0.0.Alpha2

 What is it?
 ===========

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


 Contents of distribution
 ========================

 artifacts/
 
    Provided libraries

 lib/

    Dependencies

 docs/

    API Docs and reference guide
  
 examples/

    Examples for module
  
 Licensing
 =========

 This distribution, as a whole, is licensed under the terms of the Apache
 Software License, Version 2.0 (ASL).

 Seam JMS URLs
 =============

 JMS Module page:     http://sfwk.org/Seam3/JMS
 Seam 3 project:      http://sfwk.org/Seam3
 Downloads:           http://sfwk.org/Seam3/DistributionDownloads
 Forums:              http://sfwk.org/Community/Seam3Users
 Source Code:         http://github.com/seam/jms
 Issue Tracking:      http://issues.jboss.org/browse/SEAMJMS

 Release Notes
 =============

