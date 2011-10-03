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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;

import org.jboss.solder.logging.Logger;

public class JmsMessageImpl implements JmsMessage {
	private MessageManager messageManager;
	private String selector = null;
	private Class<?> payloadType;
	private Object payload;
	private List<Destination> destinations;
	private Map<String,Object> headers;
	private Map<String,Object> properties;
	private Logger logger;
	
	protected JmsMessageImpl(Class<?> payloadType, Object payload, MessageManager messageManager) {
		this.logger = Logger.getLogger(JmsMessageImpl.class);
		this.payloadType = payloadType;
		this.payload(payload);
		this.messageManager = messageManager;
		destinations = new ArrayList<Destination>();
		headers = new HashMap<String,Object>();
		properties = new HashMap<String,Object>();
	}
	
	@Override
	public JmsMessage destination(Destination destination) {
		this.destinations.add(destination);
		return this;
	}

	@Override
	public JmsMessage destination(String jndiName) {
		Destination d = messageManager.lookupDestination(jndiName);
		if(d == null) {
			logger.warn("Unable to find a destination at "+jndiName);
			return this;
		} else {
			return destination(d);
		}
	}

	@Override
	public JmsMessage headers(Map<String, Object> headers) {
		this.headers = headers;
		return this;
	}

	@Override
	public JmsMessage properties(Map<String, Object> properties) {
		this.properties = properties;
		return this;
	}

	@Override
	public JmsMessage payload(Object payload) {
		this.payload = payload;
		return this;
	}

	@Override
	public JmsMessage selector(String selector) {
		this.selector = selector;
		return this;
	}

	@Override
	public Class<?> getPayloadType() {
		return this.payloadType;
	}

	@Override
	public Object getPayload() {
		return this.payload;
	}

	@Override
	public Map<String, Object> getHeaders() {
		return this.headers;
	}

	@Override
	public Map<String, Object> getProperties() {
		return this.properties;
	}

	@Override
	public String getSelector() {
		return this.selector;
	}
	
	@Override
	public List<Destination> getDestinations() {
		return this.destinations;
	}
}
