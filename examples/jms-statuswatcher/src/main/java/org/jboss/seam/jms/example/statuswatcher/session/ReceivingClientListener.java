package org.jboss.seam.jms.example.statuswatcher.session;

import javax.enterprise.inject.spi.BeanManager;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.AbstractMessageListener;
import org.jboss.seam.jms.example.statuswatcher.model.Status;

/**
 *
 * @author johnament
 */
public class ReceivingClientListener extends AbstractMessageListener {
    private ReceivingClient client;
    private Logger logger;
    public ReceivingClientListener(BeanManager beanManager, ClassLoader classLoader,
            ReceivingClient client) {
        super(beanManager,classLoader);
        this.client = client;
        this.logger = Logger.getLogger(ReceivingClientListener.class);
    }

    @Override
    protected void handleMessage(Message message) throws JMSException {
        if(message instanceof ObjectMessage) {
            ObjectMessage om = (ObjectMessage)message;
            Object obj = om.getObject();
            if(obj instanceof Status){
                client.notify((Status)obj);
                return;
            }
        }
        logger.infof("Received an invalid message %s",message);
    }

}
