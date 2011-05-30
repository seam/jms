package org.jboss.seam.jms;

import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * QueueBuilder is a builder pattern implementation for working with JMS Queues.
 * 
 * @author johnament
 *
 */
public interface QueueBuilder {
	public QueueBuilder destination(String destination);
	public QueueBuilder send(Object obj);
	public QueueBuilder send(Message m);
	public QueueBuilder send(Map m);
	public QueueBuilder send(String s);
	public QueueBuilder listen(MessageListener... ml);
	public QueueBuilder newBuilder();
}
