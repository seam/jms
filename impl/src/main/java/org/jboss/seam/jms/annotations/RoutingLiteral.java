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
package org.jboss.seam.jms.annotations;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.seam.jms.bridge.RouteType;

/**RoutingLiteral - literal implementation of the Routing qualifier.
 * 
 * provides two static members, representing EGRESS and INGRESS.
 * 
 * @author johnament
 *
 */
public class RoutingLiteral extends AnnotationLiteral<Routing> implements Routing {
	private RouteType routeType;
	
	public static final Routing EGRESS = new RoutingLiteral(RouteType.EGRESS);
	public static final Routing INGRESS = new RoutingLiteral(RouteType.INGRESS);
	
	public RoutingLiteral(RouteType routeType) {
		this.routeType = routeType;
	}
	
	@Override
	public RouteType value() {
		// TODO Auto-generated method stub
		return routeType;
	}

}
