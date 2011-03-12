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
