package org.jboss.seam.jms.test.inject;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.jms.Topic;

import org.jboss.seam.jms.test.MyQueue;
import org.jboss.seam.jms.test.MyTopic;

public class InjectConstructors
{

   private Topic t;
   private Queue q;

   @Inject
   public InjectConstructors(@MyTopic Topic topic, @MyQueue Queue queue)
   {
      t = topic;
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
