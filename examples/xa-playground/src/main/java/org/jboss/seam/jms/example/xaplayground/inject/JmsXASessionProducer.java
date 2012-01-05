package org.jboss.seam.jms.example.xaplayground.inject;

import org.jboss.seam.jms.annotations.JmsDefault;
import org.jboss.solder.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XASession;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class JmsXASessionProducer {
// ------------------------------ FIELDS ------------------------------

    @Inject
    @JmsDefault("XAConnectionFactory")
    private XAConnectionFactory connectionFactory;

    /**
     * We can create only one session per conneciton cause otherwise we would get:
     * javax.jms.IllegalStateException: Only allowed one session per connection. See the J2EE spec, e.g. J2EE1.4 Section 6.6
     * <p/>
     * So we need to cache them.
     * TODO doesn't the server take care of closing connections (pooling)?
     */
    private Map<Session, Connection> connectionMap = new HashMap<Session, Connection>();

    @Inject
    private Logger logger;

// -------------------------- OTHER METHODS --------------------------

    public void close(@Disposes @JmsDefault("XASession") Session session)
    {
        Connection connection = connectionMap.remove(session);
        if (connection != null) {
            try {
                logger.debugv("Closing connection for session {0}", session);
                connection.close();
            } catch (JMSException e) {
                logger.errorv(e, "Cannot close JMS connection");
            }
        }
    }

    @Produces
    @Dependent
    @JmsDefault("XASession")
    public Session produceSession() throws JMSException
    {
        XAConnection connection = connectionFactory.createXAConnection();
        XASession session = connection.createXASession();
        connectionMap.put(session, connection);
        return session;
    }
}
