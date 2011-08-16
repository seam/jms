package org.jboss.seam.jms.openmq;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.jms.ConnectionFactory;

import org.jboss.seam.jms.annotations.JmsDefault;
import org.jboss.seam.jms.inject.JmsConnectionFactoryProducer;

import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance;
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime;

@ApplicationScoped
@Specializes
public class OpenMQConnectionFactoryProducer extends JmsConnectionFactoryProducer {
	private ConnectionFactory connectionFactory;
	@PostConstruct
	public void initOpenMQ() {
		// Obtain the ClientRuntime singleton object
		ClientRuntime clientRuntime = ClientRuntime.getRuntime();

		// Create a broker instance
		BrokerInstance brokerInstance = clientRuntime.createBrokerInstance();

		// Create a broker event listener
		//BrokerEventListener listener = new EmbeddedBrokerEventListener();

		// Convert the broker arguments into Properties. Note that parseArgs is
		// a utility method that does not change the broker instance.
		Properties props = brokerInstance.parseArgs(new String[]{});

		// Initialize the broker instance using the specified properties and
		// broker event listener
		brokerInstance.init(props, null);

		// now start the embedded broker
		brokerInstance.start();
		this.connectionFactory = new com.sun.messaging.ConnectionFactory();
		connectionFactory.setProperty(ConnectionConfiguration.imqAddressList, "mq://localhost/direct");
	}
	
	@Override
	@Produces @JmsDefault("connectionFactory")
	public ConnectionFactory produceConnectionFactory() {
		return this.connectionFactory;
	}

}
