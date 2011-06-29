package org.jboss.seam.jms;

import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * TopicBuilder is a builder pattern implementation for working with JMS Topics.
 * 
 * @author johnament
 *
 */
public interface TopicBuilder {
	public TopicBuilder destination(String destination);
	public TopicBuilder subtopic(String subtopic);
	public TopicBuilder sendObject(Object obj);
	public TopicBuilder send(Message m);
	public TopicBuilder sendMap(Map m);
	public TopicBuilder sendString(String s);
	public TopicBuilder listen(MessageListener... ml);
	public TopicBuilder newBuilder();
}
