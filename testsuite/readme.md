#Seam Solder Test Suite

##Running the testsuite on the default container (Embedded Weld)

    mvn clean verify 

##Running the testsuite on JBoss AS 7

    export JBOSS_HOME=/path/to/jboss-as-7.x
    $JBOSS_HOME/bin/standalone.sh --server-config=$JBOSS_HOME/standalone/configuration/standalone-preview.xml
    
    Create queues and topics for testing
    $JBOSS_HOME/bin/jboss-admin.sh --file=jbossas7.cli
    
    mvn clean verify -Darquillian=jbossas-remote-7
    
##Running the testsuite on JBoss AS 6
    export JBOSS_HOME=/path/to/jboss-as-6.x
    
    Disable security.  See [SEAMJMS-13](https://issues.jboss.org/browse/SEAMJMS-13) for more information.
    sed '/<\/address-settings>/a<security-enabled>false</security-enabled>' -i ${JBOSS_HOME}/server/default/deploy/hornetq/hornetq-configuration.xml
    
    $JBOSS_HOME/bin/run.sh
	mvn clean verify -Darquillian=jbossas-remote-6

