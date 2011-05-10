package org.jboss.seam.jms.bridge;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.jms.JMSException;
import javax.servlet.ServletContext;

/**
 * RouteBuilder is a start up component responsible for loading the finalized
 * BeanManager into the Seam3JmsExtension and then loading all destinations
 * that will be used by the ingress routes.
 * <p/>
 * Loading the BeanManager into the Seam3JmsExtension has the result of doing
 * the same thing to the egress routes.
 * <p/>
 * Implementations of RouteBuilder should be singleton, and defines start up
 * capabilities in handleStartup (servlet containers) and init.
 *
 * @author johnament
 */
public interface RouteBuilder extends Serializable {
    public void handleStartup(@Observes ServletContext servletContext);

    @PostConstruct
    public void init() throws JMSException;
    //public void registerDurableIngressRoute(Route route, String clientId);
    //public void unregisterRoute(String clientId);
}
