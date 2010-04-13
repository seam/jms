package org.jboss.seam.jms.test.inject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.QueueSender;
import javax.jms.TopicPublisher;

import org.jboss.seam.jms.test.MyQueue;
import org.jboss.seam.jms.test.MyTopic;

public class InjectMessageProducer
{
   @Inject
   @MyTopic
   private Instance<TopicPublisher> tp;

   @Inject
   @MyQueue
   private Instance<QueueSender> qs;

   public TopicPublisher getTp()
   {
      return tp.get();
   }

   public QueueSender getQs()
   {
      return qs.get();
   }
}
