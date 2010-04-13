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
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InjectMessageProducerConsumerTest
{
   @Deployment
   public static JavaArchive createDeployment()
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
