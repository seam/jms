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
package org.jboss.seam.jms.test.jmsMessage;

import java.util.Collections;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.JmsMessage;
import org.jboss.seam.jms.JmsMessageImpl;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.bridge.JmsMessageObserver;
import org.jboss.seam.jms.test.DeploymentFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JmsMessageImplTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return DeploymentFactory.createDeployment(JmsMessageImpl.class,JmsMessageObserver.class);
    }
    @Inject Event<JmsMessage> jmsMsgEvent;
    @Inject MessageManager messageManager;
    @Inject @JmsDestination(jndiName="jms/T5") javax.jms.Topic t5;
    
    @Test
    public void testCreateJmsMessage() {
    	JmsMessage strMsg = messageManager.createJmsMessage(String.class, "hello, world!");
    	Map<String,Object> headers = Collections.singletonMap("JMSMessageID",(Object)"ID:msg123456");
    	Map<String,Object> properties = Collections.singletonMap("boomdeadda",(Object)"ID:msg123456");
    	strMsg.destination("jms/QC").destination("none").destination(t5).headers(headers).properties(properties).selector(null);
    	jmsMsgEvent.fire(strMsg);
    	Assert.assertNull(strMsg.getSelector());
    }
}
