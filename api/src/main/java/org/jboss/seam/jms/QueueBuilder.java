package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * QueueBuilder is a builder pattern implementation for working with JMS Queues.
 * 
 * @author johnament
 *
 */
public interface QueueBuilder extends Serializable {
	public QueueBuilder destination(String destination);
	public QueueBuilder sendObject(Object obj);
	public QueueBuilder send(Message m);
	public QueueBuilder sendMap(Map m);
	public QueueBuilder sendString(String s);
	public QueueBuilder listen(MessageListener... ml);
	public QueueBuilder newBuilder();
}
