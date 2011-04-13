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
package org.jboss.seam.jms;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Qualifier;
import javax.jms.Destination;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.EventRouting;
import org.jboss.seam.jms.annotations.Routing;
import org.jboss.seam.jms.bridge.EgressRoutingObserver;
import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.bridge.RouteImpl;
import org.jboss.seam.jms.bridge.RouteManager;
import org.jboss.seam.jms.bridge.RouteManagerImpl;
import org.jboss.seam.jms.bridge.RouteType;
import org.jboss.seam.jms.impl.wrapper.JmsAnnotatedTypeWrapper;
import org.jboss.seam.solder.core.VersionLoggerUtil;

/**
 * Seam 3 JMS Portable Extension
 * 
 * @author Jordan Ganoff
 */
public class Seam3JmsExtension implements Extension {

    public Seam3JmsExtension() {
        log.info("Creating a new instance of Seam3JmsExtension");
    }
    private static final Logger log = Logger.getLogger(Seam3JmsExtension.class);
    private List<Route> ingressRoutes = new ArrayList<Route>();
    private List<Route> egressRoutes = new ArrayList<Route>();
    private List<EgressRoutingObserver> observerMethods = new ArrayList<EgressRoutingObserver>();
    private Set<AnnotatedType<?>> eventRoutingRegistry = new HashSet<AnnotatedType<?>>();
    private Set<AnnotatedMethod<?>> observerMethodRegistry = new HashSet<AnnotatedMethod<?>>();
    private boolean readyToRoute = false;

    public void buildRoutes(@Observes final AfterBeanDiscovery abd, final BeanManager bm) {
        log.debug("Building JMS Routes.");
        RouteManager routeManager = new RouteManagerImpl();
        for (AnnotatedType<?> at : eventRoutingRegistry) {
            Object instance = null;
            try {
                instance = at.getJavaClass().newInstance();
            } catch (InstantiationException ex) {
                abd.addDefinitionError(ex);
                break;
            } catch (IllegalAccessException ex) {
                abd.addDefinitionError(ex);
                break;
            }
            for(AnnotatedMethod<?> am : at.getMethods()) {
                Object result = null;
                try {
                    result = am.getJavaMember().invoke(instance, routeManager);
                } catch (IllegalAccessException ex) {
                    abd.addDefinitionError(ex);
                } catch (IllegalArgumentException ex) {
                    abd.addDefinitionError(ex);
                } catch (InvocationTargetException ex) {
                    abd.addDefinitionError(ex);
                }
                if (result != null) {
                    if (Collection.class.isAssignableFrom(result.getClass())) {
                        @SuppressWarnings("unchecked")
                        Collection<Route> routes = Collection.class.cast(result);
                        for (Route route : routes) {
                            if (route == null) {
                                log.warn("No routes found for " + am);
                            } else {
                                addRoute(route);
                            }
                        }
                    } else if (Route.class.isAssignableFrom(result.getClass())) {
                        addRoute(Route.class.cast(result));
                    } else {
                        abd.addDefinitionError(new IllegalArgumentException("Unsupported route configuration type: " + result));
                    }
                }
            }
        }
        /* when going through the observer method registry, we pick up cases where we dynamically build routes based on
         * observer interfaces.  An example method of this can be seen in the test case ObserverInterface. */
        for (AnnotatedMethod<?> m : observerMethodRegistry) {
            for (AnnotatedParameter<?> ap : m.getParameters()) {
                log.debug("In method " + m.getJavaMember().getName() + " with param type " + ap.getBaseType());
            }
            Routing routing = null;
            if (m.isAnnotationPresent(Routing.class)) {
                routing = m.getAnnotation(Routing.class);
            } else {
                log.debug("Routing not found on method " + m.getJavaMember().getName());
            }
            RouteType routeType = (routing == null) ? RouteType.BOTH : routing.value();
            Route route = new RouteImpl(routeType);
            boolean isResourced = m.isAnnotationPresent(Resource.class);
            if (isResourced) {
                Resource r = m.getAnnotation(Resource.class);
                log.debug("Loading resource " + r.mappedName());
                route.addDestinationJndiName(r.mappedName());
            }

            /* in this case, each method has one non destination and multiple
             * destinations.  this can take the form of resources or qualified
             * resources.
             */
            try {
                for (AnnotatedParameter<?> ap : m.getParameters()) {
                    if (ap.getBaseType() instanceof Class) {
                        Class<?> clazz = (Class<?>) ap.getBaseType();
                        if (Destination.class.isAssignableFrom(clazz)) {
                            log.debug("Found another type of qualifier.");
                            //route.addDestinationQualifiers(ap.getAnnotations());
                            route.addAnnotatedParameter(ap);
                        } else if (ap.isAnnotationPresent(Observes.class)) {
                            route.setType(ap.getBaseType());
                            route.getQualifiers().addAll(getQualifiersFrom(ap.getAnnotations()));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Exception mapping for method " + m.getJavaMember().getDeclaringClass() + "." + m.getJavaMember().getName() + ", ", e);
            }
            addRoute(route);
        }
        for (Route egress : this.egressRoutes) {
            EgressRoutingObserver ero = new EgressRoutingObserver(egress, this);
            abd.addObserverMethod(ero);
            this.observerMethods.add(ero);
        }
    }

    public <X> void decorateAnnotatedType(@Observes ProcessAnnotatedType<X> pat) {
        /**
         * Flatten all @Annotated that define @JmsDestinations so that they may be injected
         */
        pat.setAnnotatedType(JmsAnnotatedTypeWrapper.decorate(pat.getAnnotatedType()));
    }

    public void setBeanManager(BeanManager beanManager) {
        log.debug("Handling AfterDeploymentValidation, loading active bean manager into all beans.");
        VersionLoggerUtil.logVersionInformation(this.getClass());
        if (!this.readyToRoute) {
            for (EgressRoutingObserver ero : this.observerMethods) {
                log.debug("Setting observer method beanmanager. " + beanManager);
                ero.setBeanManager(beanManager);
            }
            this.readyToRoute = true;
        }
        log.debug("EgressRoutingObservers: " + this.observerMethods);
        log.debug("Ingress routes: " + this.ingressRoutes);
    }

    public boolean isReadyToRoute() {
        return this.readyToRoute;
    }

    /**
     * Generates the observer method registry for all interfaces that have observer methods.
     * @param pat
     */
    public void registerObserverMethods(@Observes ProcessAnnotatedType<?> pat) {
        if (pat.getAnnotatedType().getJavaClass().isInterface()) {
            log.debug("Found a possible interface... " + pat.getAnnotatedType().getJavaClass());
            for (AnnotatedMethod<?> m : pat.getAnnotatedType().getMethods()) {
                this.observerMethodRegistry.add(m);
            }
        } else {
            Set<AnnotatedMethod<?>> sams = new HashSet<AnnotatedMethod<?>>();
            for (AnnotatedMethod<?> m : pat.getAnnotatedType().getMethods()) {
                if (m.isAnnotationPresent(EventRouting.class)) {
                    sams.add(m);
                }
            }
            if(!sams.isEmpty()){
                pat.veto();
                this.eventRoutingRegistry.add(pat.getAnnotatedType());
            }
        }
    }

    private void addRoute(Route route) {
        log.debug("RouteType is: " + route.getType());
        if (route.validate()) {
            if (route.getType() == RouteType.EGRESS) {
                this.egressRoutes.add(route);
            } else if (route.getType() == RouteType.INGRESS) {
                this.ingressRoutes.add(route);
            } else {
                log.debug("Adding both types of routes.");
                this.egressRoutes.add(route);
                this.ingressRoutes.add(route);
            }
        } else {
            log.debugf("Not adding route %s to routes, it was not valid.", route);
        }
    }

    public List<Route> getIngressRoutes() {
        return this.ingressRoutes;
    }
    
    public List<Route> getEgressRoutes() {
    	return this.egressRoutes;
    }
    
    private static Set<Annotation> getQualifiersFrom(Set<Annotation> annotations) {
        Set<Annotation> q = new HashSet<Annotation>();
        log.debug("Annotations in getQualifiersFrom: " + annotations);
        for (Annotation a : annotations) {
            if (a.annotationType().isAnnotationPresent(Qualifier.class)) {
                q.add(a);
            } else {
                log.infof("Skipping annotation %s", a);
            }
        }
        return q;
    }
}
