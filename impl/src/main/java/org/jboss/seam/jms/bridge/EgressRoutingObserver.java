/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.jms.bridge;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.inject.Named;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.Seam3JmsExtension;
import org.jboss.seam.solder.bean.ImmutableInjectionPoint;

/**
 * Forwards CDI events that match the provided {@link Route} configuration to
 * the configured destinations.
 * 
 * @author Jordan Ganoff
 * 
 */
@Named
@ApplicationScoped
public class EgressRoutingObserver implements ObserverMethod<Object> {

    private Logger log;
    private BeanManager bm;
    private Route routing;
    private Seam3JmsExtension extension;

    public EgressRoutingObserver(BeanManager bm, Route routing, Seam3JmsExtension extension) {
        this(routing, extension);
        this.bm = bm;
    }

    public EgressRoutingObserver(Route route, Seam3JmsExtension extension) {
        this.routing = route;
        this.extension = extension;
        this.log = Logger.getLogger(EgressRoutingObserver.class);
    }

    public void setBeanManager(BeanManager beanManager) {
        this.bm = beanManager;
    }

    public Class<?> getBeanClass() {
        return getClass();
    }

    public Set<Annotation> getObservedQualifiers() {
        return routing.getQualifiers();
    }

    public Type getObservedType() {
        return routing.getPayloadType();
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
        if(this.extension.isReadyToRoute())
            forwardEvent(evt, null);
        else
            evtCache.add(evt);
    }

    private List<Object> evtCache = new ArrayList<Object>();

    private Session getSession() {
        Set<Bean<?>> beans = bm.getBeans(Session.class);
        Bean<?> bean = bm.resolve(beans);
        Session s = (Session) bm.getReference(bean, Session.class, bm.createCreationalContext(bean));
        return s;
    }

    private Destination lookupDestination(String jndiName) {
        try{
            Context c = new InitialContext();
            return (Destination)c.lookup(jndiName);
        } catch (NamingException e) {
            log.warn("Unable to lookup "+jndiName,e);
        }
        return null;
    }

    private Destination lookupDestination(AnnotatedParameter<?> ap) {
        Set<Bean<?>> beans = bm.getBeans(Destination.class);
        Bean<?> bean = bm.resolve(beans);
        ImmutableInjectionPoint iip = new ImmutableInjectionPoint(ap,bm,bean,false,false);
        Object o = bm.getInjectableReference(iip, bm.createCreationalContext(bean));
        return (Destination)o;
    }

    private void forwardEvent(Object event, Set<Annotation> qualifiers) {
        // TODO Allow session to be configured
        Session s = getSession();
        Set<Destination> destinations = new HashSet<Destination>();
        destinations.addAll(routing.getDestinations());
        for(String dest : routing.getDestinationJndiNames()) {
            Destination destination = lookupDestination(dest);
            destinations.add(destination);
        }
        for(AnnotatedParameter<?> ap : routing.getAnnotatedParameters()) {
            Destination destination = lookupDestination(ap);
            destinations.add(destination);
        }
        try {
            for (Destination d : destinations) {
                log.infof("Routing event %s over destination %s", event, d);
                try {
                    Message m = s.createObjectMessage((Serializable) event);
                    // Safe to create producers here always? In an app server these
                    // should be cached via JCA managed connection factory but what
                    // about other environments?
                    s.createProducer(d).send(m);
                } catch (JMSException ex) {
                    log.error("Unable to forward event", ex);
                }
            }
        } finally {
            try {
                s.close();
            } catch (JMSException ex) {
                log.error("Unable to close session", ex);
            }
        }
    }
}
