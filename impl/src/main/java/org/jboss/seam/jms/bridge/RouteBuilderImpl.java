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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.Seam3JmsExtension;

@Singleton
public class RouteBuilderImpl implements RouteBuilder, java.io.Serializable {

	private static final long serialVersionUID = -6782656668733696386L;
	private Logger log = Logger.getLogger(RouteBuilderImpl.class);
	private List<Route> ingressRoutes;
	@Inject
	Seam3JmsExtension extension;
	@Inject
	MessageManager messageBuilder;
	@Inject
	BeanManager beanManager;
	
	public RouteBuilderImpl() {
		Logger log = Logger.getLogger(RouteBuilderImpl.class);
		log.debug("Creating a new RouteBuilder()");
	}

	@Override
	public void handleStartup(@Observes ServletContext servletContext) {
		log.debug("Starting up Seam JMS via ServletContext callback.");
	}

	@PostConstruct
	@Override
	public void init() throws JMSException {
		log.debug("Calling RouteBuilder.init");
		extension.setBeanManager(beanManager);
		ingressRoutes = extension.getIngressRoutes();
		log.debug("Ingress routes size: (" + ingressRoutes.size() + ") "
				+ ingressRoutes);
		for (Route ingressRoute : ingressRoutes) {
			ingressRoute.build(beanManager);
			createListener(ingressRoute);
		}
	}

	private void createListener(Route ingressRoute) {
		ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
		log.debug("About to create listener for route " + ingressRoute);
		log.debug("Routes: " + ingressRoute.getDestinations());
		for (Destination d : ingressRoute.getDestinations()) {
			IngressMessageListener listener = new IngressMessageListener(
					beanManager, prevCl, ingressRoute);
			this.messageBuilder.createMessageConsumer(d, listener);
		}
	}

    @Override
    public void registerDurableIngressRoute(Route ingressRoute, String clientId) {
        if(ingressRoute.getType() == RouteType.INGRESS) {
            ingressRoute.build(beanManager);
            ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
            log.debug("About to create listener for route " + ingressRoute);
            log.debug("Routes: " + ingressRoute.getDestinations());
            for (Destination d : ingressRoute.getDestinations()) {
                    IngressMessageListener listener = new IngressMessageListener(
                                    beanManager, prevCl, ingressRoute);
                    this.messageBuilder.createDurableSubscriber(d, clientId, listener);
            }
        } else {
            throw new IllegalArgumentException("Route "+ingressRoute+" is not valid, it must be an ingress route.");
        }
    }

    @Override
    public void unregisterRoute(String clientId) {
        this.messageBuilder.unsubscribe(clientId);
    }

}
