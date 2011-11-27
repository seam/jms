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
package org.jboss.seam.jms.test.builder.queue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.QueueBuilder;
import org.jboss.seam.jms.QueueBuilderImpl;
import org.jboss.seam.jms.test.DeploymentFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class QueueBuilderImplTest {

	@Deployment
    public static Archive<?> createDeployment() {
        return DeploymentFactory.createDeployment(QueueBuilderImplTest.class,QueueBuilderImpl.class);
    }
	
	@Inject QueueBuilder queueBuilder;
		
	@Test
	public void testNewBuilder() {
		QueueBuilder tb = queueBuilder.newBuilder();
		Assert.assertFalse(tb.equals(queueBuilder));
	}
	
	@Test
	public void testDestination() {
		QueueBuilder tb = queueBuilder.newBuilder();
		//tb.destination("myDestination");
		if(!(tb instanceof QueueBuilderImpl)) {
			Assert.assertFalse(true);
		}
		QueueBuilderImpl tbi = (QueueBuilderImpl)tb;
		//ist<javax.jms.Queue> destinations = tbi.getDestinations();
		//Assert.assertEquals(1, destinations.size());
		//Assert.assertNull(destinations.get(0));
	}
	
	private static void testMessageSent(boolean observed,Class<?> type,QueueTestListener ttl) {
		Assert.assertEquals(observed, ttl.isObserved());
		if(type == null) {
			Assert.assertTrue(ttl.getMessageClass() == null);
		} else {
			Assert.assertTrue(type.isAssignableFrom(ttl.getMessageClass()));
		}
	}
	
	@Test
	public void testListen() {
		QueueTestListener ttl = new QueueTestListener();
		queueBuilder.newBuilder().listen(ttl);
		testMessageSent(false,null,ttl);
	}
	@Resource(mappedName="jms/QA")
        Queue qa;
        
        @Resource(mappedName="jms/QB")
        Queue qb;
        
        @Resource(mappedName="jms/QC")
        Queue qc;
        
	@Test
	public void testSendMap() {
		QueueTestListener ttl = new QueueTestListener();
		Map mapData = new HashMap<String,String>();
		mapData.put("my key","my value");
		queueBuilder.newBuilder().destination(qa).listen(ttl).sendMap(mapData);
		DeploymentFactory.pause(5000);
		testMessageSent(true,MapMessage.class,ttl);
	}
	@Test
	public void testSendString() {
		QueueTestListener ttl = new QueueTestListener();
		String data = "new data";
		queueBuilder.newBuilder().destination(qb).listen(ttl).sendString(data);
		DeploymentFactory.pause(5000);
		testMessageSent(true,TextMessage.class,ttl);
	}
	@Test
	public void testSendObject() {
		QueueTestListener ttl = new QueueTestListener();
		Serializable data = 33L;
		queueBuilder.newBuilder().destination(qc).listen(ttl).sendObject(data);
		DeploymentFactory.pause(5000);
		testMessageSent(true,ObjectMessage.class,ttl);
	}
}
