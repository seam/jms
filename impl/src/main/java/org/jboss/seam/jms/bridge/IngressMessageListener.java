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
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.jboss.solder.logging.Logger;
import org.jboss.seam.jms.AbstractMessageListener;
import org.jboss.seam.jms.annotations.InboundLiteral;

import static org.jboss.seam.jms.annotations.RoutingLiteral.INGRESS;

/**
 * @author johnament
 */
public class IngressMessageListener extends AbstractMessageListener {

    private Annotation[] qualifiers = null;
    private Logger logger;
    private Class<?> payload;
    private Route route;

    public IngressMessageListener(BeanManager beanManager,
                                  ClassLoader classLoader, Route route) {
        super(beanManager, classLoader);
        this.logger = Logger.getLogger(IngressMessageListener.class);
        logger.debug("Creating new IngressMessageListener.");
        setRoute(route);
    }

    public void setRoute(Route route) {
        logger.info("Setting route. " + route);
        Set<Annotation> annotations = new HashSet<Annotation>();
        if (!route.getQualifiers().isEmpty()) {
            annotations.addAll(route.getQualifiers());
        }
        this.payload = (Class<?>) route.getPayloadType();
        annotations.add(INGRESS);
		annotations.add(InboundLiteral.INSTANCE);
        logger.info("Qualifiers: " + annotations);
        this.qualifiers = annotations.toArray(new Annotation[]{});
        this.route = route;
    }

    private boolean isMessagePayload() {
        return this.payload.isAssignableFrom(Message.class);
    }

    @Override
    protected void handleMessage(Message msg) throws JMSException {
        if (!this.route.isIngressEnabled())
            return;
        if (isMessagePayload()) {
            beanManager.fireEvent(msg, qualifiers);
        } else {
            // then the result is an object message, and we're going to
            // send the object.
            if (msg instanceof ObjectMessage) {
                ObjectMessage om = (ObjectMessage) msg;
                try {
                    Serializable data = (Serializable) om.getObject();
                    logger.debug("data was: " + om.getObject() + " of type "
                            + data.getClass().getCanonicalName());
                    beanManager.fireEvent(data, qualifiers);
                } catch (JMSException ex) {
                    logger.warn("Unable to read data in message " + msg);
                }
            } else if (msg instanceof TextMessage
                    && this.payload.isAssignableFrom(String.class)) {
                TextMessage tm = (TextMessage) msg;
                try {
                    String data = tm.getText();
                    logger.debug("data was: " + data + " of type "
                            + data.getClass().getCanonicalName());
                    beanManager.fireEvent(data, qualifiers);
                } catch (JMSException e) {
                    logger.warn("Unable to read data in message " + msg);
                }
            } else {
                logger.warn("Received the wrong type of message " + msg);
            }
        }

    }
}
