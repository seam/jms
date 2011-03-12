/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
