package org.jboss.seam.jms.example.statuswatcher.messagedriven;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.example.statuswatcher.model.Status;
import org.jboss.seam.jms.example.statuswatcher.session.StatusManager;

@MessageDriven(name = "OrderProcessor", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "/jms/updateStatusQueue")})
public class DistributorMDB implements MessageListener {
    @Inject
    private Logger log;

    @Inject
    private StatusManager manager;

    @Resource(mappedName = "/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "/jms/statusInfoTopic")
    private Topic statusTopic;

    @Override
    public void onMessage(Message message) {
        Connection connection = null;
        try {
            ObjectMessage om = (ObjectMessage) message;
            Status status = (Status) om.getObject();
            status = manager.addStatusMessage(status);
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicPublisher publisher = ((TopicSession) session).createPublisher(statusTopic);
            ObjectMessage update = session.createObjectMessage(status);
            publisher.send(update);
        } catch (JMSException e) {
            log.error(e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.warn("Closing of a connection failed");
                }
            }
        }
    }
}
