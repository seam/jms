package org.jboss.seam.jms.test.inject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Queue;
import javax.jms.Topic;

import org.jboss.seam.jms.test.MyQueue;
import org.jboss.seam.jms.test.MyTopic;

@Named
public class InjectMethods
{

   private Topic t;
   private Queue q;

   @Inject
   public void initTopic(@MyTopic Topic topic)
   {
      t = topic;
   }

   @Inject
   public void initQueue(@MyQueue Queue queue)
   {
      q = queue;
   }

   public Topic getT()
   {
      return t;
   }

   public Queue getQ()
   {
      return q;
   }
}
