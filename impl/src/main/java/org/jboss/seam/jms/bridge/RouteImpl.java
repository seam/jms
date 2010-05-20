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
import java.util.HashSet;
import java.util.Set;

import javax.inject.Qualifier;
import javax.jms.Destination;

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

   public RouteImpl(RouteType type, Type payloadType)
   {
      this.type = type;
      this.payloadType = payloadType;
      qualifiers = new HashSet<Annotation>();
      destinations = new HashSet<Destination>();
   }

   public Route addQualifiers(Annotation... q)
   {
      if (q != null)
      {
         for (int i = 0; i < q.length; i++)
         {
            Annotation qualifier = q[i];
            if (!qualifier.annotationType().isAnnotationPresent(Qualifier.class))
            {
               throw new IllegalArgumentException("not a qualifier: " + qualifier);
            }
            qualifiers.add(q[i]);
         }
      }
      return this;
   }

   public <D extends Destination> Route connectTo(java.lang.Class<D> d, D destination)
   {
      destinations.add(Destination.class.cast(destination));
      return this;
   };

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
}
