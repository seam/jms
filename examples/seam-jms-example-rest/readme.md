#sample-jms-app

This application is deployed as a web application and shows off the 

##Building
*  Execute mvn clean install

##Deployment
Currently, the deployment has only been tested on JBoss AS 6

*  You need to the following JMS Topics (in server/*/deploy/hornetq/hornetq-jms.xml)
	<topic name="LongT4"><entry name="jms/LongT4"/></topic>
	<topic name="LongT2"><entry name="jms/LongT2"/></topic>

*  Copy the war file to server/*/deploy/

*  Generate a rest request, e.g. rest-resources/pub/12345
This publishes a Long type to the JMS Topic to jms/LongT4.
It is observed in an event, published using JMS APIs

*  Generate a rest request, e.g. rest-resources/msg/hello_world!
This publishes via an event, observed in an MDB.
