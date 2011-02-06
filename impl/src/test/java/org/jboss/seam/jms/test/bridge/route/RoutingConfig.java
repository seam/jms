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
package org.jboss.seam.jms.test.bridge.route;

import static org.jboss.seam.jms.bridge.RouteType.EGRESS;

import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.jms.Queue;

import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.bridge.EventBridge;
import org.jboss.seam.jms.bridge.Route;

public class RoutingConfig
{
   @Inject EventBridge bridge;

   @Inject @JmsDestination(jndiName = "queue/DLQ") Queue q;
   
   private static final AnnotationLiteral<BridgedViaCollection> BRIDGED_VIA_COLLECTION = new AnnotationLiteral<BridgedViaCollection>()
   {
      private static final long serialVersionUID = 1L;
   };

   private static final AnnotationLiteral<BridgedViaRoute> BRIDGED_VIA_ROUTE = new AnnotationLiteral<BridgedViaRoute>()
   {
      private static final long serialVersionUID = 1L;
   };

   @Produces
   public Collection<Route> getRoutes()
   {
      return Arrays.asList(bridge.createRoute(EGRESS, String.class).addQualifiers(BRIDGED_VIA_COLLECTION).connectTo(Queue.class, q));
   }
   
   @Produces
   public Route getRoute()
   {
      return bridge.createRoute(EGRESS, String.class).addQualifiers(BRIDGED_VIA_ROUTE).connectTo(Queue.class, q);
   }
}
