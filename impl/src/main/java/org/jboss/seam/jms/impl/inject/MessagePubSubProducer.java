package org.jboss.seam.jms.impl.inject;

import static org.jboss.seam.jms.impl.inject.InjectionUtil.getExpectedQualifier;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.jboss.seam.jms.annotations.JmsDestination;

public @RequestScoped
class MessagePubSubProducer
{
   @Inject
   @Any
   Instance<Topic> anyTopic;

   @Inject
   @Any
   Instance<Queue> anyQueue;

   @Produces
   @JmsDestination
   public TopicPublisher createTopicProducer(InjectionPoint ip, Session s) throws JMSException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      Topic t = anyTopic.select(d).get();
      return TopicPublisher.class.cast(s.createProducer(t));
   }

   @Produces
   @JmsDestination
   public TopicSubscriber createTopicSubscriber(InjectionPoint ip, Session s) throws JMSException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      Topic t = anyTopic.select(d).get();
      return TopicSubscriber.class.cast(s.createConsumer(t));
   }

   @Produces
   @JmsDestination
   public QueueSender createQueueSender(InjectionPoint ip, Session s) throws JMSException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      Queue q = anyQueue.select(d).get();
      return QueueSender.class.cast(s.createProducer(q));
   }

   @Produces
   @JmsDestination
   public QueueReceiver createQueueReceiver(InjectionPoint ip, Session s) throws JMSException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      Queue q = anyQueue.select(d).get();
      return QueueReceiver.class.cast(s.createConsumer(q));
   }
}
