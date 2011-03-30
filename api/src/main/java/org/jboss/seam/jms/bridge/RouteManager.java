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
 * The RouteManager is responsible for instantiating new routes.
 *
 * @author johnament
 */
public interface RouteManager {
    /**
     * Creates a new instance of a Route based on given {@link RouteType} and
     * payload type
     *
     * @param type The RouteType to create.
     * @param payloadType The type of payload to use.
     * @return a new Route instance.
     */
    public Route createRoute(RouteType type, Type payloadType);
}
