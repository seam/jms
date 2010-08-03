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
