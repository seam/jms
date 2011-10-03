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

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Topic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Routing;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.bridge.RouteBuilderImpl;
import org.jboss.seam.jms.bridge.RouteType;
import org.jboss.seam.jms.impl.inject.DestinationProducer;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.seam.jms.test.Util;
import org.jboss.solder.bean.ImmutableInjectionPoint;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author johnament
 */
@RunWith(Arquillian.class)
public class IngressTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(ObserverInterface.class, ImmutableInjectionPoint.class,
                DestinationProducer.class, RouteBuilderImpl.class);
    }

    @Inject
    RouteBuilder builder;
    @Inject
    MessageManager messageBuilder;
    @Inject
    @JmsDestination(jndiName = "jms/T2")
    Topic t;

    private static boolean received = false;

    @Test
    public void testObserveMessage() throws JMSException, InterruptedException {
        messageBuilder.sendObjectToDestinations(7L, t);
        Thread.sleep(5 * 1000);
        Assert.assertTrue(received);
    }

    public void observeString(@Observes @Routing(RouteType.INGRESS) Long l) {
        System.out.println("Received message " + l);
        received = true;
    }
}
