package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;

import java.util.Set;
import javax.jms.*;
import javax.enterprise.event.Event;
import org.jboss.solder.exception.control.ExceptionToCatch;

import org.jboss.solder.logging.Logger;

public class DestinationBuilderImpl implements DestinationBuilder {
    private Logger logger = Logger.getLogger(QueueBuilderImpl.class);
    private Event<ExceptionToCatch> exceptionEvent;
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private javax.jms.MessageProducer messageProducer;
    private javax.jms.MessageConsumer messageConsumer;
    private Destination lastDestination;
    private boolean transacted = false;
    private int sessionMode = Session.AUTO_ACKNOWLEDGE;

    DestinationBuilderImpl(Event<ExceptionToCatch> exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    @Override
    public DestinationBuilder destination(Destination destination) {
        this.lastDestination = destination;
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
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
    }

    private void createMessageProducer() {
        if (messageProducer == null) {
            try {
                this.messageProducer = session.createProducer(lastDestination);
            } catch (JMSException ex) {
                this.exceptionEvent.fire(new ExceptionToCatch(ex));
            }
        }
    }

    private void createMessageConsumer() {
        if (messageConsumer == null) {
            try {
                this.messageConsumer = session.createConsumer(lastDestination);
            } catch (JMSException ex) {
                this.exceptionEvent.fire(new ExceptionToCatch(ex));
            }
        }
    }

    @Override
    public DestinationBuilder connectionFactory(ConnectionFactory cf) {
        try {
            cleanConnection();
            this.connectionFactory = cf;
            this.connection = cf.createConnection();
            this.session = connection.createSession(transacted, sessionMode);
            logger.debug("Created session  "+session);
            this.connection.start();
            //getSession();
            return this;
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
            return null;
        }
    }

    @Override
    public DestinationBuilder send(Message m) {
        this.createMessageProducer();
        try{
            this.messageProducer.send(m);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public DestinationBuilder sendMap(Map map) {
        try {
            MapMessage msg = this.session.createMapMessage();
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
    public DestinationBuilder sendString(String string) {
        try{
            TextMessage tm = this.session.createTextMessage();
            tm.setText(string);
            send(tm);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        } 
        return this;
    }
    
    @Override
    public DestinationBuilder sendObject(Serializable obj) {
        try{
            ObjectMessage om = this.session.createObjectMessage();
            om.setObject(obj);
            send(om);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        } 
        return this;
    }

    @Override
    public DestinationBuilder listen(MessageListener listener) {
        this.createMessageConsumer();
        try{
            this.messageConsumer.setMessageListener(listener);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public DestinationBuilder newBuilder() {
        return new DestinationBuilderImpl(this.exceptionEvent);
    }

    @Override
    public DestinationBuilder transacted() {
        this.transacted = !this.transacted;
        return this;
    }

    @Override
    public DestinationBuilder sessionMode(int sessionMode) {
        this.sessionMode = sessionMode;
        return this;
    }
}
