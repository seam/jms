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

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.jboss.seam.jms.annotations.Module;

/**
 * <p>Provides a producer of an unqualified (i.e., @Default) application-scoped JMS {@link Connection} object.</p>
 *
 * <p>According to the documentation, a {@link Connection} is a relatively heavyweight object because its creation
 * involves setting up authentication and communication. Most clients will do all their messaging with a single
 * connection. Only more advanced applications may use several connections, though it's considered atypical.
 * Therefore, use of the application-scope is justified.</p>
 *
 * @author Jordan Ganoff
 */
public class ConnectionProducer
{
   @Produces
   @ApplicationScoped
   @Module
   @Resource(mappedName = "ConnectionFactory")
   private ConnectionFactory cf;

   @Produces
   @ApplicationScoped
   public Connection getConnection() throws Exception
   {
      return cf.createConnection();
   }

   public void closeConnection(@Disposes Connection c) throws JMSException
   {
      c.close();
   }
}
