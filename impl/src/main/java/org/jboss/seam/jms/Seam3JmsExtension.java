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
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.inject.Qualifier;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDestination;
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
public class Seam3JmsExtension implements Extension
{
    public Seam3JmsExtension() {
        log.info("Creating a new instance of Seam3JmsExtension");
    }
   private static final Logger log = Logger.getLogger(Seam3JmsExtension.class);
   private List<Route> ingressRoutes = new ArrayList<Route>();
   private Set<AnnotatedMethod<?>> eventRoutingRegistry = new HashSet<AnnotatedMethod<?>>();
   private Set<AnnotatedMethod<?>> observerMethodRegistry = new HashSet<AnnotatedMethod<?>>();
   
   public void buildRoutes(@Observes final AfterBeanDiscovery abd, final BeanManager bm)
   {
      /*for (AnnotatedMethod<?> m : eventRoutingRegistry)
      {
         Type beanType = m.getDeclaringType().getBaseType();
         Set<Bean<?>> configBeans = bm.getBeans(beanType);
         for (Bean<?> configBean : configBeans)
         {
            CreationalContext<?> context = bm.createCreationalContext(configBean);
            Object config = null;
            try
            {
               Object bean = bm.getReference(configBean, beanType, context);
               config = m.getJavaMember().invoke(bean);
            } catch (Exception ex)
            {
               abd.addDefinitionError(new IllegalArgumentException("Routing could not be configured from bean " + beanType + ": " + ex.getMessage(), ex));
            }
            log.debug("Building " + Route.class.getSimpleName() + "s from " + beanType);
            if (config != null)
            {
               if (Collection.class.isAssignableFrom(config.getClass()))
               {
                  @SuppressWarnings("unchecked")
                  Collection<Route> routes = Collection.class.cast(config);
                  for (Route route : routes)
                  {
                     if(route == null)
                     {
                        log.warn("No routes found for " + m);
                     }
                     createRoute(abd, bm, route);
                  }
               } else if(Route.class.isAssignableFrom(config.getClass()))
               {
                  createRoute(abd, bm, Route.class.cast(config));
               } else
               {
                  abd.addDefinitionError(new IllegalArgumentException("Unsupported route configuration type: " + config));
               }
            }
         }
      }*/
      /* when going through the observer method registry, we pick up cases where we dynamically build routes based on
       * observer interfaces.  An example method of this can be seen in the test case ObserverInterface. */
      for (AnnotatedMethod<?> m : observerMethodRegistry) {
            //When adding support for SEAMJMS-3, we only work on EGRESS support
            //Ingress to come at a later date.
            for(AnnotatedParameter<?> ap : m.getParameters()) {
                log.debug("In method "+m.getJavaMember().getName()+" with param type "+ap.getBaseType());
            }
            boolean isResourced = m.isAnnotationPresent(Resource.class);
            Set<Destination> d = new HashSet<Destination>();
            Type type = null;
            Set<Annotation> qualifiers = new HashSet<Annotation>();
            if(isResourced) {
                Resource r = m.getAnnotation(Resource.class);
                log.info("Loading resource "+r.mappedName());
                d.add(locateDestination(r.mappedName()));
            }
            /* in this case, each method has one non destination and multiple
             * destinations.  this can take the form of resources or qualified
             * resources.
             */
            try{
                for(AnnotatedParameter<?> ap : m.getParameters()) {
                    if(ap.getBaseType() instanceof Class) {
                        Class<?> clazz = (Class<?>)ap.getBaseType();
                        if(Destination.class.isAssignableFrom(clazz)) {
                            log.info("Found another type of qualifier.");
                            d.add(resolveDestination(ap));
                        } else if (ap.isAnnotationPresent(Observes.class)){
                            type = ap.getBaseType();
                            qualifiers.addAll(getQualifiersFrom(ap.getAnnotations()));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Exception mapping for method "+m.getJavaMember().getDeclaringClass()+"."+m.getJavaMember().getName()+", ",e);
            }

            if(type == null) {
                log.debug("Unable to bind mapping for method "+m.getJavaMember().getDeclaringClass()+"."+m.getJavaMember().getName()+", no Observes identified.");
            } else if(d.isEmpty()) {
                log.debug("Unable to bind mapping for method "+m.getJavaMember().getDeclaringClass()+"."+m.getJavaMember().getName()+", no Destinations found.");
            } else {
                Route egress = new RouteImpl(RouteType.EGRESS,type).addQualifiers(qualifiers)
                        .addDestinations(d);
                this.ingressRoutes.add(egress);
                log.infof("About to add a new observer of %s to %s destination",type,d);
                this.createRoute(abd, bm, egress);
            }
        }
   }
   
   private void createRoute(final AfterBeanDiscovery abd, final BeanManager bm, final Route route)
   {
      switch(route.getType())
      {
         case EGRESS:
            abd.addObserverMethod(new EgressRoutingObserver(bm, route));
            log.debug("Built " + route);
            break;
         default:
            abd.addDefinitionError(new IllegalArgumentException("Unsupported routing type: " + route.getType()));
      }
   }
   
   public <X> void decorateAnnotatedType(@Observes ProcessAnnotatedType<X> pat)
   {
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
            log.info("Found a possible interface... "+pat.getAnnotatedType().getJavaClass());
            for (AnnotatedMethod<?> m : pat.getAnnotatedType().getMethods()) {
                this.observerMethodRegistry.add(m);
            }
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
   private static Destination locateDestination(String jndiName) {
        try {
            InitialContext ic = new InitialContext();
            return (Destination) ic.lookup(jndiName);
        } catch (NamingException e) {
            log.error("Unable to locate resource " + jndiName, e);
            return null;
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

    private static Destination resolveDestination(AnnotatedParameter<?> ap) {
        JmsDestination dest = ap.getAnnotation(JmsDestination.class);
        Destination d = null;
        try{
            InitialContext ic = new InitialContext();
            d = (Destination)ic.lookup(dest.jndiName());
        } catch (NamingException e) {
            log.warnf("Unable to look up object found at %s",dest.jndiName(),e);
        }
        return d;
    }
}
