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
package org.jboss.seam.jms.test.bridge.msg;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.MessageManagerImpl;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.test.DeploymentFactory;
import org.jboss.seam.jms.test.builder.topic.TopicTestListener;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MessageManagerImplTest {
    private static final String QUEUE_NAME = "/jms/T5";
    
    @Inject @JmsDestination(jndiName="jms/T5") Topic t5;
    @Inject @JmsDestination(jndiName="jms/QA") Queue qa;
    @Inject @JmsDestination(jndiName="jms/QB") Queue qb;

    @Deployment
    public static Archive<?> createDeployment() {
        return DeploymentFactory.createDeployment(MessageManagerImpl.class).addClass(TopicTestListener.class);
    }

    @Inject
    MessageManagerImpl messageManager;

    @Test
    public void testCreateObjectMessage() throws JMSException {
        Long l = 9L;
        ObjectMessage om = messageManager.createObjectMessage(l);
        Assert.assertEquals(l, om.getObject());
    }

    @Test
    public void testCreateTextMessage() throws JMSException {
        String s = "target";
        TextMessage om = messageManager.createTextMessage(s);
        Assert.assertEquals(s, om.getText());
    }

    @Test
    public void testCreateMapMessage() throws JMSException {
        Object prop = "myprop";
        Object valu = "myvalu";
        Map<Object, Object> m = Collections.singletonMap(prop, valu);
        MapMessage om = messageManager.createMapMessage(m);
        String s = om.getObject(prop.toString()).toString();
        Assert.assertTrue(valu.toString().equalsIgnoreCase(s));
    }

    @Test
    public void testSendObjectToDestinations() throws JMSException {
        Long l = 9L;
        MessageConsumer mc = messageManager.createMessageConsumer(QUEUE_NAME);
        messageManager.sendObjectToDestinations(l, QUEUE_NAME);
        ObjectMessage om = (ObjectMessage) mc.receive(3000);
        mc.close();
        Assert.assertEquals(l, om.getObject());
    }
    
    @Test
    public void testSendObjectToDestination() throws JMSException {
        Long l = 9L;
        MessageConsumer mc = messageManager.createMessageConsumer(qa);
        messageManager.sendObjectToDestinations(l, qa);
        ObjectMessage om = (ObjectMessage) mc.receive(3000);
        mc.close();
        Assert.assertEquals(l, om.getObject());
    }

    @Test
    public void testSendTextToDestinations() throws JMSException {
        String s = "target";
        MessageConsumer mc = messageManager.createMessageConsumer(QUEUE_NAME);
        messageManager.sendTextToDestinations(s, QUEUE_NAME);
        TextMessage om = (TextMessage) mc.receive(3000);
        mc.close();
        Assert.assertEquals(s, om.getText());
    }
    
    @Test
    public void testSendTextToDestination() throws JMSException {
        String s = "target";
        MessageConsumer mc = messageManager.createMessageConsumer(qa);
        messageManager.sendTextToDestinations(s, qa);
        TextMessage om = (TextMessage) mc.receive(3000);
        mc.close();
        Assert.assertEquals(s, om.getText());
    }
    
    @Test
    public void testCreateMessageConsumerSelector() throws JMSException {
    	TopicTestListener ttl = new TopicTestListener();
    	MessageProducer mp = messageManager.createMessageProducer("jms/QC");
    	TextMessage tm = messageManager.createTextMessage("my data");
    	//tm.setStringProperty("mypromp", "value");
    	mp.send(tm);
    	DeploymentFactory.pause(3000);
    	MessageConsumer mc = messageManager.createMessageConsumer("jms/QC");//, "myprop = 'value'");
    	Message m = mc.receive(3000);
    	Assert.assertNotNull(m);
    }

    @Test
    public void testSendMapMessageToDestinations() throws JMSException {
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
    public void testSendMapMessageToDestination() throws JMSException {
        Object prop = "myprop";
        Object valu = "myvalu";
        Map<Object, Object> m = Collections.singletonMap(prop, valu);
        MessageConsumer mc = messageManager.createMessageConsumer(t5);
        messageManager.sendMapToDestinations(m, t5);
        MapMessage om = (MapMessage) mc.receive(3000);
        mc.close();
        String s = om.getObject(prop.toString()).toString();
        Assert.assertTrue(valu.toString().equalsIgnoreCase(s));
    }

    @Test
    public void testCreateMessageConsumer() throws JMSException {
        Assert.assertNotNull(this.messageManager.createMessageConsumer(QUEUE_NAME));
    }

    @Test
    public void testCreateMessageProducer() throws JMSException {
        Assert.assertNotNull(this.messageManager.createMessageProducer(QUEUE_NAME));
    }
    
    @Test
    public void testCreateQueueSender() throws JMSException {
        Assert.assertNotNull(this.messageManager.createQueueSender("jms/QA"));
    }
    
    @Test
    public void testCreateTopicPublisher() throws JMSException {
        Assert.assertNotNull(this.messageManager.createTopicPublisher("jms/T5"));
    }

    @Test
    public void testCreateQueueReceiver() throws JMSException {
        Assert.assertNotNull(this.messageManager.createQueueReceiver("jms/QA", new javax.jms.MessageListener() {
			@Override
			public void onMessage(Message arg0) {
			} }
        ));
    }
    
    @Test
    public void testCreateTopicSubscriber() throws JMSException {
        Assert.assertNotNull(this.messageManager.createQueueReceiver("jms/T5", new javax.jms.MessageListener() {
			@Override
			public void onMessage(Message arg0) {
			} }
        ));
    }
    
    @Test
    public void testCreateQueueBuilder() {
    	Assert.assertNotNull(this.messageManager.createQueueBuilder());
    }
    
    @Test
    public void testCreateTopicBuilder() {
    	Assert.assertNotNull(this.messageManager.createTopicBuilder());
    }
    
    @Test
    public void testGetSession() {
    	Assert.assertNotNull(this.messageManager.getSession());
    }
    
    @Test
    public void testClose() {
    }
    
    @Test
    public void testCreateBytesMessage() throws JMSException {
    	String data = "this is my message";
    	byte[] bytes = data.getBytes();
    	BytesMessage bm = this.messageManager.createBytesMessage(bytes);
    	Assert.assertNotNull(bm);
    }
    
    @Test
    public void testSendBytesToDestinations() throws JMSException {
    	String data = "this is my message";
    	byte[] bytes = data.getBytes();
    	MessageConsumer mc = messageManager.createMessageConsumer(qb);
    	this.messageManager.sendBytesToDestinations(bytes, qb);
    	Message msg = mc.receive(3000);
    	if(msg instanceof BytesMessage) {
    		BytesMessage bm = (BytesMessage)msg;
    		long len = bm.getBodyLength();
    		Assert.assertEquals((int)len,bytes.length);
    		byte[] out = new byte[bytes.length];
    		bm.readBytes(out);
    		String s = new String(out);
    		Assert.assertEquals(s,data);
    	} else {
    		Assert.assertFalse(true);
    	}
    }
}
