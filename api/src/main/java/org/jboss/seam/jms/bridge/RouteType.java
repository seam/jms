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

/**
 * All valid {@link Route} types.
 * 
 * @author Jordan Ganoff
 * 
 */
public enum RouteType
{
   /**
    * Inbound route. Any route defined as INGRESS will listen for objects
    * delivered to the registered destinations and fire events for all that
    * match the configuration as defined by the route.
    */
   INGRESS,

   /**
    * Outbound route. Any route defined as EGRESS will forward CDI events that
    * match the configuration as defined by the route to the registered
    * destinations.
    */
   EGRESS,

   /**
    * This shouldn't be used with Routes developed, but instead with interfaces.
    * Represents a route that will be generated that has both Egress and Ingress
    * capabilities.	
    */
   BOTH;
}
