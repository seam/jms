package org.jboss.seam.jms.test.inject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MessagePubSubProducerTest {
	@Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(MessagePubSubProducerTest.class,MessagePubSubProducer.class);
    }
	
	@Inject @JmsDestination(jndiName="jms/T3") Instance<TopicPublisher> topicPublisherInstance;
	@Inject @JmsDestination(jndiName="jms/T3") Instance<TopicSubscriber> topicSubscriberInstance;
	@Inject @JmsDestination(jndiName="jms/QA")  Instance<QueueSender> queueSenderInstance;
	@Inject @JmsDestination(jndiName="jms/QA") Instance<QueueReceiver> queueReceiverInstance;
	
	@Test
	public void testCreateTopicProducer() {
		Assert.assertFalse(topicPublisherInstance.isUnsatisfied());
	}
	@Test
	public void testCreateTopicSubscriber() {
		Assert.assertFalse(topicSubscriberInstance.isUnsatisfied());
	}
	@Test
	public void testCreateQueueSender() {
		Assert.assertFalse(queueSenderInstance.isUnsatisfied());
	}
	@Test
	public void testCreateQueueReceiver() {
		Assert.assertFalse(queueReceiverInstance.isUnsatisfied());
	}
	
}
