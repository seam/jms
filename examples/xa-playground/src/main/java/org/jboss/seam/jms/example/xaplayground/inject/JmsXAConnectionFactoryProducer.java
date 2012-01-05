package org.jboss.seam.jms.example.xaplayground.inject;

import org.jboss.seam.jms.annotations.JmsDefault;
import org.jboss.solder.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.XAConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

@ApplicationScoped
public class JmsXAConnectionFactoryProducer {

    private String connectionFactoryJNDILocation = "/JmsXA";

    private Logger logger = Logger.getLogger(JmsXAConnectionFactoryProducer.class);

    @Inject
    Context context;

    @Produces
    @ApplicationScoped
    @JmsDefault("XAConnectionFactory")
    public XAConnectionFactory produceConnectionFactory()
    {
        try {
            return (XAConnectionFactory) context.lookup(connectionFactoryJNDILocation);
        } catch (NamingException e) {
            logger.info("Unable to look up " + connectionFactoryJNDILocation + " in JNDI", e);
            return null;
        }
    }
}
