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
package org.jboss.seam.jms;

import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.jboss.seam.jms.annotations.Durable;

@Durable
public interface DurableMessageManager extends MessageManager {

    /**
     * Initializes the connection for this DurableMessageManager.
     * Sets the ClientID for the underlying {@link javax.jms.Connection}
     *
     * @param clientId
     */
    public void login(String clientId);

    /**
     * Creates a topic subscriber with the given ID and binds a message listener to it, if valid.
     * <p/>
     * {@see MessageBuilder.createDurableSubscriber}
     *
     * @param topic    JNDI Location of the topic to subscribe to.
     * @param id       the client id for the subscriber.  This ID should be unique, and should be used to shutdown the listener.
     * @param listener The Message Listeners to be bound, if any.
     * @return the resulting TopicSubscriber or null if an error occurred.
     */
    public TopicSubscriber createDurableSubscriber(String topic, String id, MessageListener... listeners);

    /**
     * Creates a topic subscriber with the given ID and binds a message listener to it, if valid.
     * <p/>
     * {@see MessageBuilder.createDurableSubscriber}
     *
     * @param topic    the existing destination to reference.
     * @param id       the client id for the subscriber.  This ID should be unique, and should be used to shutdown the listener.
     * @param listener The Message Listeners to be bound, if any.
     * @return the resulting TopicSubscriber or null if an error occurred.
     */
    public TopicSubscriber createDurableSubscriber(Topic topic, String id, MessageListener... listeners);

    /**
     * Unsubscribes a durable subscriber from the topic, with the given id.
     *
     * @param id the id of the subscriber.
     */
    public void unsubscribe(String id);

}
