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

import java.lang.annotation.Annotation;
import java.util.Iterator;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.jboss.seam.jms.annotations.JmsSession;
import org.jboss.seam.jms.annotations.JmsSessionSelector;

public class SessionProducer {
    @Produces
    @Dependent
    public Session getSession(Connection c, InjectionPoint ip) throws JMSException {
        JmsSession s = null;
        if (ip != null) {
            // Check for JmsSession annotation
            if (ip.getAnnotated().isAnnotationPresent(JmsSession.class)) {
                s = ip.getAnnotated().getAnnotation(JmsSession.class);
            } else {
                // Check meta-annotations
                for (Annotation a : ip.getAnnotated().getAnnotations()) {
                    if (a.annotationType().isAnnotationPresent(JmsSession.class)) {
                        s = a.annotationType().getAnnotation(JmsSession.class);
                    }
                }
            }
            if (s != null) {
                return c.createSession(s.transacted(), s.acknowledgementMode());
            }
        }

        // Default case where we cannot find an annotation
        return c.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void closeSession(@Disposes @Any Session s) throws JMSException {
        s.close();
    }

}
