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
package org.jboss.seam.jms.bridge;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.AsyncLiteral;

/**
 *
 * @author johnament
 */
public class IngressMessageListener implements MessageListener {

    private BeanManager beanManager;
    private Annotation[] qualifiers = null;
    private Logger logger;
    private ClassLoader classLoader;

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
        annotations.add(new AsyncLiteral());
        this.qualifiers = annotations.toArray(new Annotation[]{});
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }

    public Annotation[] getAnnotations() {
        return qualifiers;
    }

    public void onMessage(Message msg) {
        logger.info("Received a message");
        ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try{
            if (msg instanceof ObjectMessage) {
                ObjectMessage om = (ObjectMessage) msg;
                try {
                    Serializable data = (Serializable)om.getObject();
                    logger.info(" data was: " + om.getObject());
                    //if(qualifiers == null) {
                    //BeanManager beanManager = Utils.lookupBM();
                    try {
                        beanManager.fireEvent(data, getAnnotations());
                    } catch (Exception e) {
                        logger.error("Unable to fire event", e);
                    }
                    /*} else {
                    beanManager.fireEvent(data, qualifiers);
                    }*/
                } catch (JMSException ex) {
                    logger.warn("Unable to read data in message " + msg);
                }
            } else {
                logger.warn("Received the wrong type of message " + msg);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(prevCl);
        }
    }
}
