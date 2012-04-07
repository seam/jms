package org.jboss.seam.jms.example.xaplayground;

import org.jboss.solder.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(name = "GoodlListener",
    activationConfig = {@ActivationConfigProperty(propertyName = "destination", propertyValue = Constants.DEFAULT_QUEUE_JNDI),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Session-transacted")})
public class GoodlListener implements MessageListener {
// ------------------------------ FIELDS ------------------------------

    @Inject
    private Logger logger;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface MessageListener ---------------------

    @Override
    public void onMessage(Message message)
    {
        logger.info(message);
    }
}
