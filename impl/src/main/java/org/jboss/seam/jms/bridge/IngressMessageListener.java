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
package org.jboss.seam.jms.bridge;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.spi.BeanManager;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;
import static org.jboss.seam.jms.annotations.RoutingLiteral.INGRESS;

/**
 *
 * @author johnament
 */
public class IngressMessageListener implements MessageListener {

    private BeanManager beanManager;
    private Annotation[] qualifiers = null;
    private Logger logger;
    private ClassLoader classLoader;
    private Class<?> payload;

    public IngressMessageListener(BeanManager beanManager, ClassLoader classLoader) {
        this.logger = Logger.getLogger(IngressMessageListener.class);
        this.beanManager = beanManager;
        this.classLoader = classLoader;
        logger.info("Creating new IngressMessageListener.");
    }

    public void setRoute(Route route) {
        logger.info("Setting route. "+route);
        Set<Annotation> annotations = new HashSet<Annotation>();
        if (!route.getQualifiers().isEmpty()) {
            annotations.addAll(route.getQualifiers());
        }
        this.payload = (Class<?>)route.getPayloadType();
        annotations.add(INGRESS);
        logger.info("Qualifiers: "+annotations);
        this.qualifiers = annotations.toArray(new Annotation[]{});
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }

    public Annotation[] getAnnotations() {
        return qualifiers;
    }
    
    private boolean isMessagePayload() {
    	return this.payload.isAssignableFrom(Message.class);
    }

    public void onMessage(Message msg) {
        logger.info("Received a message");
        ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try{
	        if(isMessagePayload()) {
	        	beanManager.fireEvent(msg,getAnnotations());
	        } else {
	        	// then the result is an object message, and we're going to 
	        	// send the object.
	            if (msg instanceof ObjectMessage) {
	                ObjectMessage om = (ObjectMessage) msg;
	                try {
	                    Serializable data = (Serializable)om.getObject();
	                    logger.debug("data was: " + om.getObject()+" of type "+data.getClass().getCanonicalName());
                    	beanManager.fireEvent(data,getAnnotations());
	                } catch (JMSException ex) {
	                    logger.warn("Unable to read data in message " + msg);
	                }
	            } else if(msg instanceof TextMessage && 
	            		this.payload.isAssignableFrom(String.class)){
	            	TextMessage tm = (TextMessage)msg;
	            	try {
						String data = tm.getText();
						logger.debug("data was: " + data+" of type "+data.getClass().getCanonicalName());
                    	beanManager.fireEvent(data,getAnnotations());
					} catch (JMSException e) {
						logger.warn("Unable to read data in message " + msg);
					}
	            } else {
	            	logger.warn("Received the wrong type of message " + msg);
	            }
	        }
        } finally {
            Thread.currentThread().setContextClassLoader(prevCl);
        }
    }
}
