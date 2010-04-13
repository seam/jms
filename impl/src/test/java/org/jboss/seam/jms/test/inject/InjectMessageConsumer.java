package org.jboss.seam.jms.test.inject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.QueueReceiver;
import javax.jms.TopicSubscriber;

import org.jboss.seam.jms.test.MyQueue;
import org.jboss.seam.jms.test.MyTopic;

@Named
public class InjectMessageConsumer
{

   @Inject
   @MyTopic
   private Instance<TopicSubscriber> ts;

   @Inject
   @MyQueue
   private Instance<QueueReceiver> qr;

   public TopicSubscriber getTs()
   {
      return ts.get();
   }

   public QueueReceiver getQr()
   {
      return qr.get();
   }
}
