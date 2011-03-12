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

import java.lang.reflect.Type;

/**
 * Facilitates bridging of events between CDI and a messaging system.
 * 
 * @author Jordan Ganoff
 * 
 */
public interface EventBridge
{
   /**
    * Creates a routing for the provided payload (event) type.
    * 
    * @param type Type or direction of routing.
    * @param payloadType Payload type to route. This is the event type we wish
    *           to observe events for.
    * @return 
    */
   public Route createRoute(RouteType type, Type payloadType);
}
