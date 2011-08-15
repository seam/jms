package org.jboss.seam.jms.test.inject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.impl.inject.ContextProducer;
import org.jboss.seam.jms.impl.inject.DestinationProducer;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MessagePubSubProducerTest {
	@Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(MessagePubSubProducerTest.class,MessagePubSubProducer.class,DestinationProducer.class,ContextProducer.class);
    }
	
	@Inject @JmsDestination(jndiName="jms/T3") Instance<MessageProducer> messageProducerInstance;
	@Inject @JmsDestination(jndiName="jms/QA") Instance<MessageConsumer> messageConsumerInstance;
	
	@Test
	public void testCreateMessageProducer() {
		Assert.assertFalse(messageProducerInstance.isUnsatisfied());
		Assert.assertNotNull(messageProducerInstance.get());
	}
	@Test
	public void testCreateMessageConsumer() {
		Assert.assertFalse(messageConsumerInstance.isUnsatisfied());
		Assert.assertNotNull(messageConsumerInstance.get());
	}	
}
