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
package org.jboss.seam.jms.test.inject.session;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Session;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.JmsSession;
import org.jboss.seam.jms.annotations.JmsSessionSelector;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InjectSessionTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(InjectSessionTest.class);
    }

    @Inject
    private Instance<Session> s;

    @Inject
    @JmsSessionSelector(transacted = false, acknowledgementMode = Session.AUTO_ACKNOWLEDGE)
    private Instance<Session> qualified;

    @Inject
    @JmsSession(transacted = false, acknowledgementMode = Session.CLIENT_ACKNOWLEDGE)
    private Instance<Session> configuredSession;

    @Inject
    @EventSession
    private Instance<Session> eventSession;

    @Inject
    SessionSelectorBean selectorBean;

    @Test
    public void injectSession() {
        Assert.assertNotNull(s.get());
    }

    @Test
    public void injectSession_annotated() throws JMSException {
        Session s = configuredSession.get();
        Assert.assertNotNull(s);
        Assert.assertFalse(s.getTransacted());
        Assert.assertEquals(Session.CLIENT_ACKNOWLEDGE, s.getAcknowledgeMode());
    }

    @Test
    public void injectSession_meta_annotated() throws JMSException {
        Session s = eventSession.get();
        Assert.assertNotNull(s);
        Assert.assertFalse(s.getTransacted());
        Assert.assertEquals(Session.DUPS_OK_ACKNOWLEDGE, s.getAcknowledgeMode());
    }

    @Test
    public void injectQualifiedSession() throws JMSException {
        Session s = qualified.get();
        Assert.assertNotNull(s);
        Assert.assertFalse(s.getTransacted());
        Assert.assertEquals(Session.AUTO_ACKNOWLEDGE, s.getAcknowledgeMode());
    }

    @Test
    public void injectSessionWithLiteral() throws JMSException {
        Session session = selectorBean.get();
        Assert.assertNotNull(session);
        Assert.assertFalse(session.getTransacted());
        Assert.assertEquals(Session.DUPS_OK_ACKNOWLEDGE, session.getAcknowledgeMode());
    }
}
