package org.jboss.seam.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.jboss.seam.solder.logging.Logger;

public class TopicBuilderImpl implements TopicBuilder {

	private MessageManager messageManager;
	private List<Topic> destinations;
	private String subtopic = null;
	private Logger logger;
	
	TopicBuilderImpl(MessageManager messageManager) {
		this.logger = Logger.getLogger(TopicBuilder.class);
		this.messageManager = messageManager;
		this.destinations = new ArrayList<Topic>();
	}
	
	@Override
	public TopicBuilder destination(String destination) {
		Topic topic = (Topic)this.messageManager.lookupDestination(destination);
		return destination(topic);
	}
	
	@Override
	public TopicBuilder destination(Topic topic) {
		this.destinations.add(topic);
		return this;
	}

	@Override
	public TopicBuilder subtopic(String subtopic) {
		this.subtopic = subtopic;
		return this;
	}

	@Override
	public TopicBuilder sendObject(Object obj) {
		send(this.messageManager.createObjectMessage(obj));
		return this;
	}

	@Override
	public TopicBuilder send(Message m) {
		for(Topic topic : this.destinations) {
			TopicPublisher tp = messageManager.createTopicPublisher(topic);
			if(this.subtopic != null) {
				try{
					m.setStringProperty("sm_jms_subtopic", subtopic);
				} catch (JMSException e) {
					logger.warn("Unable to set JMS Sub Topic "+subtopic,e);
				}
			}
			try {
				tp.send(m);
			} catch (JMSException e) {
				logger.warn("Error when sending JMS Message",e);
			}
		}
		return this;
	}

	@Override
	public TopicBuilder sendMap(Map m) {
		send(this.messageManager.createMapMessage(m));
		return this;
	}

	@Override
	public TopicBuilder sendString(String s) {
		send(this.messageManager.createTextMessage(s));
		return this;
	}

	@Override
	public TopicBuilder listen(MessageListener... ml) {
		for(Topic topic : this.destinations) {
			if(this.subtopic != null) {
				String topicSelector = String.format("sm_jms_subtopic = '%s'",subtopic);
				this.messageManager.createTopicSubscriber(topic, topicSelector, ml);
			} else {
				this.messageManager.createMessageConsumer(topic, ml);
			}
		}
		return this;
	}
	
	@Override
	public TopicBuilder newBuilder() {
		return new TopicBuilderImpl(this.messageManager);
	}
	public List<Topic> getDestinations() {
		return this.destinations;
	}
	public String getSubtopic() {
		return this.subtopic;
	}

	
}
