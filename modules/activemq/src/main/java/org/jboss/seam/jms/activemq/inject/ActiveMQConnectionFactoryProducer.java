package org.jboss.seam.jms.activemq.inject;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jboss.seam.jms.annotations.JmsDefault;
import org.jboss.seam.jms.inject.JmsConnectionFactoryProducer;

@Specializes
public class ActiveMQConnectionFactoryProducer extends
		JmsConnectionFactoryProducer {
	private ConnectionFactory connectionFactory;
	
	@PostConstruct
	public void initActiveMQ() {
		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		this.connectionFactory = cf;
	}

	@Override @Specializes
	@Produces @ApplicationScoped
	@JmsDefault("connectionFactory")
	public ConnectionFactory produceConnectionFactory() {
		return connectionFactory;
	}
	
	
}
