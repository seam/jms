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

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.jms.JMSException;
import javax.servlet.ServletContext;

/**
 * RouteBuilder is a start up component responsible for loading the finalized
 * BeanManager into the Seam3JmsExtension and then loading all destinations
 * that will be used by the ingress routes.
 * <p/>
 * Loading the BeanManager into the Seam3JmsExtension has the result of doing
 * the same thing to the egress routes.
 * <p/>
 * Implementations of RouteBuilder should be singleton, and defines start up
 * capabilities in handleStartup (servlet containers) and init.
 *
 * @author johnament
 */
public interface RouteBuilder extends Serializable {
    public void handleStartup(@Observes ServletContext servletContext);

    @PostConstruct
    public void init() throws JMSException;
    //public void registerDurableIngressRoute(Route route, String clientId);
    //public void unregisterRoute(String clientId);
}
