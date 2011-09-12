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
package org.jboss.seam.jms.test.httpSession;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.bridge.RouteBuilderImpl;
import org.jboss.seam.jms.bridge.RouteManager;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test case for injecting objects into session scoped object.
 *
 * @author johnament
 */
@SessionScoped
@RunWith(Arquillian.class)
public class SessionScopedTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(MessageManager.class, RouteBuilder.class, RouteManager.class, RouteBuilderImpl.class);
    }

    @Inject
    MessageManager msgMgr;
    @Inject
    RouteBuilder rbild;

    @Test
    public void simpleTest() {
        Assert.assertTrue(true);
    }
}
