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
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.jboss.seam.jms.annotations.JmsSession;

public @RequestScoped
class SessionProducer
{
   @Produces
   public Session getGenericSession(InjectionPoint ip, Connection c) throws JMSException
   {
      return c.createSession(false, Session.AUTO_ACKNOWLEDGE);
   }

   @Produces
   @JmsSession
   public Session getSession(InjectionPoint ip, Connection c) throws JMSException
   {
      JmsSession s = getExpectedQualifier(JmsSession.class, ip.getQualifiers());
      return c.createSession(s.transacted(), s.acknowledgementType());
   }

   public void closeSession(@Disposes Session s) throws JMSException
   {
      s.close();
   }
}
