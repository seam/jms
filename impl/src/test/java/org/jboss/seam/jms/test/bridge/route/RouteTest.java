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
package org.jboss.seam.jms.test.bridge.route;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.QueueReceiver;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Ignore
public class RouteTest
{
   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(RouteTest.class);
   }
   
   @Inject Connection c;
   @Inject @JmsDestination(jndiName="queue/DLQ") QueueReceiver qr;
   @Inject @BridgedViaCollection Event<String> event_viaCollectionRouteConfig;
   @Inject @BridgedViaRoute Event<String> event_viaSingleRouteConfig;
   @Inject Event<String> plainEvent;

   private void clear(QueueReceiver qr) throws JMSException {
      while (qr.receiveNoWait() != null);
   }

   @Test
   public void forwardSimpleEvent() throws JMSException
   {
      String expected = "'configured via Collection<Route>'";
      c.start();
      clear(qr);
      event_viaCollectionRouteConfig.fire(expected);
      Message m = qr.receive(3000);
      qr.close();
      Assert.assertTrue(m != null);
      Assert.assertTrue(m instanceof ObjectMessage);
      Assert.assertEquals(expected, ((ObjectMessage) m).getObject());
   }

   @Test
   public void noMatchingRoutes() throws JMSException
   {
      String expected = "'no matching route'";
      c.start();
      clear(qr);
      plainEvent.fire(expected);
      Message m = qr.receive(3000);
      qr.close();
      Assert.assertNull("Unexpectedly received a message", m);
   }

   @Test
   public void forwardSimpleEvent_via_single_route_config() throws JMSException {
      String expected = "'configured via Route'";
      c.start();
      clear(qr);
      event_viaSingleRouteConfig.fire(expected);
      Message m = qr.receive(3000);
      qr.close();
      Assert.assertTrue(m != null);
      Assert.assertTrue(m instanceof ObjectMessage);
      Assert.assertEquals(expected, ((ObjectMessage) m).getObject());
   }
}
