/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.Seam3JmsExtension;
import org.jboss.seam.jms.annotations.Closeable;
import org.jboss.seam.jms.annotations.Module;
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
    @Inject @Module ConnectionFactory cf;
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
       for(Route ingressRoute : ingressRoutes) {
           ingressRoute.build(beanManager);
           createListener(ingressRoute,cf);
       }
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

    private void createListener(Route ingressRoute,ConnectionFactory cf) {
        ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
        log.info("About to create listener for route "+ingressRoute);
        log.info("Routes: "+ingressRoute.getDestinations());
        for (Destination d : ingressRoute.getDestinations()) {
            IngressMessageListener listener = new IngressMessageListener(beanManager,Thread.currentThread().getContextClassLoader());
            listener.setRoute(ingressRoute);
            try {
                Connection conn = cf.createConnection();
                conn.start();
                Session sess = conn.createSession(true, Session.DUPS_OK_ACKNOWLEDGE);
                log.info("Creating a consumer for destination "+d);
                MessageConsumer consumer = sess.createConsumer(d);
                consumer.setMessageListener(listener);
            } catch (JMSException ex) {
                log.warnf("Unable to create consumer for destination %s", d, ex);
            }
        }
    }

}
