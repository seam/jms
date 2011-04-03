package org.jboss.seam.jms;

import javax.enterprise.inject.spi.BeanManager;
import javax.jms.JMSException;
import javax.jms.Message;

import org.jboss.logging.Logger;

/**
 * Supporting base MessageListener for working in CDI enabled environments.
 * This is useful for having a MessageListener 
 * 
 * @author johnament
 *
 */
public abstract class AbstractMessageListener implements javax.jms.MessageListener {

    private Logger logger;
    protected BeanManager beanManager;
    protected ClassLoader classLoader;

    protected AbstractMessageListener() {
        this.logger = Logger.getLogger(AbstractMessageListener.class);
    }

    protected AbstractMessageListener(BeanManager beanManager, ClassLoader classLoader) {
        this();
        this.beanManager = beanManager;
        this.classLoader = classLoader;
        logger.debug("Creating new AbstractMessageListener.");
    }

    protected void setBeanManager(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    protected void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * AbstractMessageListener implements the basic on message functionality to
     * handle classloader behavior for working in CDI environments.
     *
     * This method should not be overridden, even though it is not final.
     *
     * @param message The JMS Message that is being received.
     */
    public void onMessage(Message message) {
        logger.info("Received a message");
        ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            handleMessage(message);
        } catch (JMSException e) {
            logger.warn("A JMS Exception occurred during processing.", e);
        } finally {
            Thread.currentThread().setContextClassLoader(prevCl);
        }
    }

    /**
     * Implementations should override this method and
     * perform necessary business logic in here.
     *
     * A BeanManager reference is available, for looking up beans.
     *
     * @param message The message to be handled.
     * @throws JMSException The method can throw this exception if an error occurred.
     */
    protected abstract void handleMessage(Message message) throws JMSException;
}
