package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

/**
 * TopicBuilder is a builder pattern implementation for working with JMS Topics.
 * 
 * @author johnament
 *
 */
public interface TopicBuilder extends Serializable {
	/**
	 * Adds a destination based on JNDI location.
	 * 
	 * @param destination jndi location.
	 * @return this TopicBuilder
	 */
	public TopicBuilder destination(String destination);
	/**
	 * Adds a topic to the associated destinations.
	 * 
	 * @param topic the topic to add.
	 * @return this TopicBuilder.
	 */
	public TopicBuilder destination(Topic topic);
	/**
	 * Denotes messages and listeners on this Topic as belonging to a subtopic.  subtopics are implemented as selectors on messages with String property sm_jms_subtopic
	 * 
	 * @param subtopic The name of the subtopic.
	 * @return this TopicBuilder
	 */
	public TopicBuilder subtopic(String subtopic);
	/**
	 * Sends an Object as a JMS Object Message to the destinations associated.
	 * 
	 * @param obj the serializable Object to send.
	 * @return this TopicBuilder
	 */
	public TopicBuilder sendObject(Object obj);
	/**
	 * Sends a JMS Message to the destinations associated.
	 * 
	 * @param m The message to send.
	 * @return this TopicBuilder.
	 */
	public TopicBuilder send(Message m);
	/**
	 * Sends a Map as a JMS Map Message to the destinations associated.
	 * 
	 * @param m the Map to send.
	 * @return this TopicBuilder
	 */
	public TopicBuilder sendMap(Map m);
	/**
	 * Sends a String as a JMS TextMessage to the destinations associated.
	 * 
	 * @param s The String to send.
	 * @return this TopicBuilder.
	 */
	public TopicBuilder sendString(String s);
	/**
	 * Adds the given MessageListeners as listeners on the associated destinations.
	 * 
	 * @param ml MessageListener instances to connect to these destinations.
	 * @return this TopicBuilder
	 */
	public TopicBuilder listen(MessageListener... ml);
	/**
	 * Creates a TopicBuilder.  It will be associated with any active Session.
	 * 
	 * @return a new TopicBuilder instance.
	 */
	public TopicBuilder newBuilder();
}
