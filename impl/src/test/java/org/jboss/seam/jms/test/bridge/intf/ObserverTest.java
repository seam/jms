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

import org.junit.Ignore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.ObjectMessage;
import javax.jms.JMSException;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.seam.jms.annotations.JmsDestination;
import javax.jms.TopicSubscriber;
import org.jboss.seam.solder.bean.ImmutableInjectionPoint;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.jboss.arquillian.api.Deployment;
import org.junit.runner.RunWith;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author johnament
 */
@Ignore
@RunWith(Arquillian.class)
public class ObserverTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(ObserverInterface.class, ImmutableInjectionPoint.class, MessagePubSubProducer.class);
    }
    @Inject
    Event<String> stringEvent;
    @Inject @JmsDestination(jndiName="jms/T2") TopicSubscriber ts;

    @Test
    public void testObserve() throws JMSException {
        ts.setMessageListener(new ObserverListener());
        stringEvent.fire("hello, world!");
        try {
            Thread.sleep(10*60 * 1000);
            /*ObjectMessage msg = (ObjectMessage)ts.receive(3000);
            assertNotNull(msg);
            String data = msg.getObject().toString();
            assertEquals(data,"hello, world!");
            ts.close();*/
        } catch (InterruptedException ex) {
            Logger.getLogger(ObserverTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}