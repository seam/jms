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
package org.jboss.seam.jms.test.bridge.route;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueReceiver;
import javax.jms.TextMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.bridge.RouteImpl;
import org.jboss.seam.jms.bridge.RouteType;
import org.jboss.seam.jms.test.DeploymentFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
//@Ignore
public class RouteTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return DeploymentFactory.createDeployment(RouteTest.class);
    }

    @Inject
    RouteBuilder routeBuilder;
    @Inject
    Connection c;
    @Inject
    MessageManager messageManager;
    @Inject
    @BridgedViaCollection
    Event<String> event_viaCollectionRouteConfig;
    @Inject
    @BridgedViaRoute
    Event<String> event_viaSingleRouteConfig;
    @Inject
    Event<String> plainEvent;

    private void clear(QueueReceiver qr) throws JMSException {
        if(qr != null)
        	while (qr.receiveNoWait() != null) ;
    }


    @Test
    public void forwardSimpleEvent() throws JMSException {
        String expected = "'configured via Collection<Route>'";
        QueueReceiver qr = messageManager.createQueueReceiver("queue/DLQ");
        clear(qr);
        event_viaCollectionRouteConfig.fire(expected);
        Message m = qr.receive(3000);
        qr.close();
        Assert.assertTrue(m != null);
        Assert.assertTrue(m instanceof TextMessage);
        Assert.assertEquals(expected, ((TextMessage) m).getText());
    }

    @Test
    public void noMatchingRoutes() throws JMSException {
        String expected = "'no matching route'";
        QueueReceiver qr = messageManager.createQueueReceiver("queue/DLQ");
        clear(qr);
        plainEvent.fire(expected);
        Message m = qr.receive(3000);
        qr.close();
        Assert.assertNull("Unexpectedly received a message", m);
    }

    @Test
    public void forwardSimpleEvent_via_single_route_config() throws JMSException {
        String expected = "'configured via Route'";
        QueueReceiver qr = messageManager.createQueueReceiver("queue/DLQ");
        clear(qr);
        event_viaSingleRouteConfig.fire(expected);
        Message m = qr.receive(3000);
        qr.close();
        Assert.assertTrue(m != null);
        Assert.assertTrue(m instanceof TextMessage);
        Assert.assertEquals(expected, ((TextMessage) m).getText());
    }

    @Test
    public void testRouteBehavior() {
        RouteImpl ri = new RouteImpl(RouteType.INGRESS, this.getClass());
        Assert.assertTrue(ri.isIngressEnabled());
        Assert.assertFalse(ri.isEgressEnabled());
        ri.disableIngress();
        Assert.assertFalse(ri.isIngressEnabled());
        ri.enableIngress();
        ri.enableEgress();
        Assert.assertTrue(ri.isIngressEnabled());
        Assert.assertFalse(ri.isEgressEnabled());

        RouteImpl ri2 = new RouteImpl(RouteType.BOTH, this.getClass());
        Assert.assertTrue(ri2.isIngressEnabled());
        Assert.assertTrue(ri2.isEgressEnabled());
        ri2.disableEgress();
        Assert.assertTrue(ri2.isIngressEnabled());
        Assert.assertFalse(ri2.isEgressEnabled());
    }
}
