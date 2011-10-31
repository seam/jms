Seam JMS StatusWatcher example
==============================

To run the example on JBossAS 6 you need to do the following:

* add this line to $JBOSS_HOME/server/*/deploy/hornetq/hornetq-configuration.xml:

   <security-enabled>false</security-enabled>

* build and deploy the app:

   mvn clean install jboss:hard-deploy -Pjbossas6

This will copy the resulting war along with a hornetq configuration file to a JBossAS
deploy directory.

The application simulates sending your status messages to a server via JMS Queue, the server
then stores the messages in a database and distributes them to a general JMS Topic where all people 
can see statuses of all the other people.

The webpage where you can find statuses of other people is available at:

   http://localhost:8080/jms-statuswatcher/watchstatus.jsf

If you tick "Follow all" the page will be periodically refreshed (the interval is 20 seconds) and
status messages will be received. Furthermore, you can receive all messages immediately by clicking 
on "Receive". If you are interested in all the messages that came to the server, click "History".

You can send your status to the server via (opening it in a different browser window):

   http://localhost:8080/jms-statuswatcher/sendstatus.jsf

Of course, you can open several browser windows, one for each user, and watch incomming statuses.



To run the example on JBossAS 7 you need to do the following:

* Start an AS7 instance with the EE6-full profile

    $JBOSS_HOME/bin/standalone.sh --server-config=$JBOSS_HOME/standalone/configuration/standalone-preview.xml

* Create queues and topics for testing

    $JBOSS_HOME/bin/jboss-admin.sh --file=jbossas7.cli

* Build the example

    mvn clean package -Pjbossas7

* Deploy the example

    $JBOSS_HOME/bin/jboss-admin.sh --connect
    deploy target/jms-statuswatcher.war

