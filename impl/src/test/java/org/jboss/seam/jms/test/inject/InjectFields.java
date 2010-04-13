package org.jboss.seam.jms.test.inject;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Queue;
import javax.jms.Topic;

import org.jboss.seam.jms.test.MyQueue;
import org.jboss.seam.jms.test.MyTopic;

@Named
public class InjectFields
{
   @Inject
   @MyTopic
   private Instance<Topic> t;

   @Inject
   @MyQueue
   private Instance<Queue> q;

   @Inject
   @Any
   private Instance<Topic> anyTopic;

   @Inject
   @Any
   private Instance<Queue> anyQueue;

   @SuppressWarnings("serial")
   class MyTopicLiteral extends AnnotationLiteral<MyTopic>
   {
   }

   @SuppressWarnings("serial")
   class MyQueueLiteral extends AnnotationLiteral<MyQueue>
   {
   }

   public Topic getT()
   {
      return t.get();
   }

   public Queue getQ()
   {
      return q.get();
   }

   public Topic getSelectTopic()
   {
      return getAnyTopic(new MyTopicLiteral());
   }

   public Queue getSelectQueue()
   {
      return getAnyQueue(new MyQueueLiteral());
   }

   private <T extends Annotation> Topic getAnyTopic(T qualifier)
   {
      return anyTopic.select(qualifier).get();
   }

   private <Q extends Annotation> Queue getAnyQueue(Q qualifier)
   {
      return anyQueue.select(qualifier).get();
   }
}
