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
package org.jboss.seam.jms.tools;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.QueueReceiver;
import javax.jms.TopicSubscriber;

import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.Closeable;

/**
 * Resource manager is able to manage the end lifecycle of various
 * JMS resources
 *
 * @author johnament
 */
@ApplicationScoped
public class JMSResourceManager {
    private Logger logger = Logger.getLogger(JMSResourceManager.class);

    private List<Connection> closeableConnections = new ArrayList<Connection>();
    private List<TopicSubscriber> topicSubscribes = new ArrayList<TopicSubscriber>();
    private List<MessageConsumer> messageConsumers = new ArrayList<MessageConsumer>();
    private List<QueueReceiver> receives = new ArrayList<QueueReceiver>();

    public void addCloseableConnection(@Observes @Closeable Connection connection) {
        closeableConnections.add(connection);
    }

    public void addCloseableTS(@Observes @Closeable TopicSubscriber ts) {
        topicSubscribes.add(ts);
    }

    public void addCloseableQR(@Observes @Closeable QueueReceiver qr) {
        receives.add(qr);
    }

    public void addCloseableMC(@Observes @Closeable MessageConsumer mc) {
        messageConsumers.add(mc);
    }

    @PreDestroy
    public void shutdown() {
        for (MessageConsumer mc : messageConsumers) {
            try {
                mc.close();
            } catch (JMSException e) {
                logger.debug("Unable to close message consumer", e);
            }
        }
        for (TopicSubscriber ts : topicSubscribes) {
            try {
                ts.close();
            } catch (JMSException e) {
                logger.debug("Unable to close topic subscriber", e);
            }
        }
        for (QueueReceiver qr : receives) {
            try {
                qr.close();
            } catch (JMSException e) {
                logger.debug("Unable to close queue receiver", e);
            }
        }
        for (Connection conn : closeableConnections) {
            try {
                conn.close();
                conn.stop();
            } catch (JMSException e) {
                logger.debug("Unable to close connection", e);
            }
        }
    }
}
