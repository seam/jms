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
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author johnament
 */
@RunWith(Arquillian.class)
public class PubSubTest {
    @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(PubSubTest.class);
   }
    @Inject @JmsDestination(jndiName="jms/T2") Topic t2;
    @Inject Connection conn;
    private Logger logger = Logger.getLogger(PubSubTest.class);
    @Test
    public void testPubAndSub() throws JMSException {
        conn.start();
        Session session = conn.createSession(true, Session.DUPS_OK_ACKNOWLEDGE);
        MessageProducer mp = session.createProducer(t2);
        MessageConsumer mc = session.createConsumer(t2);
        TextMessage m = (TextMessage)session.createTextMessage("hello");
        boolean observed = false;
        mp.send(m);
        Message out;
        while((out = mc.receive(50000)) != null) {
            if(out instanceof TextMessage) {
                TextMessage tm = (TextMessage)out;
                logger.info("The data received is: "+tm.getText());
                Assert.assertEquals(m.getText(), tm.getText());
                observed = true;
            } else {
                //Assert.assertTrue(false);
            }
        }
        Assert.assertTrue(observed);
    }
}
