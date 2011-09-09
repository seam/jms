#Seam JMS Test Suite

##Running the tests on JBoss AS 6

    mvn clean verify -Djbossas-managed-6

or

    mvn clean verify -Djbossas-remote-6

In order for tests dealing with consumers to pass HornetQ security must be disabled.  See [SEAMJMS-13](https://issues.jboss.org/browse/SEAMJMS-13) for more information.

In $JBOSS_HOME/server/$PROFILE_NAME/deploy/hornetq/hornetq-configuration.xml you must add <security-enabled>false</security-enabled> below <configuration> root node.

##Running the tests on JBoss AS 7

    mvn clean verify -Djbossas-managed-7

The profile is configured to download the JBoss AS distribution from a Maven repository and uses a custom configuration from
    
    jbossas-managed-7/src/test/resources/standalone-jms.xml

which defines JMS queues and topics used in the tests.

##Contents

common/ directory contains the source of tests common to all the containers. Sources of container-specific tests are located in the respective container modules.


