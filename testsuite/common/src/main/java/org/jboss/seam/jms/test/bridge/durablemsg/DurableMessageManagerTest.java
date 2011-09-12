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
package org.jboss.seam.jms.test.bridge.durablemsg;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.TopicSubscriber;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.DurableMessageManager;
import org.jboss.seam.jms.DurableMessageManagerImpl;
import org.jboss.seam.jms.annotations.Durable;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DurableMessageManagerTest {

    private static final String QUEUE_NAME = "/jms/T5";

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(DurableMessageManagerImpl.class);
    }

    @Inject
    @Durable
    Instance<DurableMessageManager> messageManagerInst;

    private Random random = new Random();

    @Test
    public void testCreateObjectMessage() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        Long l = 9L;
        ObjectMessage om = messageManager.createObjectMessage(l);
        Assert.assertEquals(l, om.getObject());
    }

    @Test
    public void testCreateTextMessage() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        String s = "target";
        TextMessage om = messageManager.createTextMessage(s);
        Assert.assertEquals(s, om.getText());
    }

    @Test
    public void testCreateMapMessage() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        Object prop = "myprop";
        Object valu = "myvalu";
        Map<Object, Object> m = Collections.singletonMap(prop, valu);
        MapMessage om = messageManager.createMapMessage(m);
        String s = om.getObject(prop.toString()).toString();
        Assert.assertTrue(valu.toString().equalsIgnoreCase(s));
    }

    @Test
    public void testSendObjectToDestinations() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        Long l = 9L;
        MessageConsumer mc = messageManager.createMessageConsumer(QUEUE_NAME);
        messageManager.sendObjectToDestinations(l, QUEUE_NAME);
        ObjectMessage om = (ObjectMessage) mc.receive(3000);
        mc.close();
        Assert.assertEquals(l, om.getObject());
    }

    @Test
    public void testSendTextToDestinations() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        String s = "target";
        MessageConsumer mc = messageManager.createMessageConsumer(QUEUE_NAME);
        messageManager.sendTextToDestinations(s, QUEUE_NAME);
        TextMessage om = (TextMessage) mc.receive(3000);
        mc.close();
        Assert.assertEquals(s, om.getText());
    }

    @Test
    public void testSendMapMessageToDestinations() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        Object prop = "myprop";
        Object valu = "myvalu";
        Map<Object, Object> m = Collections.singletonMap(prop, valu);
        MessageConsumer mc = messageManager.createMessageConsumer(QUEUE_NAME);
        messageManager.sendMapToDestinations(m, QUEUE_NAME);
        MapMessage om = (MapMessage) mc.receive(3000);
        mc.close();
        String s = om.getObject(prop.toString()).toString();
        Assert.assertTrue(valu.toString().equalsIgnoreCase(s));
    }

    @Test
    public void testCreateMessageConsumer() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        Assert.assertNotNull(messageManager.createMessageConsumer(QUEUE_NAME));
    }

    @Test
    public void testCreateMessageProducer() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        Assert.assertNotNull(messageManager.createMessageProducer(QUEUE_NAME));
    }

    @Test
    public void testDurableCapabilities() throws JMSException {
        DurableMessageManager messageManager = this.messageManagerInst.get();
        messageManager.login("client1" + random.nextInt());
        TopicSubscriber subscriber = messageManager.createDurableSubscriber(QUEUE_NAME, "listener-1");
        String msgData = "this is my text message";
        messageManager.sendTextToDestinations(msgData, QUEUE_NAME);
        TextMessage tm = (TextMessage) subscriber.receive(3000);
        Assert.assertEquals(msgData, tm.getText());
        messageManager.unsubscribe("listener-1");
        subscriber.close();
    }
}
