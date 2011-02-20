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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedParameter;

import javax.jms.Destination;

/**
 * Routing configuration between CDI and JMS.
 * 
 * @author Jordan Ganoff
 * 
 */
public interface Route {

    /**
     * Connect this route to a destination. Multiple destinations may be defined.
     *
     * @param <D> Destination type
     * @param d Destination type (e.g. javax.jms.Topic)
     * @param destination Destination to connect this route to
     * @return this
     */
    public <D extends Destination> Route connectTo(Class<D> d, D destination);

    /**Add multiple destinations to this route
     *
     * @param d destinations to be added.
     * @return this
     */
    public Route addDestinations(Destination... d);

    /**Adds a collection of destinations to this route.
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

    public List<Set<Annotation>> getDestinationQualifiers();

    public Route addDestinationQualifiers(Set<Annotation> qualifiers);

    public Route addAnnotatedParameter(AnnotatedParameter<?> ap);

    public Set<AnnotatedParameter<?>> getAnnotatedParameters();

    public Route addDestinationJndiName(String jndi);

    public Route setType(Type type);

    public boolean validate();
}
