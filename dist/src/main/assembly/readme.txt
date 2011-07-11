
 Seam JMS Module
 ${project.version}

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

 doc/

    API Docs and reference guide
  
 examples/

    Examples for module

 lib/

    Dependencies
        
 source/
 
    Source code for this module
  
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
 Source Code:         https://github.com/seam/jms
 Issue Tracking:      https://issues.jboss.org/browse/SEAMJMS

 Release Notes
 =============
 For release notes visit https://issues.jboss.org/secure/ConfigureReport.jspa?atl_token=3a4f2ddb0a82d411fc73c0d25b896263d3c02df3&versions=12314867&sections=all&style=html&selectedProjectId=12311010&reportKey=org.jboss.labs.jira.plugin.release-notes-report-plugin%3Areleasenotes&Next=Next
