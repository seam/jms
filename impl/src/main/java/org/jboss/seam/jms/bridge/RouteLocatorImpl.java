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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.seam.jms.Seam3JmsExtension;
import org.jboss.seam.jms.annotations.Routing;

public class RouteLocatorImpl implements RouteLocator {
    @Inject
    Seam3JmsExtension extension;
    private Map<String, Route> routeMap = new HashMap<String, Route>();

    @PostConstruct
    public void loadRoutes() {
        for (Route ingress : this.extension.getIngressRoutes())
            routeMap.put(ingress.getId(), ingress);
        for (Route egress : this.extension.getEgressRoutes())
            routeMap.put(egress.getId(), egress);
    }

    @Override
    public Route findById(String id) {
        return routeMap.get(id);
    }

    @Produces
    @Routing(RouteType.INGRESS)
    public List<Route> produceIngressRoutes() {
        return extension.getIngressRoutes();
    }

    @Produces
    @Routing(RouteType.EGRESS)
    public List<Route> produceEgressRoutes() {
        return extension.getEgressRoutes();
    }
}
