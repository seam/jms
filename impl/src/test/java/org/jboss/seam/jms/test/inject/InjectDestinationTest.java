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

import javax.inject.Inject;
import javax.jms.JMSException;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class) 
public class InjectDestinationTest
{
   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(InjectDestinationTest.class);
   }

   // Use a bean to handle the injection since there is no ProcessAnnotatedType
   // event being fired when test cases are injected
   // @Inject @MyTopic Topic t;
   @Inject
   private InjectFields f;

   @Inject
   private InjectMethods m;

   @Inject
   private InjectConstructors c;

   @Test
   public void injectField_topic() throws JMSException
   {
      Assert.assertNotNull(f.getT());
      Assert.assertEquals("T", f.getT().getTopicName());
   }

   @Test
   public void injectField_queue() throws JMSException
   {
      Assert.assertNotNull(f.getQ());
      Assert.assertEquals("Q", f.getQ().getQueueName());
   }

   @Ignore
   @Test
   public void injectField_topic_select_qualifier() throws JMSException
   {
      // TODO Implement better dynamic injection
      // Not currently supported with the way we handle replacing
      // JmsDestination qualifiers during ProcessAnnotatedType
      Assert.assertNotNull(f.getSelectTopic());
      Assert.assertEquals("T", f.getSelectTopic().getTopicName());
   }

   @Ignore
   @Test
   public void injectField_queue_select_qualifier() throws JMSException
   {
      // TODO Implement better dynamic injection
      // Not currently supported with the way we handle replacing
      // JmsDestination qualifiers during ProcessAnnotatedType
      Assert.assertNotNull(f.getSelectQueue());
      Assert.assertEquals("Q", f.getSelectQueue().getQueueName());
   }

   @Test
   public void injectMethod_topic() throws JMSException
   {
      Assert.assertNotNull(m.getT());
      Assert.assertEquals("T", m.getT().getTopicName());
   }

   @Test
   public void injectMethod_queue() throws JMSException
   {
      Assert.assertNotNull(m.getQ());
      Assert.assertEquals("Q", m.getQ().getQueueName());
   }

   @Test
   public void injectConstructor_topic() throws JMSException
   {
      Assert.assertNotNull(c.getT());
      Assert.assertEquals("T", c.getT().getTopicName());
   }

   @Test
   public void injectConstructor_queue() throws JMSException
   {
      Assert.assertNotNull(c.getQ());
      Assert.assertEquals("Q", c.getQ().getQueueName());
   }
}
