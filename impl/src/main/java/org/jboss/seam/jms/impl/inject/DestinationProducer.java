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

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.solder.reflection.AnnotationInspector;

public class DestinationProducer
{
   @Inject BeanManager beanManager;
   
    @Produces
    @JmsDestination
    public Topic getTopic(InjectionPoint ip, Context c) throws NamingException {
	  JmsDestination d = AnnotationInspector.getAnnotation(ip.getAnnotated(), JmsDestination.class, beanManager);
      return resolveTopic(d.jndiName(),c);
    }

    @Produces
    @JmsDestination
    public Queue getQueue(InjectionPoint ip, Context c) throws NamingException {
	  JmsDestination d = AnnotationInspector.getAnnotation(ip.getAnnotated(), JmsDestination.class, beanManager);
	  return resolveQueue(d.jndiName(),c);
    }
    
    public Topic resolveTopic(String jndiName, Context c) throws NamingException {
    	return (Topic)  resolveDestination(jndiName,c);
    }
    public Queue resolveQueue(String jndiName, Context c) throws NamingException {
    	return (Queue) resolveDestination(jndiName,c);
    }
    public Destination resolveDestination(String jndiName, Context c) throws NamingException {
    	return (Destination) c.lookup(jndiName);
    }
}
