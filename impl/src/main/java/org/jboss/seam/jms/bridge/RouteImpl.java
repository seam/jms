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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.spi.AnnotatedParameter;

import javax.inject.Qualifier;
import javax.jms.Destination;
import org.jboss.logging.Logger;

/**
 * JMS Event Bridge Routing
 * 
 * @author Jordan Ganoff
 * 
 */
public class RouteImpl implements Route
{
   private RouteType type;
   private Type payloadType;
   private Set<Annotation> qualifiers;
   private Set<Destination> destinations;
   private List<Set<Annotation>> destinationQualifiers;
   private Set<String> destinationJndiNames;
   private Set<AnnotatedParameter<?>> annotatedParameters;
   private Logger logger;

   public RouteImpl(RouteType type, Type payloadType)
   {
      this(type);
      this.payloadType = payloadType;
   }

   public RouteImpl(RouteType type)
   {
      logger = Logger.getLogger(RouteImpl.class);
      this.type = type;
      qualifiers = new HashSet<Annotation>();
      destinations = new HashSet<Destination>();
      destinationQualifiers = new ArrayList<Set<Annotation>>();
      destinationJndiNames = new HashSet<String>();
      annotatedParameters = new HashSet<AnnotatedParameter<?>>();
   }

   public Route addQualifiers(Collection<Annotation> q)
   {
      if (q != null)
      {
         for (Annotation qualifier : q)
         {
            if (!qualifier.annotationType().isAnnotationPresent(Qualifier.class))
            {
               throw new IllegalArgumentException("not a qualifier: " + qualifier);
            }
            qualifiers.add(qualifier);
         }
      }
      return this;
   }

   public Route addQualifiers(Annotation... q)
   {
      return addQualifiers(Arrays.asList(q));
   }
    public Route addDestinations(Destination... d) {
        return addDestinations(Arrays.asList(d));
    }

    public Route addDestinations(Collection<Destination> d) {
        this.destinations.addAll(d);
        return this;
    }

   public <D extends Destination> Route connectTo(java.lang.Class<D> d, D destination)
   {
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

   public RouteType getType()
   {
      return type;
   }

   public Type getPayloadType()
   {
      return payloadType;
   }

   public Set<Annotation> getQualifiers()
   {
      return qualifiers;
   }

   public Set<? extends Destination> getDestinations()
   {
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

    public Set<AnnotatedParameter<?>> getAnnotatedParameters() {
        return this.annotatedParameters;
    }

    @Override
    public Route setType(Type type) {
        this.payloadType = type;
        return this;
    }

    public boolean validate() {
        if(this.payloadType == null) {
            logger.debug("No payload type found.");
            return false;
        }
        if(this.annotatedParameters.isEmpty() && this.destinationJndiNames.isEmpty() && this.destinations.isEmpty()) {
            logger.debug("No destinations configured.");
            return false;
        }

        return true;
    }

    public void setDestinations(Collection<Destination> destinations) {
        this.destinations = new HashSet<Destination>(destinations);
    }
}
