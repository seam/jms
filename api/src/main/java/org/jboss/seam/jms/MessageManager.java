package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

/**
 * The MessageBuilder interface defines an abstraction layer over the JMS APIs
 * You can instances of this interface to create and send JMS Messages outbound.
 *
 * @author johnament
 */
public interface MessageManager extends Serializable {

    /* factory methods for creating messages */

    /**
     * Creates an object message based on the object payload provided.
     *
     * @param object Object to use as payload.
     * @return The ObjectMessage created.  To interact with it, use the session provided.
     */
    public ObjectMessage createObjectMessage(Object object);

    /**
     * Creates a text message based on the String payload provided.
     *
     * @param string the payload
     * @return A TextMessage based on the given string.
     */
    public TextMessage createTextMessage(String string);

    /**
     * Creates a MapMessage based on the Map payload provided.
     * Keys are converted to strings, values are the objects.
     *
     * @param map the payload
     * @return A MapMessage based on the given Map.
     */
    public MapMessage createMapMessage(Map<Object, Object> map);

    /**
     * Creates a bytes message based on the byte[] payload
     *
     * @param bytes the bytes to be written.
     * @return the resulting BytesMessage with the payload already written.
     */
    public BytesMessage createBytesMessage(byte[] bytes);

    /**
     * Utility methods for sending messages to a set of Strings that are the destinations
     *
     * @param message      The message to be sent.
     * @param destinations A sequence of JNDI names representing the destinations to distribute to.
     */
    public void sendMessage(Message message, String... destinations);

    /**
     * Sends an object (as an {@link javax.jms.ObjectMessage}) to listed destinations.
     *
     * @param object       Payload of the object message.
     * @param destinations A sequence of JNDI names representing the destinations to distribute to.
     */
    public void sendObjectToDestinations(Object object, String... destinations);

    /**
     * Sends a string (as an {@link javax.jms.TextMessage}) to listed destinations.
     *
     * @param string       Payload of the text message.
     * @param destinations A sequence of JNDI names representing the destinations to distribute to.
     */
    public void sendTextToDestinations(String string, String... destinations);

    /**
     * Sends a map (as an {@link javax.jms.MapMessage}) to listed destinations.
     *
     * @param map          Payload of the map message.
     * @param destinations A sequence of JNDI names representing the destinations to distribute to.
     */
    public void sendMapToDestinations(Map map, String... destinations);

    /**
     * Sends a bytep[ (as an {@link javax.jms.BytesMessage}) to listed destinations.
     *
     * @param bytes        Payload of the bytes message.
     * @param destinations A sequence of JNDI names representing the destinations to distribute to.
     */
    public void sendBytesToDestinations(byte[] bytes, String... destinations);


	/**
	 * Sends the given message to the list of destinations.
	 * 
	 * @param message The message to be sent.
	 * @param destinations an array of destinations to be sent to.
	 */
    public void sendMessage(Message message, Destination... destinations);


    /**
     * Wrapper method for creating object message, then sending it to listed destinations.
     *
     * @param object       the object to be sent as an object message.
     * @param destinations destinations to be sent to.
     */
    public void sendObjectToDestinations(Object object, Destination... destinations);

    /**
     * Wrapper method for creating text message, then sending it to listed destinations.
     *
     * @param string       the string data to be sent.
     * @param destinations destinations to be sent to.
     */
    public void sendTextToDestinations(String string, Destination... destinations);

    /**
     * Wrapper method for creating map message, then sending it to listed destinations.
     *
     * @param map          the map object to be sent.
     * @param destinations destinations to be sent to.
     */
    public void sendMapToDestinations(Map map, Destination... destinations);

    /**
     * Wrapper method for creating bytes message, then sending it to listed destinations.
     *
     * @param bytes        the stream data to be sent.
     * @param destinations destinations to be sent to.
     */
    public void sendBytesToDestinations(byte[] bytes, Destination... destinations);

    /**
     * Exposes the underlying {@link javax.jms.Session} for public usage.
     *
     * @returns the local Session.
     */
    public Session getSession();

    /**
     * Creates a new MessageProducer that will be managed by the used session
     *
     * @param destination JNDI Location of Destination in use
     * @return a new MessageProducer that is ready to work.
     */
    public MessageProducer createMessageProducer(String destination);

    /**
     * Creates a TopicPublisher for the given topic.
     *
     * @param destination JNDI Location of Destination in use
     * @return a new TopicPublisher that is ready to work.
     */
    public TopicPublisher createTopicPublisher(String destination);

    /**
     * Creates a TopicBuilder, simple interface for working with Topics.
     * @return a new TopicBuilder
     */
	public TopicBuilder createTopicBuilder();
	
	/**
	 * Creates a QueueBuilder, simple interface for working with Queues
	 * @return a new QueueBuilder
	 */
	public QueueBuilder createQueueBuilder();
	
    /**
     * Creates a QueueSender for the given queue.
     *
     * @param destination JNDI Location of Destination in use
     * @return a new QueueSender that is ready to work.
     */
    public QueueSender createQueueSender(String destination);

    /**
     * Creates a new MessageConsumer that will be managed by the used session
     *
     * @param destination JNDI Location of Destination in use
     * @param listeners   optional list of MessageListeners that will be bound to the consumer.
     * @return a new MessageConsumer that is ready to work.
     */
    public MessageConsumer createMessageConsumer(String destination, MessageListener... listeners);

    /**
     * Creates a new MessageConsumer that will be managed by the used session
     *
     * @param destination JNDI Location of Destination in use
     * @param listeners   optional list of MessageListeners that will be bound to the consumer.
     * @return a new MessageConsumer that is ready to work.
     */
    public MessageConsumer createMessageConsumer(Destination destination, MessageListener... listeners);

    /**
     * Creates a new TopicSubscriber that will be managed by the used session
     *
     * @param destination JNDI Location of Topic in use
     * @param listeners   optional list of MessageListeners that will be bound to the subscriber.
     * @return a new TopicSubscriber that is ready to work.
     */
    public TopicSubscriber createTopicSubscriber(String destination, MessageListener... listeners);

    /**
     * Creates a new QueueReceiver that will be managed by the used session
     *
     * @param destination JNDI Location of Queue in use
     * @param listeners   optional list of MessageListeners that will be bound to the receiver.
     * @return a new QueueReceiver that is ready to work.
     */
    public QueueReceiver createQueueReceiver(String destination, MessageListener... listeners);

	public MessageConsumer createMessageConsumer(String destination, String selector,
			MessageListener... listeners);

	public TopicSubscriber createTopicSubscriber(String destination, String selector,
			MessageListener... listeners);
}
