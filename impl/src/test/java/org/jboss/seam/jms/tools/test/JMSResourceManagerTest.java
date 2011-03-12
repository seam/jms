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
package org.jboss.seam.jms.tools.test;

import javax.annotation.Resource;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.Closeable;
import org.jboss.seam.jms.annotations.Module;
import org.jboss.seam.jms.impl.inject.ConnectionProducer;
import org.jboss.seam.jms.test.Util;
import org.jboss.seam.jms.tools.JMSResourceManager;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author johnament
 */
@RunWith(Arquillian.class)
public class JMSResourceManagerTest {

   @Deployment
   public static Archive<?> createDeployment()
   {
      return Util.createDeployment(Closeable.class,JMSResourceManager.class,ConnectionProducer.class,JMSResourceManagerTest.class);
   }

   @Inject @Closeable Event<Connection> connectionHandler;
   @Inject @Module ConnectionFactory connectionFactory;

   @Test
   public void testClosingConnection() throws JMSException {
        Connection conn = connectionFactory.createConnection();
        connectionHandler.fire(conn);
   }
}