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
package org.jboss.seam.jms.tools.test;

import javax.annotation.Resource;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.Closeable;
import org.jboss.seam.jms.annotations.Module;
import org.jboss.seam.jms.impl.inject.ConnectionProducer;
import org.jboss.seam.jms.test.Util;
import org.jboss.seam.jms.tools.JMSResourceManager;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author johnament
 */
@RunWith(Arquillian.class)
public class JMSResourceManagerTest {

   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(Closeable.class,JMSResourceManager.class,ConnectionProducer.class,JMSResourceManagerTest.class);
   }

   @Inject @Closeable Event<Connection> connectionHandler;
   @Inject @Module ConnectionFactory connectionFactory;

   @Test
   public void testClosingConnection() throws JMSException {
        Connection conn = connectionFactory.createConnection();
        connectionHandler.fire(conn);
   }
}