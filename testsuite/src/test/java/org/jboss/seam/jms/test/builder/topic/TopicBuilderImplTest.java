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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.jms.Topic;
import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.TopicBuilder;
import org.jboss.seam.jms.TopicBuilderImpl;
import org.jboss.seam.jms.test.DeploymentFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TopicBuilderImplTest {
	@Deployment
    public static Archive<?> createDeployment() {
        return DeploymentFactory.createDeployment(TopicBuilderImplTest.class,TopicBuilderImpl.class);
    }
	
	@Inject TopicBuilder topicBuilder;
	
	@Test
	public void testNewBuilder() {
		TopicBuilder tb = topicBuilder.newBuilder();
		Assert.assertFalse(tb.equals(topicBuilder));
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
	
	@Test @Ignore
	public void testListen() {
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().listen(ttl);
		testMessageSent(false,null,ttl);
	}
        
        @Resource(mappedName="jms/T3") Topic t3;
        @Resource(mappedName="jms/T1") Topic t1;
        @Resource(mappedName="jms/T2") Topic t2;
        
        @Resource(mappedName="/ConnectionFactory") ConnectionFactory cf;
        
	
	@Test
	public void testSendMap() {
		Map mapData = new HashMap<String,String>();
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().connectionFactory(cf).transacted().sessionMode(Session.SESSION_TRANSACTED).destination(t3).listen(ttl).sendMap(mapData);
		DeploymentFactory.pause(5000);
		testMessageSent(true,MapMessage.class,ttl);
	}
	@Test
	public void testSendString() {
		String data = "new data";
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().connectionFactory(cf).transacted().sessionMode(Session.SESSION_TRANSACTED).destination(t1).listen(ttl).sendString(data);
		DeploymentFactory.pause(5000);
		testMessageSent(true,TextMessage.class,ttl);
	}
	@Test
	public void testSendObject() {
		Serializable data = 33L;
		TopicTestListener ttl = new TopicTestListener();
		topicBuilder.newBuilder().connectionFactory(cf).transacted().sessionMode(Session.SESSION_TRANSACTED).destination(t2).listen(ttl).sendObject(data);
		DeploymentFactory.pause(5000);
		testMessageSent(true,ObjectMessage.class,ttl);
	}
}
