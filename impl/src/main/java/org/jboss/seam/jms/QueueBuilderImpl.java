package org.jboss.seam.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.jboss.logging.Logger;

public class QueueBuilderImpl implements QueueBuilder {

	private MessageManager messageManager;
	private List<String> destinations;
	private Logger logger;
	
	QueueBuilderImpl(MessageManager messageManager) {
		this.logger = Logger.getLogger(QueueBuilder.class);
		this.messageManager = messageManager;
		this.destinations = new ArrayList<String>();
	}
	
	@Override
	public QueueBuilder destination(String destination) {
		destinations.add(destination);
		return this;
	}

	@Override
	public QueueBuilder sendObject(Object obj) {
		send(this.messageManager.createObjectMessage(obj));
		return this;
	}

	@Override
	public QueueBuilder send(Message m) {
		for(String destination : this.destinations) {
			QueueSender qs = messageManager.createQueueSender(destination);
			try {
				qs.send(m);
			} catch (JMSException e) {
				logger.warn("Error when sending JMS Message",e);
			}
		}
		return this;
	}

	@Override
	public QueueBuilder sendMap(Map m) {
		send(this.messageManager.createMapMessage(m));
		return this;
	}

	@Override
	public QueueBuilder sendString(String s) {
		send(this.messageManager.createTextMessage(s));
		return this;
	}

	@Override
	public QueueBuilder listen(MessageListener... listeners) {
		for(String destination : this.destinations) {
			this.messageManager.createQueueReceiver(destination, listeners);
		}
		return this;
	}
	
	@Override
	public QueueBuilder newBuilder() {
		return new QueueBuilderImpl(this.messageManager);
	}
	public List<String> getDestinations() {
		return this.destinations;
	}
}
