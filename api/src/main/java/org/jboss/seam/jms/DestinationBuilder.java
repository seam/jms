package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;

/**
 * QueueBuilder is a builder pattern implementation for working with JMS Queues.
 * 
 * @author johnament
 *
 */
public interface DestinationBuilder extends Serializable {

    /**
     * Toggles the transacted state (default is false) for this builder.
     * 
     * @return this Builder.
     */
    public DestinationBuilder transacted();

    /**
     * Sets the session mode for this Builder.  Default is Session.AUTO_ACKNOWLEDGE
     * 
     * @param sessionMode  SessionMode flag, see javax.jms.Session's list of valid values.
     * @return this Builder.
     */
    public DestinationBuilder sessionMode(int sessionMode);

    /**
     * Specifies the ConnectionFactory to use.
     * 
     * @param ConnectionFactory to use.
     * @return this QueueBuilder.
     */
    public DestinationBuilder connectionFactory(ConnectionFactory connectionFactory);

    /**
     * Adds a Queue to the destinations of this QueueBuilder.
     * 
     * @param queue The queue to add.
     * @return this QueueBuilder
     */
    public DestinationBuilder destination(Destination destination);

    /**
     * Sends a JMS Message to the destinations associated.
     * 
     * @param m The message to send.
     * @return this QueueBuilder.
     */
    public DestinationBuilder send(Message m);

    /**
     * Sends a Map as a JMS Map Message to the destinations associated.
     * 
     * @param m the Map to send.
     * @return this QueueBuilder
     */
    public DestinationBuilder sendMap(Map m);

    /**
     * Sends a String as a JMS TextMessage to the destinations associated.
     * 
     * @param s The String to send.
     * @return this QueueBuilder.
     */
    public DestinationBuilder sendString(String s);

    /**
     * Sends a Serializable Object as an ObjectMessage.
     * 
     * @param obj The Serializable object to send.
     * @return this QueueBuilder
     */
    public DestinationBuilder sendObject(Serializable obj);

    /**
     * Adds the given MessageListeners as listeners on the associated destinations.
     * 
     * @param ml MessageListener instances to connect to these destinations.
     * @return this QueueBuilder
     */
    public DestinationBuilder listen(MessageListener ml);

    /**
     * Creates a QueueBuilder.  It will be associated with any active Session.
     * 
     * @return a new QueueBuilder instance.
     */
    public DestinationBuilder newBuilder();
}
