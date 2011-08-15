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
package org.jboss.seam.jms.impl.inject;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.seam.jms.annotations.JmsDefault;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.solder.reflection.AnnotationInspector;

public class MessagePubSubProducer {
    @Inject
    @Any
    Instance<Topic> anyTopic;

    @Inject
    @Any
    Instance<Queue> anyQueue;
    
    @Inject
    BeanManager beanManager;
    
    @Inject DestinationProducer destinationProducer;
    
    @Inject
    Context c;

    @Produces
    @JmsDestination
    public MessageConsumer createMessageConsumer(InjectionPoint ip, @JmsDefault("session") Session s) throws JMSException, NamingException {
    	JmsDestination d = AnnotationInspector.getAnnotation(ip.getAnnotated(), JmsDestination.class, beanManager);
    	Destination dest = destinationProducer.resolveDestination(d.jndiName(), c);
        return s.createConsumer(dest);
    }
    
    public void disposesMessageConsumer(@Disposes @Any MessageConsumer mc) throws JMSException {
        mc.close();
    }
    
    @Produces
    @JmsDestination
    public MessageProducer createMessageProducer(InjectionPoint ip, @JmsDefault("session") Session s) throws JMSException, NamingException {
    	JmsDestination d = AnnotationInspector.getAnnotation(ip.getAnnotated(), JmsDestination.class, beanManager);
    	Destination dest = destinationProducer.resolveDestination(d.jndiName(), c);
        return s.createProducer(dest);
    }
    
    public void disposesMessageProducer(@Disposes @Any MessageProducer mp) throws JMSException {
        mp.close();
    }

}
