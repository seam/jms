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
package org.jboss.seam.jms.test.inject.destination;

import java.lang.annotation.Retention;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@RunWith(Arquillian.class)
public class InjectDestinationViaResource {
    @Qualifier
    @Retention(RUNTIME)
    public @interface Q {
    }

    @Qualifier
    @Retention(RUNTIME)
    public @interface T {
    }

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(InjectDestinationViaResource.class);
    }

    @Inject
    @T
    Instance<Topic> t;
    @Inject
    @Q
    Instance<Queue> q;

    @Test
    public void injectTopic() throws JMSException {
        Topic topic = t.get();
        Assert.assertNotNull(topic);
        Assert.assertEquals("T", topic.getTopicName());
    }

    @Test
    public void injectQueue() throws JMSException {
        Queue queue = q.get();
        Assert.assertNotNull(queue);
        Assert.assertNotNull("Q", queue.getQueueName());
    }
}
