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

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.jms.Queue;
import javax.jms.Topic;

import org.jboss.seam.jms.test.inject.destination.InjectDestinationViaResource.Q;
import org.jboss.seam.jms.test.inject.destination.InjectDestinationViaResource.T;

@Named
public class DestinationProducers {

    @Resource(mappedName = "/jms/T")
    @Produces
    @T
    Topic t;

    @Resource(mappedName = "/jms/Q")
    @Produces
    @Q
    Queue q;
}
