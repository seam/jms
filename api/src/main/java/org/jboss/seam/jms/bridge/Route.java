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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.BeanManager;
import javax.jms.Destination;

/**
 * Routing configuration between CDI and JMS.
 *
 * @author Jordan Ganoff
 */
public interface Route {

    /**
     * Connect this route to a destination. Multiple destinations may be defined.
     *
     * @param <D>         Destination type
     * @param d           Destination type (e.g. javax.jms.Topic)
     * @param destination Destination to connect this route to
     * @return this
     */
    public <D extends Destination> Route connectTo(Class<D> d, D destination);

    /**
     * Add multiple destinations to this route
     *
     * @param d destinations to be added.
     * @return this
     */
    public Route addDestinations(Destination... d);

    /**
     * Adds a collection of destinations to this route.
     *
     * @param d Destinations to be added
     * @return this
     */
    public Route addDestinations(Collection<Destination> d);

    /**
     * Apply the qualifiers listed to this route.
     *
     * @param qualifiers Qualifiers for the payload type
     * @return this
     */
    public Route addQualifiers(Annotation... qualifiers);

    /**
     * Apply the collection of qualifiers to this route.
     *
     * @param q qualifiers to be added
     * @return this
     */
    public Route addQualifiers(Collection<Annotation> q);

    /**
     * @return the routing type
     */
    public RouteType getType();

    /**
     * @return the type this route routes
     */
    public Type getPayloadType();

    /**
     * @return the qualifiers
     */
    public Set<Annotation> getQualifiers();

    /**
     * @return The destinations involved in this routing
     */
    public Set<? extends Destination> getDestinations();

    public Set<String> getDestinationJndiNames();

    public void setDestinations(Collection<Destination> destinations);

    public List<Set<Annotation>> getDestinationQualifiers();

    public Route addDestinationQualifiers(Set<Annotation> qualifiers);

    public Route addAnnotatedParameter(AnnotatedParameter<?> ap);

    public Set<AnnotatedParameter<?>> getAnnotatedParameters();

    public Route addDestinationJndiName(String jndi);

    public Route setType(Type type);

    public boolean validate();

    public void build(BeanManager beanManager);

    public Route id(String id);

    public String getId();

    /**
     * Determines if this route is enabled for Egress routes.
     * If the route is ingress, this returns false always.
     *
     * @return true if enabled for egress routes, else false.
     */
    public boolean isEgressEnabled();

    /**
     * Determines if this route is enabled for ingress routes.
     * If the route is egress, this returns false always.
     *
     * @return true if enabled for ingress routes, else false.
     */
    public boolean isIngressEnabled();

    /**
     * Disables egress routing for this route.
     */
    public void disableEgress();

    /**
     * Enables egress routing for this route.
     * Ineffective if the route is ingress.
     */
    public void enableEgress();

    /**
     * Disables ingress routing for this route.
     */
    public void disableIngress();

    /**
     * Enables ingress routing for this route.
     * Ineffective if the route is egress.
     */
    public void enableIngress();
}
