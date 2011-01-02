Building
--------

* Run mvn clean install
* To create the zip distribution, run mvn clean install -Pdistribution - the resulting zip is located in dist/target/seam-jms-${version}.zip 
* The readme.txt  placed in the distribution is not this one, see dist/src/main/assembly/readme.txt 

Testing
-------

* Currently supported application servers: JBoss AS 6 Final.  Two profiles exist to facilitate testing:
   jbossas-remote-6
   jbossas-managed-6

** In order for tests dealing with QueueReceivers to pass HornetQ security must be disabled.  See SEAMJMS-13 for more information.
