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

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

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
   
   public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm)
   {
      Set<Bean<?>> configuration = bm.getBeans(JmsForwarding.class);
      
      if(configuration == null || configuration.isEmpty())
      {
         log.info("No {} registered.  Event forwarding disabled.", JmsForwarding.class.getSimpleName());
      } else
      {
         for(Bean<?> c : configuration)
         {
            log.info("Creating {} for configuration {}", BridgedObserver.class.getSimpleName(), c);
            CreationalContext<?> context = bm.createCreationalContext(c);
            // TODO Verify configuration for correctness (e.g. getQualifiers() must contain only @Qualifier annotations)
            JmsForwarding config = JmsForwarding.class.cast(bm.getReference(c, JmsForwarding.class, context));
            BridgedObserver b = new BridgedObserver(bm, config);
            abd.addObserverMethod(b);
         }
      }
   }
   
   public <X> void decorateAnnotatedType(@Observes ProcessAnnotatedType<X> pat)
   {
      /**
       * Flatten all @Annotated that define @JmsDestinations so that they may be injected  
       */
      pat.setAnnotatedType(JmsAnnotatedTypeWrapper.decorate(pat.getAnnotatedType()));
   }
}
