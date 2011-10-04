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
package org.jboss.seam.jms.inject;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.solder.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDefault;

@ApplicationScoped
public class JmsConnectionFactoryProducer {
	private String connectionFactoryJNDILocation = "/ConnectionFactory";
	private Logger logger = Logger.getLogger(JmsConnectionFactoryProducer.class);
	@Inject Context context;
	@Produces @ApplicationScoped
	@JmsDefault("connectionFactory")
	public ConnectionFactory produceConnectionFactory() {
		try{
			return (ConnectionFactory) context.lookup(connectionFactoryJNDILocation);
		} catch (NamingException e) {
			logger.info("Unable to look up "+connectionFactoryJNDILocation+" in JNDI",e);
			return null;
		}
	}
}
