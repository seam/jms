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
package org.jboss.seam.jms.test.transmit;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.seam.jms.test.inject.InjectMessageConsumer;
import org.jboss.seam.jms.test.inject.InjectMessageProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Ignore
public class SimpleTransmitMessageTest
{
   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(SimpleTransmitMessageTest.class, InjectMessageConsumer.class);
   }

   @Inject
   private Connection c;
   
   @Inject
   private Session s;
  
   @Inject
   private Instance<InjectMessageConsumer> imc;

   @Inject
   private Instance<InjectMessageProducer> imp;

   @Test
   public void sendMessage_topic() throws JMSException
   {
      sendMessage(imp.get().getTp(), imc.get().getTs());
   }
   
   @Test
   public void sendMessage_queue() throws JMSException
   {
      sendMessage(imp.get().getQs(), imc.get().getQr());
   }
   
   private void sendMessage(MessageProducer mp, MessageConsumer mc) throws JMSException
   {
      String expected = "test";
      Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Message m = s.createTextMessage(expected);
      c.start();
      try
      {
         mp.send(m);
         Message received = mc.receive(3000);
         Assert.assertNotNull(received);
         Assert.assertTrue(received instanceof TextMessage);
         TextMessage tm = TextMessage.class.cast(received);
         Assert.assertEquals(expected, tm.getText());
      } finally
      {
         c.stop();
      }
   }
}
