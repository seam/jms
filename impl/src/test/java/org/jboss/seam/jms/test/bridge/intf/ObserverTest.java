/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.jms.test.bridge.intf;

import javax.enterprise.inject.Default;
import org.jboss.seam.jms.bridge.RouteBuilder;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.jboss.logging.Logger;
import javax.jms.Connection;
import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.solder.bean.ImmutableInjectionPoint;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.arquillian.api.Deployment;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import static org.junit.Assert.*;

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

    @Inject @Default
    Event<String> stringEvent;
    @Inject Connection c;
    @Inject @JmsDestination(jndiName="jms/T2") Topic t;
    @Inject RouteBuilder routeBuilder;
    Logger log = Logger.getLogger(ObserverTest.class);
    @Test
    public void testObserve() throws JMSException {
        c.start();
        log.debug("Running ObserverTest");
        stringEvent.fire(EVENT_MSG);
        try {
            SimpleListener sl = new SimpleListener();
            Session session = c.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
            MessageConsumer mc = session.createConsumer(t);
            mc.setMessageListener(sl);
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