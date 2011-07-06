package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
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
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDefault;

@Dependent
public class MessageManagerImpl implements MessageManager {

    @Inject @JmsDefault("session")
    Session session;

    private Logger logger = Logger.getLogger(MessageManagerImpl.class);
/*
    @PostConstruct
    public void init() {
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
        }
    }

    @PreDestroy
    public void close() {
        try {
            session.commit();
            connection.close();
        } catch (JMSException e) {
        }
    }
*/
    @Override
    public ObjectMessage createObjectMessage(Object object) {
        if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException("Objects must be serializable to be sent over JMS.");
        }
        try {
            return session.createObjectMessage((Serializable) object);
        } catch (JMSException e) {
            logger.error("Unable to create object message.", e);
            throw new RuntimeException("Error when creating object message", e);
        }
    }

    @Override
    public TextMessage createTextMessage(String string) {
        try {
            TextMessage tm = session.createTextMessage();
            tm.setText(string);
            return tm;
        } catch (JMSException e) {
            logger.error("Unable to create text message.", e);
            throw new RuntimeException("Error when creating text message", e);
        }
    }

    @Override
    public MapMessage createMapMessage(Map<Object, Object> map) {
        try {
            MapMessage msg = session.createMapMessage();
            Set<Object> keys = map.keySet();
            for (Object key : keys) {
                Object value = map.get(key);
                msg.setObject(key.toString(), value);
            }
            return msg;
        } catch (JMSException e) {
            logger.error("Unable to create map message.", e);
            throw new RuntimeException("Error when creating map message", e);
        }
    }

    @Override
    public BytesMessage createBytesMessage(byte[] bytes) {
        try {
            BytesMessage bm = session.createBytesMessage();
            bm.writeBytes(bytes);
            return bm;
        } catch (JMSException e) {
            logger.error("Unable to create bytes message", e);
            throw new RuntimeException("Error creating bytes message", e);
        }
    }

    @Override
    public void sendObjectToDestinations(Object object, String... destinations) {
        this.sendMessage(this.createObjectMessage(object), destinations);
    }

    @Override
    public void sendTextToDestinations(String string, String... destinations) {
        this.sendMessage(this.createTextMessage(string), destinations);
    }

    @Override
    public void sendMapToDestinations(Map map, String... destinations) {
        this.sendMessage(this.createMapMessage(map), destinations);
    }

    @Override
    public void sendBytesToDestinations(byte[] bytes, String... destinations) {
        this.sendMessage(this.createBytesMessage(bytes), destinations);
    }

    public Destination lookupDestination(String jndiName) {
        try {
            Context c = new InitialContext();
            return (Destination) c.lookup(jndiName);
        } catch (NamingException e) {
            logger.warn("Unable to lookup " + jndiName, e);
        }
        return null;
    }

    protected void sendMessage(String jndiName, Message message) {
        Destination d = this.lookupDestination(jndiName);
        this.sendMessage(d, message);
    }

    private void sendMessage(Destination destination, Message message) {
        try {
            logger.info("Routing destionation " + destination + " with message " + message);
            this.createMessageProducer(destination).send(message);
        } catch (JMSException e) {
            logger.warn("Problem attempting to send message " + message + " to destination " + destination, e);
        }
    }

    @Override
    public void sendMessage(Message message, Destination... destinations) {
        for (Destination destination : destinations)
            sendMessage(destination, message);
    }

    @Override
    public void sendMessage(Message message, String... destinations) {
        for (String destination : destinations)
            sendMessage(destination, message);
    }

    @Override
    public void sendObjectToDestinations(Object object,
                                         Destination... destinations) {
        sendMessage(this.createObjectMessage(object), destinations);
    }

    @Override
    public void sendTextToDestinations(String string,
                                       Destination... destinations) {
        sendMessage(this.createTextMessage(string), destinations);
    }

    @Override
    public void sendMapToDestinations(Map map, Destination... destinations) {
        sendMessage(this.createMapMessage(map), destinations);
    }

    @Override
    public void sendBytesToDestinations(byte[] bytes,
                                        Destination... destinations) {
        sendMessage(this.createBytesMessage(bytes), destinations);
    }

    @Override
    public Session getSession() {
        return this.session;
    }

    @Override
    public MessageConsumer createMessageConsumer(String destination, MessageListener... listeners) {
        try {
            MessageConsumer mc = this.session.createConsumer(lookupDestination(destination));
            if (mc != null && listeners != null) {
                for (MessageListener listener : listeners)
                    try {
                        mc.setMessageListener(listener);
                    } catch (JMSException e) {
                        logger.warn("Unable to map listener " + listener + " to consumer " + mc, e);
                    }
            }
            return mc;
        } catch (JMSException e) {
            logger.warn("Unable to create message consumer", e);
            return null;
        }
    }

	@Override
	public MessageConsumer createMessageConsumer(String destination, String selector, MessageListener... listeners) {
		try {
			MessageConsumer mc = this.session.createConsumer(lookupDestination(destination),selector);
			if(mc != null && listeners != null) {
				for(MessageListener listener : listeners)
					try {
						mc.setMessageListener(listener);
					} catch (JMSException e) {
						logger.warn("Unable to map listener "+listener+" to consumer "+mc,e);
					}
			}
			return mc;
		} catch (JMSException e) {
			logger.warn("Unable to create message consumer",e);
			return null;
		}
	}
	
    protected MessageConsumer createMessageConsumer(Destination destination) {
        try {
            return this.session.createConsumer(destination);
        } catch (JMSException e) {
            logger.warn("Problem creating message consumer for " + destination, e);
            return null;
        }
    }

    protected MessageProducer createMessageProducer(Destination destination) {
        try {
            return this.session.createProducer(destination);
        } catch (JMSException e) {
            logger.warn("Problem creating message producer for " + destination, e);
            return null;
        }
    }

    private TopicSubscriber createDurableSubscriber(String destination, String id) {
        try {
            return
                    this.session.createDurableSubscriber((Topic) this.lookupDestination(destination), id);
        } catch (JMSException e) {
            logger.warn("Unable to create durable subscriber", e);
            return null;
        }
    }

    @Override
    public MessageProducer createMessageProducer(String destination) {
        try {
            return this.session.createProducer(lookupDestination(destination));
        } catch (JMSException e) {
            logger.warn("Unable to create message producer", e);
            return null;
        }
    }

    @Override
    public TopicPublisher createTopicPublisher(String destination) {
        return (TopicPublisher) this.createMessageProducer(destination);
    }

    @Override
    public QueueSender createQueueSender(String destination) {
        return (QueueSender) this.createMessageProducer(destination);
    }

    @Override
    public TopicSubscriber createTopicSubscriber(String destination,
                                                 MessageListener... listeners) {
        MessageConsumer mc = this.createMessageConsumer(destination, listeners);
        return (TopicSubscriber) mc;
    }
	
    @Override
	public TopicSubscriber createTopicSubscriber(String destination,
			String selector,
			MessageListener... listeners) {
		MessageConsumer mc = this.createMessageConsumer(destination, listeners);
		return (TopicSubscriber)mc;
	}

	@Override
    public QueueReceiver createQueueReceiver(String destination,
                                             MessageListener... listeners) {
        MessageConsumer mc = this.createMessageConsumer(destination, listeners);
        return (QueueReceiver) mc;
    }

    @Override
    public MessageConsumer createMessageConsumer(Destination destination,
                                                 MessageListener... listeners) {
        MessageConsumer mc = this.createMessageConsumer(destination);
        if (mc != null && listeners != null) {
            for (MessageListener listener : listeners)
                try {
                    mc.setMessageListener(listener);
                } catch (JMSException e) {
                    logger.warn("Unable to set listener " + listener + " on to destination " + destination);
                }
        }
        return mc;
	}

    @Produces
	@Override
	public TopicBuilder createTopicBuilder() {
		return new TopicBuilderImpl(this);
    }
	
	@Produces
	@Override
	public QueueBuilder createQueueBuilder() {
		return new QueueBuilderImpl(this);
	}

	@Override
	public JmsMessage createJmsMessage(Class<?> payloadType, Object payload) {
		return new JmsMessageImpl(payloadType,payload,this);
	}

}
