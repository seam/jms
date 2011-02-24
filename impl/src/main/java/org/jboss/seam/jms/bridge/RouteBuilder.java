/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.seam.jms.bridge;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.Seam3JmsExtension;
import org.jboss.seam.jms.annotations.Closeable;
import org.jboss.seam.solder.literal.NewLiteral;

/**
 *
 * @author johnament
 */
@Singleton
public class RouteBuilder implements java.io.Serializable {

    private Logger log = Logger.getLogger(RouteBuilder.class);
    private List<Route> ingressRoutes;
    @Inject Seam3JmsExtension extension;
   public RouteBuilder() {
        Logger log = Logger.getLogger(RouteBuilder.class);
        log.info("Creating a new RouteBuilder()");
    }
   @PostConstruct
   public void init() throws JMSException {
       log.info("Calling RouteBuilder.init");
       extension.setBeanManager(beanManager);
       ingressRoutes = extension.getIngressRoutes();
       log.info("Ingress routes size: ("+ingressRoutes.size()+") "+ingressRoutes);
       connection.start();
       for(Route ingressRoute : ingressRoutes)
            createListener(ingressRoute);
   }
    @Inject
    @Closeable
    Event<MessageConsumer> closeMessageConsumer;
    @Inject
    Connection connection;
    @Inject
    Session session;
    @Inject
    Instance<IngressMessageListener> listeners;
    @Inject BeanManager beanManager;

    private void createListener(Route ingressRoute) {
        ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
        log.info("About to create listener for route "+ingressRoute);
        log.info("Routes: "+ingressRoute.getDestinations());
        for (Destination d : ingressRoute.getDestinations()) {
            IngressMessageListener listener = new IngressMessageListener(beanManager,Thread.currentThread().getContextClassLoader());
            listener.setRoute(ingressRoute);
            try {
                MessageConsumer consumer = session.createConsumer(d);
                consumer.setMessageListener(listener);
            } catch (JMSException ex) {
                log.warnf("Unable to create consumer for destination %s", d, ex);
            }
        }
    }

}
