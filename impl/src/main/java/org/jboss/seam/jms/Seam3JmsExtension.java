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
package org.jboss.seam.jms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.inject.Qualifier;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Routing;
import org.jboss.seam.jms.bridge.EgressRoutingObserver;
import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.bridge.RouteImpl;
import org.jboss.seam.jms.bridge.RouteType;
import org.jboss.seam.jms.impl.wrapper.JmsAnnotatedTypeWrapper;
import org.jboss.seam.solder.bean.ImmutableInjectionPoint;

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
    private Set<AnnotatedMethod<?>> eventRoutingRegistry = new HashSet<AnnotatedMethod<?>>();
    private Set<AnnotatedMethod<?>> observerMethodRegistry = new HashSet<AnnotatedMethod<?>>();
    private boolean readyToRoute = false;

    public void buildRoutes(@Observes final AfterBeanDiscovery abd, final BeanManager bm) {
        log.info("Building JMS Routes.");
        for (AnnotatedMethod<?> m : eventRoutingRegistry) {
            Type beanType = m.getDeclaringType().getBaseType();
            Set<Bean<?>> configBeans = bm.getBeans(beanType);
            for (Bean<?> configBean : configBeans) {
                CreationalContext<?> context = bm.createCreationalContext(configBean);
                Object config = null;
                try {
                    Object bean = bm.getReference(configBean, beanType, context);
                    config = m.getJavaMember().invoke(bean);
                } catch (Exception ex) {
                    abd.addDefinitionError(new IllegalArgumentException("Routing could not be configured from bean " + beanType + ": " + ex.getMessage(), ex));
                }
                log.debug("Building " + Route.class.getSimpleName() + "s from " + beanType);
                if (config != null) {
                    if (Collection.class.isAssignableFrom(config.getClass())) {
                        @SuppressWarnings("unchecked")
                        Collection<Route> routes = Collection.class.cast(config);
                        for (Route route : routes) {
                            if (route == null) {
                                log.warn("No routes found for " + m);
                            } else {
                                addRoute(route);
                            }
                        }
                    } else if (Route.class.isAssignableFrom(config.getClass())) {
                        addRoute(Route.class.cast(config));
                    } else {
                        abd.addDefinitionError(new IllegalArgumentException("Unsupported route configuration type: " + config));
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
            }
            RouteType routeType = (routing == null) ? RouteType.BOTH : routing.value();
            Route route = new RouteImpl(routeType);
            boolean isResourced = m.isAnnotationPresent(Resource.class);
            if (isResourced) {
                Resource r = m.getAnnotation(Resource.class);
                log.info("Loading resource " + r.mappedName());
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
                            log.info("Found another type of qualifier.");
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
            EgressRoutingObserver ero = new EgressRoutingObserver(egress,this);
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

    /**
     * Register method producers of {@link org.jboss.seam.jms.bridge.Route}s.
     */
    public void registerRouteCollectionProducer(@Observes ProcessProducer<?, ? extends Collection<Route>> pp) {
        registerRouteProducer(pp.getAnnotatedMember());
    }

    public void handleAfterDeploymentValidation(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        log.debug("Handling AfterDeploymentValidation, loading active bean manager into all beans.");
        if(!this.readyToRoute) {
            for(EgressRoutingObserver ero : this.observerMethods) {
                log.debug("Setting observer method beanmanager. "+beanManager);
                ero.setBeanManager(beanManager);
            }
            this.readyToRoute = true;
        }
    }

    public boolean isReadyToRoute() {
        return this.readyToRoute;
    }

    /**
     * Register method producers of a single {@link org.jboss.seam.jms.bridge.Route}.
     */
    public void registerRouteProducer(@Observes ProcessProducer<?, ? extends Route> pp) {
        registerRouteProducer(pp.getAnnotatedMember());
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
        }
    }
    private void addRoute(Route route) {
        if(route.validate()) {
            if(route.getType() == RouteType.EGRESS) {
                this.egressRoutes.add(route);
            } else if(route.getType() == RouteType.INGRESS) {
                this.ingressRoutes.add(route);
            } else {
                this.egressRoutes.add(route);
                this.ingressRoutes.add(route);
            }
        } else {
            log.debugf("Not adding route %s to routes, it was not valid.");
        }
    }
    public List<Route> getIngressRoutes() {
        return this.ingressRoutes;
    }

    private boolean registerRouteProducer(AnnotatedMember<?> m) {
        if (AnnotatedMethod.class.isAssignableFrom(m.getClass())) {
            eventRoutingRegistry.add((AnnotatedMethod<?>) m);
            return true;
        } else {
            log.warnf("Producer of routes not registered. Must declare a method producer. (%s)", m);
            return false;
        }
    }

    private static Set<Annotation> getQualifiersFrom(Set<Annotation> annotations) {
        Set<Annotation> q = new HashSet<Annotation>();
        for (Annotation a : annotations) {
            if (a.annotationType().isAnnotationPresent(Qualifier.class)) {
                q.add(a);
            }
        }
        return q;
    }
}
