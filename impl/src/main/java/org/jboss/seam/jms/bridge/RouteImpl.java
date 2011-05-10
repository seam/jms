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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Qualifier;
import javax.jms.Destination;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.bean.ImmutableInjectionPoint;

/**
 * JMS Event Bridge Routing
 *
 * @author Jordan Ganoff
 */
public class RouteImpl implements Route {
    private RouteType type;
    private String id;
    private Type payloadType;
    private Set<Annotation> qualifiers;
    private Set<Destination> destinations;
    private List<Set<Annotation>> destinationQualifiers;
    private Set<String> destinationJndiNames;
    private Set<AnnotatedParameter<?>> annotatedParameters;
    private Logger logger;

    public RouteImpl(RouteType type, Type payloadType) {
        this(type);
        this.payloadType = payloadType;
    }

    public RouteImpl(RouteType type) {
        logger = Logger.getLogger(RouteImpl.class);
        this.type = type;
        this.enableEgress();
        this.enableIngress();
        qualifiers = new HashSet<Annotation>();
        destinations = new HashSet<Destination>();
        destinationQualifiers = new ArrayList<Set<Annotation>>();
        destinationJndiNames = new HashSet<String>();
        annotatedParameters = new HashSet<AnnotatedParameter<?>>();
    }

    public Route addQualifiers(Collection<Annotation> q) {
        if (q != null) {
            for (Annotation qualifier : q) {
                if (!qualifier.annotationType().isAnnotationPresent(Qualifier.class)) {
                    throw new IllegalArgumentException("not a qualifier: " + qualifier);
                }
                qualifiers.add(qualifier);
            }
        }
        return this;
    }

    public Route addQualifiers(Annotation... q) {
        return addQualifiers(Arrays.asList(q));
    }

    public Route addDestinations(Destination... d) {
        return addDestinations(Arrays.asList(d));
    }

    public Route addDestinations(Collection<Destination> d) {
        this.destinations.addAll(d);
        return this;
    }

    public <D extends Destination> Route connectTo(java.lang.Class<D> d, D destination) {
        destinations.add(Destination.class.cast(destination));
        return this;
    }

    public Route addDestinationQualifiers(Set<Annotation> qualifiers) {
        this.destinationQualifiers.add(qualifiers);
        return this;
    }

    public Route addDestinationJndiName(String jndi) {
        this.destinationJndiNames.add(jndi);
        return this;
    }

    public RouteType getType() {
        return type;
    }

    public Type getPayloadType() {
        return payloadType;
    }

    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    public Set<? extends Destination> getDestinations() {
        return destinations;
    }

    public Set<String> getDestinationJndiNames() {
        return destinationJndiNames;
    }

    public List<Set<Annotation>> getDestinationQualifiers() {
        return destinationQualifiers;
    }

    public Route addAnnotatedParameter(AnnotatedParameter<?> ap) {
        this.annotatedParameters.add(ap);
        return this;
    }

    private boolean built = false;
    private BeanManager bm;

    public void build(BeanManager beanManager) {
        if (!built) {
            this.bm = beanManager;
            loadDestinations();
            this.built = true;
        }
    }

    private void loadDestinations() {
        Set<Destination> destinations = new HashSet<Destination>();
        destinations.addAll(getDestinations());
        for (String dest : getDestinationJndiNames()) {
            Destination destination = lookupDestination(dest);
            destinations.add(destination);
        }
        for (AnnotatedParameter<?> ap : getAnnotatedParameters()) {
            Destination destination = lookupDestination(ap);
            destinations.add(destination);
        }
        logger.infof("Routing destinations: [%s]", destinations);
        setDestinations(destinations);
    }

    private Destination lookupDestination(String jndiName) {
        try {
            Context c = new InitialContext();
            return (Destination) c.lookup(jndiName);
        } catch (NamingException e) {
            logger.warn("Unable to lookup " + jndiName, e);
        }
        return null;
    }

    private Destination lookupDestination(AnnotatedParameter<?> ap) {
        logger.debug("Looking up destination: " + ap);
        Set<Bean<?>> beans = bm.getBeans(Destination.class);
        Bean<?> bean = bm.resolve(beans);
        ImmutableInjectionPoint iip = new ImmutableInjectionPoint(ap, bm, bean, false, false);
        Object o = bm.getInjectableReference(iip, bm.createCreationalContext(bean));
        return (Destination) o;
    }

    public Set<AnnotatedParameter<?>> getAnnotatedParameters() {
        return this.annotatedParameters;
    }

    @Override
    public Route setType(Type type) {
        this.payloadType = type;
        return this;
    }

    public boolean validate() {
        if (this.payloadType == null) {
            logger.debug("No payload type found.");
            return false;
        }
        if (this.annotatedParameters.isEmpty() && this.destinationJndiNames.isEmpty() && this.destinations.isEmpty()) {
            logger.debug("No destinations configured.");
            return false;
        }

        return true;
    }

    public void setDestinations(Collection<Destination> destinations) {
        this.destinations = new HashSet<Destination>(destinations);
    }

    @Override
    public Route id(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getId() {
        return this.id;
    }

    private boolean egressEnabled = false;
    private boolean ingressEnabled = false;

    @Override
    public boolean isEgressEnabled() {
        return egressEnabled;
    }

    @Override
    public boolean isIngressEnabled() {
        return ingressEnabled;
    }

    @Override
    public void disableEgress() {
        this.egressEnabled = false;
    }

    @Override
    public void enableEgress() {
        if (this.type == RouteType.BOTH || this.type == RouteType.EGRESS) {
            this.egressEnabled = true;
        } else {
            this.egressEnabled = false;
        }
    }

    @Override
    public void disableIngress() {
        this.ingressEnabled = false;
    }

    @Override
    public void enableIngress() {
        if (this.type == RouteType.BOTH || this.type == RouteType.INGRESS) {
            this.ingressEnabled = true;
        } else {
            this.ingressEnabled = false;
        }
    }
}
