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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author johnament
 */
@RunWith(Arquillian.class)
public class PubSubTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(PubSubTest.class);
    }

    @Inject
    @JmsDestination(jndiName = "jms/T2")
    Topic t2;
    @Inject
    Connection conn;
    private Logger logger = Logger.getLogger(PubSubTest.class);

    @Test
    public void testPubAndSub() throws JMSException {
        conn.start();
        Session session = conn.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        MessageProducer mp = session.createProducer(t2);
        MessageConsumer mc = session.createConsumer(t2);
        TextMessage m = (TextMessage) session.createTextMessage("hello");
        boolean observed = false;
        mp.send(m);
        Message out;
        while ((out = mc.receive(1000)) != null) {
            if (out instanceof TextMessage) {
                TextMessage tm = (TextMessage) out;
                logger.info("The data received is: " + tm.getText());
                Assert.assertEquals(m.getText(), tm.getText());
                observed = true;
            } else {
                //Assert.assertTrue(false);
            }
        }
        Assert.assertTrue(observed);
    }
}
