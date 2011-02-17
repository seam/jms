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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.impl.inject.ConnectionProducer;
import org.jboss.seam.jms.impl.inject.DestinationProducer;
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
public class IngressTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(ObserverInterface.class, ImmutableInjectionPoint.class,
                DestinationProducer.class, MessagePubSubProducer.class, RouteBuilder.class, ConnectionProducer.class);
    }

    @Inject RouteBuilder builder;
    @Inject Connection conn;
    @Inject Session session;
    @Inject @JmsDestination(jndiName="jms/T2") Topic t;
    @Test
    public void testObserveMessage() throws JMSException, InterruptedException {
        conn.start();
        MessageProducer mp = session.createProducer(t);
        ObjectMessage om = session.createObjectMessage();
        om.setObject("hello, world!");
        mp.send(om);
        Thread.sleep(5 * 1000);
        mp.close();
        //conn.stop();
    }

    public void observeString(@Observes String s) {
        System.out.println("Received message "+s);
    }
}
