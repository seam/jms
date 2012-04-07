package org.jboss.seam.jms.example.xaplayground;

import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.example.xaplayground.inject.XA;
import org.jboss.seam.transaction.Transactional;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.logging.Logger;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.Queue;
import javax.transaction.UserTransaction;
import java.util.Date;

@HandlesExceptions
@Model
public class TestView {
// ------------------------------ FIELDS ------------------------------

    @Inject
    Event<String> stringEvent;

    @Inject
    private FacesContext facesContext;

    @Inject
    @JmsDestination(jndiName = Constants.DEFAULT_QUEUE_JNDI)
    private Queue mailQueue;

    @Inject
    private MessageManager messageManager;

    @Inject
    @XA
    private MessageManager messageManagerXA;

    @Inject
    private Logger logger;

// -------------------------- OTHER METHODS --------------------------

    public void handleMeantException(@Handles CaughtException<MeantException> e)
    {
        logger.info(e.getException().getLocalizedMessage());
        facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, "home");
        e.handled();
    }

    public void sendBridgedEvents()
    {
        stringEvent.fire("Listening to Florence: " + new Date());
        throw new MeantException();
    }

    @Transactional
    public void sendBridgedEventsTransactional()
    {
        sendBridgedEvents();
    }

    public void sendNonXA()
    {
        Message jmsMessage = messageManager.createTextMessage("Hello");
        messageManager.sendMessage(jmsMessage, mailQueue);
        throw new MeantException();
    }

    @Transactional
    public void sendNonXATransactional()
    {
        sendNonXA();
    }

    public void sendXA()
    {
        Message jmsMessage = messageManager.createTextMessage("Hello");
        messageManagerXA.sendMessage(jmsMessage, mailQueue);
        throw new MeantException();
    }

    @Inject
    private UserTransaction seamTransaction;

    @Transactional
    public void sendXATransactional()
    {
        sendXA();
    }
}
