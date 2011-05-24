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
package org.jboss.seam.jms.test.bridge.intf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Outbound;
import org.jboss.seam.jms.annotations.Routing;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.bridge.RouteType;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.seam.jms.test.Util;
import org.jboss.seam.solder.bean.ImmutableInjectionPoint;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author johnament
 */
@RunWith(Arquillian.class)
public class ObserverTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(ObserverInterface.class, ImmutableInjectionPoint.class, MessagePubSubProducer.class);
    }
    private static final String EVENT_MSG = "hello, world!";

    @Inject @Outbound
    Event<String> stringEvent;
    @Inject Connection conn;
    @Inject Session session;
    @Inject @JmsDestination(jndiName="jms/T2") Topic t;
    @Inject RouteBuilder routeBuilder;
    Logger log = Logger.getLogger(ObserverTest.class);
    @Test
    public void testObserve() throws JMSException {
        log.debug("Running ObserverTest");
        try {
            SimpleListener sl = new SimpleListener();
            //Session session = conn.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            MessageConsumer mc = session.createConsumer(t);
            mc.setMessageListener(sl);
            stringEvent.fire(EVENT_MSG);
            Thread.sleep(10 * 1000);
            assertTrue(sl.isObserved());
            String data = sl.getData();
            assertEquals(data,EVENT_MSG);
            mc.close();
            //c.stop();
        } catch (InterruptedException ex) {
            log.info("Error",ex);
        }

    }
}