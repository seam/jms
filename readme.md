#Seam JMS Module

Provides injectable JMS resources and bridges the CDI event bus over JMS.

For more information, see the [Seam JMS Module page](http://seamframework.org/Seam3/JMS).

##Building

   mvn clean install

##Testing

   Currently supported application servers: JBoss AS 6 Final.  Two profiles exist to facilitate testing:
      jbossas-remote-6
      jbossas-managed-6

   In order for tests dealing with QueueReceivers to pass HornetQ security must be disabled.  See SEAMJMS-13 for more information.
   
##How to release

   To create the zip distribution, run mvn clean install -Pdistribution - the resulting zip is located in dist/target/seam-jms-${version}.zip 
