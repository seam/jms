package org.jboss.seam.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.jboss.logging.Logger;

public class QueueBuilderImpl implements QueueBuilder {

	private MessageManager messageManager;
	private List<Queue> destinations;
	private Logger logger;
	
	QueueBuilderImpl(MessageManager messageManager) {
		this.logger = Logger.getLogger(QueueBuilder.class);
		this.messageManager = messageManager;
		this.destinations = new ArrayList<Queue>();
	}
	
	@Override
	public QueueBuilder destination(String destination) {
		Queue queue = (Queue)this.messageManager.lookupDestination(destination);
		return destination(queue);
	}
	
	@Override
	public QueueBuilder destination(Queue queue) {
		destinations.add(queue);
		return this;
	}

	@Override
	public QueueBuilder sendObject(Object obj) {
		send(this.messageManager.createObjectMessage(obj));
		return this;
	}

	@Override
	public QueueBuilder send(Message m) {
		for(Queue queue : this.destinations) {
			QueueSender qs = messageManager.createQueueSender(queue);
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
		for(Queue queue : this.destinations) {
			this.messageManager.createMessageConsumer(queue, listeners);
		}
		return this;
	}
	
	@Override
	public QueueBuilder newBuilder() {
		return new QueueBuilderImpl(this.messageManager);
	}
	public List<Queue> getDestinations() {
		return this.destinations;
	}
}
