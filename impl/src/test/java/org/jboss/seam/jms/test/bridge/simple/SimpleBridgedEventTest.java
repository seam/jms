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

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TopicSubscriber;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SimpleBridgedEventTest
{
   @Deployment
   public static JavaArchive createDeployment()
   {
      return Util.createDeployment(SimpleBridgedEventTest.class);
   }
   
   @Inject Connection c;
   @Inject Session s;
   @Inject @JmsDestination(jndiName="jms/T") TopicSubscriber ts;
   @Inject @Bridged Event<String> event;
   
   @Test
   public void forwardSimpleEvent() throws JMSException
   {
      String expected = "test";
      c.start();
      event.fire(expected);
      Message m = ts.receive(3000);
      Assert.assertTrue(m != null);
      Assert.assertTrue(m instanceof ObjectMessage);
      Assert.assertEquals(expected, ((ObjectMessage) m).getObject());
   }
}
