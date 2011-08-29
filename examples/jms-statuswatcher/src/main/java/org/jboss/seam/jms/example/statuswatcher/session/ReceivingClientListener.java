package org.jboss.seam.jms.example.statuswatcher.session;

import javax.enterprise.inject.spi.BeanManager;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.jboss.seam.logging.Logger;
import org.jboss.seam.jms.AbstractMessageListener;
import org.jboss.seam.jms.example.statuswatcher.model.Status;

/**
 * @author johnament
 */
public class ReceivingClientListener implements javax.jms.MessageListener {
    private ReceivingClient client;
    private Logger logger;

    public ReceivingClientListener(ReceivingClient client) {
        this.client = client;
        this.logger = Logger.getLogger(ReceivingClientListener.class);
    }

    @Override
    public void onMessage(Message message) {
        try{
        	if (message instanceof ObjectMessage) {
	            ObjectMessage om = (ObjectMessage) message;
	            Object obj = om.getObject();
	            Integer id = (Integer)obj;
	            client.notify(id);
	        } else {
	        	logger.infof("Received an invalid message %s", message);
	        }
        } catch (JMSException e) {
        	logger.warn("Unable to handle JMS Message: ",e);
        }
    }

}
