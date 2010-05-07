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
package org.jboss.seam.jms.test.bridge.simple;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Destination;
import javax.jms.Topic;

import org.jboss.seam.jms.JmsForwarding;
import org.jboss.seam.jms.annotations.JmsDestination;

@Named
@ApplicationScoped
public class MyForwarding implements JmsForwarding
{
   private static final Set<Annotation> BRIDGED = Collections.<Annotation> singleton(new AnnotationLiteral<Bridged>()
   {
      private static final long serialVersionUID = 1L;
   });

   // Use Instance<?> here to get around problem of topic not being deployed before Weld processes 
   // deployment and tries to inject topics
   @Inject
   @JmsDestination(jndiName="jms/T")
   private Instance<Topic> t;
   
   public Set<? extends Destination> getDestinations()
   {
      return Collections.singleton(t.get());
   }

   public Type getEventType()
   {
      return Object.class;
   }

   public Set<Annotation> getQualifiers()
   {
      return BRIDGED;
   }

}
