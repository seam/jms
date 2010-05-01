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
