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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;

public interface JmsMessage extends Serializable {
	public JmsMessage destination(Destination destination);
	public JmsMessage destination(String destination);
	public JmsMessage headers(Map<String,Object> headers);
	public JmsMessage properties(Map<String,Object> properties);
	public JmsMessage payload(Object payload);
	public JmsMessage selector(String selector);
	public Class<?> getPayloadType();
	public Object getPayload();
	public Map<String,Object> getHeaders();
	public Map<String,Object> getProperties();
	public String getSelector();
	public List<Destination> getDestinations();
}
