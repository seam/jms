package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;

import java.util.Set;
import java.util.logging.Level;
import javax.enterprise.event.Event;
import javax.jms.*;

import org.jboss.solder.exception.control.ExceptionToCatch;
import org.jboss.solder.logging.Logger;

public class TopicBuilderImpl implements TopicBuilder {
    private Logger logger = Logger.getLogger(TopicBuilder.class);
    private Event<ExceptionToCatch> exceptionEvent;
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private javax.jms.MessageProducer messageProducer;
    private javax.jms.MessageConsumer messageConsumer;
    private Topic lastTopic;
    private boolean transacted = false;
    private int sessionMode = Session.AUTO_ACKNOWLEDGE;

    private String subtopic;

    TopicBuilderImpl(Event<ExceptionToCatch> event) {
        this.exceptionEvent = event;
    }

    @Override
    public TopicBuilder destination(Topic topic) {
        this.lastTopic = topic;
        this.messageProducer = null;
        this.messageConsumer = null;
        return this;
    }

    private void cleanupMessaging() {
        try {
            if (this.messageConsumer != null) {
                this.messageConsumer.close();
            }
            if (this.messageProducer != null) {
                this.messageProducer.close();
            }

            this.messageConsumer = null;
            this.messageProducer = null;
        } catch (JMSException ex) {
            logger.error("There was a problem cleaning up the JMS session",ex);
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
    }

    private void cleanConnection() {
        try {
            if (this.session != null) {
                this.session.close();
            }
            if (this.connection != null) {
                this.connection.close();
            }

            this.session = null;
            this.connection = null;

            cleanupMessaging();
        } catch (JMSException ex) {
            logger.error("There was a problem cleaning up the JMS connection",ex);
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
    }

    private void createMessageProducer() {
        logger.debug("Creating the MessageProducer.");
        if (messageProducer == null) {
            try {
                this.messageProducer = session.createProducer(lastTopic);
            } catch (JMSException ex) {
                logger.error("There was a problem creating the MessageProducer",ex);
                this.exceptionEvent.fire(new ExceptionToCatch(ex));
            }
        }
    }
    
    public void close() {
        cleanMessageProducer();
    }
    
    private void cleanMessageProducer() {
        if(this.messageProducer != null) {
            try {
                messageProducer.close();
            } catch (JMSException ex) {
                logger.error("Unable to close producer",ex);
                this.exceptionEvent.fire(new ExceptionToCatch(ex));
            }
        }
    }

    private void createMessageConsumer() {
        logger.debug("Creating the MessageConsumer.");
        if (messageConsumer == null) {
            try {
                logger.debug("ABout to create.");
                this.messageConsumer = session.createConsumer(lastTopic);
            } catch (JMSException ex) {
                logger.error("There was a problem creating the MessageConsumer",ex);
                this.exceptionEvent.fire(new ExceptionToCatch(ex));
            }
        } else {
            logger.debug("messageConsumer is not null, not creating a new one");
        }
    }

    @Override
    public TopicBuilder connectionFactory(ConnectionFactory cf) {
        try {
            cleanConnection();
            this.connectionFactory = cf;
            this.connection = cf.createConnection();
            this.session = connection.createSession(transacted, sessionMode);
            this.connection.start();
            return this;
        } catch (JMSException ex) {
            logger.error("Unable to set connection factory",ex);
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
            return null;
        }
    }

    @Override
    public TopicBuilder send(Message m) {
        this.createMessageProducer();
        try {
            this.messageProducer.send(m);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public TopicBuilder subtopic(String subtopic) {
        this.subtopic = subtopic;
        return this;
    }

    @Override
    public TopicBuilder sendMap(Map map) {
        try {
            Session s = this.session;
            MapMessage msg = s.createMapMessage();
            Set<Object> keys = map.keySet();
            for (Object key : keys) {
                Object value = map.get(key);
                msg.setObject(key.toString(), value);
            }
            send(msg);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public TopicBuilder sendString(String string) {
        logger.debug("Sending a string. "+string);
        try {
            Session s = this.session;
            TextMessage tm = s.createTextMessage();
            tm.setText(string);
            send(tm);
        } catch (JMSException ex) {
            logger.error("There was a problem sending the String",ex);
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public TopicBuilder sendObject(Serializable obj) {
        logger.debug("Sending an object. "+obj);
        try {
            Session s = this.session;
            ObjectMessage om = s.createObjectMessage();
            om.setObject(obj);
            send(om);
        } catch (JMSException ex) {
            logger.error("There was a problem sending the Object",ex);
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public TopicBuilder listen(MessageListener listener) {
        logger.debug("Setting up a message listener.");
        this.createMessageConsumer();
        try {
            this.messageConsumer.setMessageListener(listener);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public TopicBuilder newBuilder() {
        return new TopicBuilderImpl(this.exceptionEvent);
    }

    public String getSubtopic() {
        return this.subtopic;
    }

    @Override
    public TopicBuilder transacted() {
        this.transacted = !this.transacted;
        return this;
    }

    @Override
    public TopicBuilder sessionMode(int sessionMode) {
        this.sessionMode = sessionMode;
        return this;
    }
}
