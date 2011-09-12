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
package org.jboss.seam.jms.test.routeBuilder;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.bridge.RouteManager;
import org.jboss.seam.jms.bridge.RouteManagerImpl;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RouteManagerImplTest {
	@Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(RouteManagerImplTest.class);
    }
	
	@Test
	public void testRouteManagerResults() {
		RouteManagerImpl rm = new RouteManagerImpl();
		Route r1 = rm.createInboundRoute(Object.class);
		Assert.assertFalse(r1.isEgressEnabled());
		Assert.assertTrue(r1.isIngressEnabled());
		Route r2 = rm.createOutboundRoute(Long.class);
		Assert.assertTrue(r2.isEgressEnabled());
		Assert.assertFalse(r2.isIngressEnabled());
	}
}
