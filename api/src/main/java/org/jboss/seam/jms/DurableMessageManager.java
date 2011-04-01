package org.jboss.seam.jms;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.TopicSubscriber;

import org.jboss.seam.jms.annotations.Durable;

@Durable
public interface DurableMessageManager extends MessageManager {
	
	/**
	 * Initializes the connection for this DurableMessageManager.
	 * Sets the ClientID for the underlying {@link javax.jms.Connection}
	 * 
	 * @param clientId
	 */
	public void login(String clientId);
	/**
	 * Creates a topic subscriber with the given ID and binds a message listener to it, if valid.
	 * 
	 * {@see MessageBuilder.createDurableSubscriber}
	 * 
	 * @param destination JNDI Location of the topic to subscribe to.
	 * @param id the client id for the subscriber.  This ID should be unique, and should be used to shutdown the listener.
	 * @param listener The Message Listeners to be bound, if any.
	 * @return the resulting TopicSubscriber or null if an error occurred.
	 */
	public TopicSubscriber createDurableSubscriber(String destination, String id, MessageListener... listeners);
	
        /**
	 * Creates a topic subscriber with the given ID and binds a message listener to it, if valid.
	 * 
	 * {@see MessageBuilder.createDurableSubscriber}
	 * 
	 * @param destination the existing destination to reference.
	 * @param id the client id for the subscriber.  This ID should be unique, and should be used to shutdown the listener.
	 * @param listener The Message Listeners to be bound, if any.
	 * @return the resulting TopicSubscriber or null if an error occurred.
	 */
        public TopicSubscriber createDurableSubscriber(Destination destination, String id, MessageListener... listeners);
	/**
	 * Unsubscribes a durable subscriber from the topic, with the given id.
	 * 
	 * @param id the id of the subscriber.
	 */
	public void unsubscribe(String id);

}
