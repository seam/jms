package org.jboss.seam.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.jboss.logging.Logger;

public class TopicBuilderImpl implements TopicBuilder {

	private MessageManager messageManager;
	private List<String> destinations;
	private String subtopic = null;
	private Logger logger;
	
	TopicBuilderImpl(MessageManager messageManager) {
		this.logger = Logger.getLogger(TopicBuilder.class);
		this.messageManager = messageManager;
		this.destinations = new ArrayList<String>();
	}
	
	@Override
	public TopicBuilder destination(String destination) {
		destinations.add(destination);
		return this;
	}

	@Override
	public TopicBuilder subtopic(String subtopic) {
		this.subtopic = subtopic;
		return this;
	}

	@Override
	public TopicBuilder send(Object obj) {
		send(this.messageManager.createObjectMessage(obj));
		return this;
	}

	@Override
	public TopicBuilder send(Message m) {
		for(String destination : this.destinations) {
			TopicPublisher tp = messageManager.createTopicPublisher(destination);
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
	public TopicBuilder send(Map m) {
		send(this.messageManager.createMapMessage(m));
		return this;
	}

	@Override
	public TopicBuilder send(String s) {
		send(this.messageManager.createTextMessage(s));
		return this;
	}

	@Override
	public TopicBuilder listen(MessageListener... ml) {
		for(String destination : this.destinations) {
			if(this.subtopic != null) {
				String topicSelector = String.format("sm_jms_subtopic = '%s'",subtopic);
				this.messageManager.createTopicSubscriber(destination, topicSelector, ml);
			} else {
				this.messageManager.createTopicSubscriber(destination, ml);
			}
		}
		return this;
	}
	
	@Override
	public TopicBuilder newBuilder() {
		return new TopicBuilderImpl(this.messageManager);
	}

}
