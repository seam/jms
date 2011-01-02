Building
--------

* Run mvn clean install
* To create the zip distribution, run mvn clean install -Pdistribution - the resulting zip is located in dist/target/seam-jms-${version}.zip 
* The readme.txt  placed in the distribution is not this one, see dist/src/main/assembly/readme.txt 

* Please note: if attempting to test the examples using JBossAS Managed 6 profile, then you must copy the the impl/src/test/resources/resources-jbossas/hornetq-jms.xml in to your server, or manually create the queue and topic.
