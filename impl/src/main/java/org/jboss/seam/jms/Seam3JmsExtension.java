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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.seam.jms.bridge.EgressRoutingObserver;
import org.jboss.seam.jms.bridge.EventRouting;
import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.impl.wrapper.JmsAnnotatedTypeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Seam 3 JMS Portable Extension
 * 
 * @author Jordan Ganoff
 */
public class Seam3JmsExtension implements Extension
{
   private static final Logger log = LoggerFactory.getLogger(Seam3JmsExtension.class);
   
   private Set<AnnotatedMethod<?>> eventRoutingRegistry = new HashSet<AnnotatedMethod<?>>();
   
   public void buildRoutes(@Observes final AfterBeanDiscovery abd, final BeanManager bm)
   {
      for (AnnotatedMethod<?> m : eventRoutingRegistry)
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
               abd.addDefinitionError(new IllegalArgumentException(EventRouting.class.getSimpleName() + " could not be loaded from bean " + beanType + ": " + ex.getMessage(), ex));
            }
            log.debug("Building " + Route.class.getSimpleName() + "s from " + beanType);
            if (config != null)
            {
               if (Collection.class.isAssignableFrom(config.getClass()))
               {
                  Collection<?> routes = Collection.class.cast(config);
                  for (Object route : routes)
                  {
                     if(route == null || !Route.class.isAssignableFrom(route.getClass()))
                     {
                        abd.addDefinitionError(new IllegalArgumentException("Non-" + Route.class.getSimpleName() + " found when loading " + EventRouting.class.getSimpleName() + " from " + beanType + ": " + route));
                     }
                     createRoute(abd, bm, (Route) route);
                  }
               } else if(Route.class.isAssignableFrom(config.getClass()))
               {
                  createRoute(abd, bm, Route.class.cast(config));
               } else
               {
                  abd.addDefinitionError(new IllegalArgumentException(EventRouting.class + " methods must return a " + Collection.class + "<? extends " + Route.class + "> or " + Route.class + " directly."));
               }
            }
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
   
   public void registerEventRouting(@Observes ProcessAnnotatedType<?> pat)
   {
      for(AnnotatedMethod<?> m : pat.getAnnotatedType().getMethods())
      {
         if(m.isAnnotationPresent(EventRouting.class))
         {
            eventRoutingRegistry.add(m);
         }
      }
   }
}
