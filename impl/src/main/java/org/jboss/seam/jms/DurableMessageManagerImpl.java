package org.jboss.seam.jms;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.Durable;
import org.jboss.seam.jms.annotations.Module;

@Dependent
@Durable
public class DurableMessageManagerImpl
        extends MessageManagerImpl
        implements DurableMessageManager {

    private Logger logger = Logger.getLogger(DurableMessageManagerImpl.class);
    @Inject
    @Module
    ConnectionFactory connectionFactory;
    private Connection connection;

    @Override
    @PostConstruct
    public void init() {
        try {
            connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            logger.warn("Unable to create connection.");
        }
    }

    @PreDestroy
    public void shutdown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                logger.warn("Unable to create connection.");
            }
        }
    }

    public void login(String clientId) {
        try {
            connection.setClientID(clientId);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            logger.warn("Unable to create connection.");
        }
    }

    @Override
    public TopicSubscriber createDurableSubscriber(String topic, String id, MessageListener... listeners) {
        Topic t = (Topic) super.lookupDestination(topic);
        return createDurableSubscriber(t, id, listeners);
    }

    @Override
    public TopicSubscriber createDurableSubscriber(Topic topic, String id, MessageListener... listeners) {
        TopicSubscriber ts;
        try {
            ts = this.session.createDurableSubscriber(topic, id);
            if (ts != null && listeners != null && listeners.length > 0) {
                for (MessageListener listener : listeners) {
                    try {
                        ts.setMessageListener(listener);
                    } catch (JMSException e) {
                        logger.warn("Unable to map listener " + listener + " to subscriber " + ts, e);
                    }
                }
            }
        } catch (JMSException ex) {
            throw new IllegalArgumentException("Unable to create subscriber on topic " + topic + " for id " + id, ex);
        }
        return ts;
    }

    @Override
    public void unsubscribe(String id) {
        try {
            session.unsubscribe(id);
        } catch (JMSException e) {
            logger.warn("Unable to unsubscribe for id: " + id, e);
        }
    }
}
