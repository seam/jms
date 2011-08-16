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
package org.jboss.seam.jms.bridge;

import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.jboss.seam.solder.logging.Logger;
import org.jboss.seam.jms.JmsMessage;
import org.jboss.seam.jms.MessageManager;

public class JmsMessageObserver {
	private Logger logger = Logger.getLogger(JmsMessageObserver.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void observeJmsMessages(@Observes JmsMessage jmsMessage, MessageManager messageManager) throws JMSException {
		Message msg = null;
		Class<?> payloadType = jmsMessage.getPayloadType();
		if(payloadType.isAssignableFrom(String.class)) {
			String data = (String)jmsMessage.getPayload();
			msg = messageManager.createTextMessage(data);
		} else if(payloadType.isAssignableFrom(Map.class)) {
			Map map = (Map)jmsMessage.getPayload();
			msg = messageManager.createMapMessage(map);
		} else {
			msg = messageManager.createObjectMessage(jmsMessage.getPayload());
		}
		Map<String,Object> properties = jmsMessage.getProperties();
		Set<String> propKeys = properties.keySet();
		for(String key : propKeys) {
			Object value = properties.get(key);
			Class<?> type = value.getClass();
			if(type.isAssignableFrom(Boolean.class)){
				msg.setBooleanProperty(key, (Boolean)value);
			} else if(type.isAssignableFrom(Byte.class)){
				msg.setByteProperty(key, (Byte)value);
			} else if(type.isAssignableFrom(Double.class)){
				msg.setDoubleProperty(key, (Double)value);
			} else if(type.isAssignableFrom(Float.class)){
				msg.setFloatProperty(key, (Float)value);
			} else if(type.isAssignableFrom(Integer.class)){
				msg.setIntProperty(key, (Integer)value);
			} else if(type.isAssignableFrom(Long.class)){
				msg.setLongProperty(key, (Long)value);
			} else if(type.isAssignableFrom(Short.class)){
				msg.setShortProperty(key, (Short)value);
			} else if(type.isAssignableFrom(String.class)){
				msg.setStringProperty(key, (String)value);
			} else {
				msg.setObjectProperty(key, value);
			}
		}
		Map<String,Object> headers = jmsMessage.getHeaders();
		Set<String> headerKeys = headers.keySet();
		for(String key : headerKeys) {
			Object value = headers.get(key);
			if(key.equalsIgnoreCase("JMSCorrelationID")) {
				msg.setJMSCorrelationID(value.toString());
			} else if(key.equalsIgnoreCase("JMSDeliveryMode")) { 
				msg.setJMSDeliveryMode((Integer)value);
			} else if(key.equalsIgnoreCase("JMSExpiration")) { 
				msg.setJMSExpiration((Long)value);
			}  else if(key.equalsIgnoreCase("JMSMessageID")) { 
				msg.setJMSMessageID(value.toString());
			} else if(key.equalsIgnoreCase("JMSPriority")) { 
				msg.setJMSPriority((Integer)value);
			} else if(key.equalsIgnoreCase("JMSRedelivered")) { 
				msg.setJMSRedelivered((Boolean)value);
			} else if(key.equalsIgnoreCase("JMSTimestamp")) { 
				msg.setJMSTimestamp((Long)value);
			} else if(key.equalsIgnoreCase("JMSType")) { 
				msg.setJMSType(value.toString());
			} else {
				logger.warnf("Unable to set header %s on message with value %s",key,value);
			}
		}
		Destination[] destinations = jmsMessage.getDestinations().toArray(new Destination[]{});
		messageManager.sendMessage(msg, destinations);
	}
}
