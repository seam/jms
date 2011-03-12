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
package org.jboss.seam.jms.test.inject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InjectMessageProducerConsumerTest
{
   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(InjectMessageProducerConsumerTest.class);
   }

   @Inject
   private Instance<InjectMessageConsumer> mc;

   @Inject
   private Instance<InjectMessageProducer> mp;

   @Test
   public void injectTopicPublisher()
   {
      InjectMessageProducer imp = mp.get();
      Assert.assertNotNull(imp);
      TopicPublisher tp = imp.getTp();
      Assert.assertNotNull(tp);
   }

   @Test
   public void injectTopicSubscriber()
   {
      InjectMessageConsumer imc = mc.get();
      Assert.assertNotNull(imc);
      TopicSubscriber tp = imc.getTs();
      Assert.assertNotNull(tp);
   }

   @Test
   public void injectQueueSender()
   {
      InjectMessageProducer imp = mp.get();
      Assert.assertNotNull(imp);
      QueueSender qs = imp.getQs();
      Assert.assertNotNull(qs);
   }

   @Test
   public void injectQueueReceiver()
   {
      InjectMessageConsumer imc = mc.get();
      Assert.assertNotNull(imc);
      QueueReceiver qr = imc.getQr();
      Assert.assertNotNull(qr);
   }
}
