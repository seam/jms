package org.jboss.seam.jms;

import java.io.Serializable;
import java.util.Map;

import java.util.Set;
import javax.jms.*;
import javax.enterprise.event.Event;
import org.jboss.solder.exception.control.ExceptionToCatch;

import org.jboss.solder.logging.Logger;

public class QueueBuilderImpl implements QueueBuilder {

    private Event<ExceptionToCatch> exceptionEvent;
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private javax.jms.MessageProducer messageProducer;
    private javax.jms.MessageConsumer messageConsumer;
    private Queue lastQueue;
    private boolean transacted = false;
    private int sessionMode = Session.AUTO_ACKNOWLEDGE;

    QueueBuilderImpl(Event<ExceptionToCatch> exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    @Override
    public QueueBuilder destination(Queue queue) {
        this.lastQueue = queue;
        cleanupMessaging();
        this.messageProducer = null;
        this.messageConsumer = null;
        return this;
    }

    private Session getSession() {
        if (this.connectionFactory != null) {
            if (this.connection == null) {
                this.session = null;
                try {
                    this.connection = this.connectionFactory.createConnection();
                } catch (JMSException ex) {
                    this.exceptionEvent.fire(new ExceptionToCatch(ex));
                    throw new RuntimeException(ex);
                }
            }
            if (this.session == null) {
                try {
                    this.session = connection.createSession(transacted, sessionMode);
                } catch (JMSException ex) {
                    this.exceptionEvent.fire(new ExceptionToCatch(ex));
                    throw new RuntimeException(ex);
                }
            }
        } else {
            throw new RuntimeException("Attempting to pull the session before setting the connectionFactory");
        }
        return this.session;
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
                this.messageProducer = session.createProducer(lastQueue);
            } catch (JMSException ex) {
                this.exceptionEvent.fire(new ExceptionToCatch(ex));
            }
        }
    }

    private void createMessageConsumer() {
        if (messageConsumer == null) {
            try {
                this.messageConsumer = session.createConsumer(lastQueue);
            } catch (JMSException ex) {
                this.exceptionEvent.fire(new ExceptionToCatch(ex));
            }
        }
    }

    @Override
    public QueueBuilder connectionFactory(ConnectionFactory cf) {
        cleanConnection();
        this.connectionFactory = cf;
        getSession();
        return this;
    }

    @Override
    public QueueBuilder send(Message m) {
        this.createMessageProducer();
        try{
            this.messageProducer.send(m);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public QueueBuilder sendMap(Map map) {
        try {
            Session s = getSession();
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
    public QueueBuilder sendString(String string) {
        try{
            Session s = getSession();
            TextMessage tm = s.createTextMessage();
            tm.setText(string);
            send(tm);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        } 
        return this;
    }
    
    @Override
    public QueueBuilder sendObject(Serializable obj) {
        try{
            Session s = getSession();
            ObjectMessage om = s.createObjectMessage();
            om.setObject(obj);
            send(om);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        } 
        return this;
    }

    @Override
    public QueueBuilder listen(MessageListener listener) {
        this.createMessageConsumer();
        try{
            this.messageConsumer.setMessageListener(listener);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
        }
        return this;
    }

    @Override
    public QueueBuilder newBuilder() {
        return new QueueBuilderImpl(this.exceptionEvent);
    }

    @Override
    public QueueBuilder transacted() {
        this.transacted = !this.transacted;
        return this;
    }

    @Override
    public QueueBuilder sessionMode(int sessionMode) {
        this.sessionMode = sessionMode;
        return this;
    }
    
    public QueueBrowser getQueueBrowser() {
        try{
            return this.session.createBrowser(this.lastQueue);
        } catch (JMSException ex) {
            this.exceptionEvent.fire(new ExceptionToCatch(ex));
            return null;
        }
    }
}
