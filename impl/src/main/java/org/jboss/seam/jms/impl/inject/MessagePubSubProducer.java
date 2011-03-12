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
package org.jboss.seam.jms.impl.inject;

import static org.jboss.seam.jms.impl.inject.InjectionUtil.getExpectedQualifier;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
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

public
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

   public void disposeTopicProducer(@Disposes @Any TopicPublisher tp) throws JMSException {
       tp.close();
   }

   @Produces
   @JmsDestination
   public TopicSubscriber createTopicSubscriber(InjectionPoint ip, Session s) throws JMSException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      Topic t = anyTopic.select(d).get();
      return TopicSubscriber.class.cast(s.createConsumer(t));
   }

   public void disposesTopicSubscriber(@Disposes @Any TopicSubscriber ts) throws JMSException {
       ts.close();
   }

   @Produces
   @JmsDestination
   public QueueSender createQueueSender(InjectionPoint ip, Session s) throws JMSException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      Queue q = anyQueue.select(d).get();
      return QueueSender.class.cast(s.createProducer(q));
   }

   public void disposesQueueSender(@Disposes @Any QueueSender qs) throws JMSException {
       qs.close();
   }

   @Produces
   @JmsDestination
   public QueueReceiver createQueueReceiver(InjectionPoint ip, Session s) throws JMSException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      Queue q = anyQueue.select(d).get();
      return QueueReceiver.class.cast(s.createConsumer(q));
   }

   public void disposesQueueReceiver(@Disposes @Any QueueReceiver qr) throws JMSException {
       qr.close();
   }
}
