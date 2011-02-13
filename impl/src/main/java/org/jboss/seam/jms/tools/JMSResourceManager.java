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
package org.jboss.seam.jms.tools;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.QueueReceiver;
import javax.jms.TopicSubscriber;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.Closeable;

/** Resource manager is able to manage the end lifecycle of various
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
        for(MessageConsumer mc : messageConsumers) {
            try{
                mc.close();
            } catch (JMSException e) {
                logger.debug("Unable to close message consumer",e);
            }
        }
        for(TopicSubscriber ts : topicSubscribes) {
            try{
                ts.close();
            } catch (JMSException e) {
                logger.debug("Unable to close topic subscriber",e);
            }
        }
        for(QueueReceiver qr : receives) {
            try{
                qr.close();
            } catch (JMSException e) {
                logger.debug("Unable to close queue receiver",e);
            }
        }
        for(Connection conn : closeableConnections) {
            try{
                conn.close();
                conn.stop();
            } catch (JMSException e) {
                logger.debug("Unable to close connection",e);
            }
        }
    }
}
