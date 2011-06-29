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
package org.jboss.seam.jms.test.builder.topic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.TopicBuilder;
import org.jboss.seam.jms.TopicBuilderImpl;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TopicBuilderImplTest {
	@Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(TopicBuilderImplTest.class,TopicBuilderImpl.class);
    }
	
	@Inject TopicBuilder topicBuilder;
	
	@Test
	public void testNewBuilder() {
		TopicBuilder tb = topicBuilder.newBuilder();
		Assert.assertFalse(tb.equals(topicBuilder));
	}
	
	@Test
	public void testDestination() {
		TopicBuilder tb = topicBuilder.newBuilder();
		tb.destination("myDestination");
		if(!(tb instanceof TopicBuilderImpl)) {
			Assert.assertFalse(true);
		}
		TopicBuilderImpl tbi = (TopicBuilderImpl)tb;
		List<String> destinations = tbi.getDestinations();
		Assert.assertEquals(1, destinations.size());
		Assert.assertEquals("myDestination",destinations.get(0));
	}
	
	@Test
	public void testSubtopic() {
		String subtopic = "subt";
		TopicBuilder tb = topicBuilder.newBuilder();
		tb.subtopic(subtopic);
		if(!(tb instanceof TopicBuilderImpl)) {
			Assert.assertFalse(true);
		}
		TopicBuilderImpl tbi = (TopicBuilderImpl)tb;
		Assert.assertEquals(subtopic, tbi.getSubtopic());
	}
	
	private static void testMessageSent(boolean observed,Class<?> type,TopicTestListener ttl) {
		Assert.assertEquals(observed, ttl.isObserved());
		if(type == null) {
			Assert.assertTrue(ttl.getMessageClass() == null);
		} else {
			Assert.assertTrue(type.isAssignableFrom(ttl.getMessageClass()));
		}
	}
	
	@Test
	public void testListen() {
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().listen(ttl);
		testMessageSent(false,null,ttl);
	}
	
	@Test
	public void testSendMap() throws Exception {
		Map mapData = new HashMap<String,String>();
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().destination("jms/T3").listen(ttl).sendMap(mapData);
		Thread.sleep(5000);
		testMessageSent(true,MapMessage.class,ttl);
	}
	@Test
	public void testSendString() throws Exception {
		String data = "new data";
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().destination("jms/T1").listen(ttl).sendString(data);
		Thread.sleep(5000);
		testMessageSent(true,TextMessage.class,ttl);
	}
	@Test
	public void testSendObject() throws Exception {
		Object data = 33L;
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().destination("jms/T2").listen(ttl).sendObject(data);
		Thread.sleep(5000);
		testMessageSent(true,ObjectMessage.class,ttl);
	}
}
