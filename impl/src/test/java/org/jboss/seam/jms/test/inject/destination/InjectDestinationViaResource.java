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
package org.jboss.seam.jms.test.inject.destination;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InjectDestinationViaResource 
{
   @Qualifier
   @Retention(RUNTIME)
   public @interface Q {}

   @Qualifier
   @Retention(RUNTIME)
   public @interface T {}
   
   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(InjectDestinationViaResource.class);
   }
   
   @Inject @T Instance<Topic> t;
   @Inject @Q Instance<Queue> q;
   
   @Test
   public void injectTopic() throws JMSException
   {
      Topic topic = t.get();
      Assert.assertNotNull(topic);
      Assert.assertEquals("T", topic.getTopicName());
   }
   
   @Test
   public void injectQueue() throws JMSException
   {
      Queue queue = q.get();
      Assert.assertNotNull(queue);
      Assert.assertNotNull("Q", queue.getQueueName());
   }
}
