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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.solder.logging.Logger;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.Seam3JmsExtension;
import org.jboss.seam.jms.annotations.OutboundLiteral;
import org.jboss.seam.solder.bean.ImmutableInjectionPoint;
import org.jboss.seam.solder.core.Veto;

import static org.jboss.seam.jms.annotations.RoutingLiteral.EGRESS;

/**
 * Forwards CDI events that match the provided {@link Route} configuration to
 * the configured destinations.
 *
 * @author Jordan Ganoff
 */
@Veto
public class EgressRoutingObserver implements ObserverMethod<Object> {

    private Logger log;
    private BeanManager bm;
    private Route route;
    private Seam3JmsExtension extension;

    public EgressRoutingObserver(BeanManager bm, Route routing, Seam3JmsExtension extension) {
        this(routing, extension);
        this.bm = bm;
    }

    public EgressRoutingObserver(Route route, Seam3JmsExtension extension) {
        this.route = route;
        this.extension = extension;
        this.log = Logger.getLogger(EgressRoutingObserver.class);
    }

    public void setBeanManager(BeanManager beanManager) {
        this.bm = beanManager;
        this.loadDestinations();
    }

    public Class<?> getBeanClass() {
        return getClass();
    }

    public Set<Annotation> getObservedQualifiers() {
        Set<Annotation> as = new HashSet<Annotation>();
        as.addAll(route.getQualifiers());
        as.add(EGRESS);
        as.add(OutboundLiteral.INSTANCE);
        log.debugf("Inidicating that I observe these qualifiers: [%s]", as);
        return route.getQualifiers();
    }

    public Type getObservedType() {
        return route.getPayloadType();
    }

    public Reception getReception() {
        return Reception.ALWAYS;
    }

    public TransactionPhase getTransactionPhase() {
        return TransactionPhase.AFTER_SUCCESS;
    }

    public void notify(Object evt) {
        // FIXME Include qualifiers once CDI 1.0 MR is complete and
        // notify(Event, Set<Annotation>) is added
        log.debugf("Notified of an event: %s", evt);
        if (this.extension.isReadyToRoute())
            forwardEvent(evt);
        else {
            this.log.warn("Adding event to evt cache " + evt);
            evtCache.add(evt);
        }
    }

    private List<Object> evtCache = new ArrayList<Object>();

    private MessageManager getMessageBuilder() {
        Set<Bean<?>> beans = bm.getBeans(MessageManager.class);
        Bean<?> bean = bm.resolve(beans);
        MessageManager mb = (MessageManager) bm.getReference(bean, MessageManager.class, bm.createCreationalContext(bean));
        return mb;
    }

    private void loadDestinations() {
        Set<Destination> destinations = new HashSet<Destination>();
        destinations.addAll(route.getDestinations());
        for (String dest : route.getDestinationJndiNames()) {
            Destination destination = lookupDestination(dest);
            destinations.add(destination);
        }
        for (AnnotatedParameter<?> ap : route.getAnnotatedParameters()) {
            Destination destination = lookupDestination(ap);
            destinations.add(destination);
        }
        log.infof("Routing destinations: [%s]", destinations);
        this.route.setDestinations(destinations);
    }

    private Destination lookupDestination(String jndiName) {
        try {
            Context c = new InitialContext();
            return (Destination) c.lookup(jndiName);
        } catch (NamingException e) {
            log.warn("Unable to lookup " + jndiName, e);
        }
        return null;
    }

    private Destination lookupDestination(AnnotatedParameter<?> ap) {
        log.debug("Looking up destination: " + ap);
        Set<Bean<?>> beans = bm.getBeans(Destination.class);
        Bean<?> bean = bm.resolve(beans);
        ImmutableInjectionPoint iip = new ImmutableInjectionPoint(ap, bm, bean, false, false);
        Object o = bm.getInjectableReference(iip, bm.createCreationalContext(bean));
        return (Destination) o;
    }

    private void forwardEvent(Object event) {
        if (!this.route.isEgressEnabled())
            return;
        MessageManager msgBuilder = this.getMessageBuilder();
        if (event instanceof String) {
            msgBuilder.sendTextToDestinations(event.toString(), route.getDestinations().toArray(new Destination[]{}));
        } else {
            msgBuilder.sendObjectToDestinations(event, route.getDestinations().toArray(new Destination[]{}));
        }

    }
}
