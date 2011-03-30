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
package org.jboss.seam.jms.test.bridge.route;

import static org.jboss.seam.jms.bridge.RouteType.EGRESS;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Resource;

import javax.enterprise.util.AnnotationLiteral;
import javax.jms.Queue;
import org.jboss.seam.jms.annotations.EventRouting;

import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.bridge.RouteManager;

public class RoutingConfig
{
   @Resource(mappedName="queue/DLQ") Queue q;
   
   private static final AnnotationLiteral<BridgedViaCollection> BRIDGED_VIA_COLLECTION = new AnnotationLiteral<BridgedViaCollection>()
   {
      private static final long serialVersionUID = 1L;
   };

   private static final AnnotationLiteral<BridgedViaRoute> BRIDGED_VIA_ROUTE = new AnnotationLiteral<BridgedViaRoute>()
   {
      private static final long serialVersionUID = 1L;
   };

   @EventRouting
   public Collection<Route> getRoutes(RouteManager routeManager)
   {
      return Arrays.asList(routeManager.createRoute(EGRESS, String.class).addQualifiers(BRIDGED_VIA_COLLECTION).addDestinationJndiName("queue/DLQ"));
   }
   
   @EventRouting
   public Route getRoute(RouteManager routeManager)
   {
      return routeManager.createRoute(EGRESS, String.class).addQualifiers(BRIDGED_VIA_ROUTE).addDestinationJndiName("queue/DLQ");
   }
}
