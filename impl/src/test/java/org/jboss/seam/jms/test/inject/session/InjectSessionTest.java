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
package org.jboss.seam.jms.test.inject.session;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Session;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.JmsSession;
import org.jboss.seam.jms.annotations.JmsSessionSelector;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InjectSessionTest
{

   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(InjectSessionTest.class);
   }

   @Inject
   private Instance<Session> s;
   
   @Inject
   @JmsSessionSelector(transacted=false, acknowledgementMode=Session.AUTO_ACKNOWLEDGE)
   private Instance<Session> qualified;

   @Inject
   @JmsSession(transacted = false, acknowledgementMode = Session.CLIENT_ACKNOWLEDGE)
   private Instance<Session> configuredSession;

   @Inject
   @EventSession
   private Instance<Session> eventSession;

   @Inject
   SessionSelectorBean selectorBean;
   
   @Test
   public void injectSession()
   {
      Assert.assertNotNull(s.get());
   }

   @Test
   public void injectSession_annotated() throws JMSException
   {
      Session s = configuredSession.get();
      Assert.assertNotNull(s);
      Assert.assertFalse(s.getTransacted());
      Assert.assertEquals(Session.CLIENT_ACKNOWLEDGE, s.getAcknowledgeMode());
   }

   @Test
   public void injectSession_meta_annotated() throws JMSException
   {
      Session s = eventSession.get();
      Assert.assertNotNull(s);
      Assert.assertFalse(s.getTransacted());
      Assert.assertEquals(Session.DUPS_OK_ACKNOWLEDGE, s.getAcknowledgeMode());
   }

   @Test
   public void injectQualifiedSession() throws JMSException
   {
      Session s = qualified.get();
      Assert.assertNotNull(s);
      Assert.assertFalse(s.getTransacted());
      Assert.assertEquals(Session.AUTO_ACKNOWLEDGE, s.getAcknowledgeMode());
   }

   @Test
   public void injectSessionWithLiteral() throws JMSException
   {
      Session session = selectorBean.get();
      Assert.assertNotNull(session);
      Assert.assertFalse(session.getTransacted());
      Assert.assertEquals(Session.DUPS_OK_ACKNOWLEDGE, session.getAcknowledgeMode());
   }
}