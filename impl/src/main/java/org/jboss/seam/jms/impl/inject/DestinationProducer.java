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
package org.jboss.seam.jms.impl.inject;

import static org.jboss.seam.jms.impl.inject.InjectionUtil.getExpectedQualifier;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Module;

public @RequestScoped class DestinationProducer
{

   @Produces
   @JmsDestination
   public Topic getTopic(InjectionPoint ip, @Module Context c) throws NamingException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      return (Topic) c.lookup(d.jndiName());
   }
   
   @Produces
   @JmsDestination
   public Queue getQueue(InjectionPoint ip, @Module Context c) throws NamingException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      return (Queue) c.lookup(d.jndiName());
   }
}
