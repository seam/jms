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
package org.jboss.seam.jms.test.bridge.intf;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 *
 * @author johnament
 */
public class SimpleListener implements javax.jms.MessageListener {
    private boolean observed=false;
    private String data=null;
    @Override
    public void onMessage(Message msg) {
    	observed =true;
    	if(msg instanceof TextMessage) { 
    		try {
				data = ((TextMessage)msg).getText();
			} catch (JMSException e) {
			}
    	}
    	else if(msg instanceof ObjectMessage) {
            ObjectMessage om = (ObjectMessage) msg;
            try {
                data = om.getObject().toString();
            } catch (JMSException ex) {
            }
        }
    }

    public String getData() {
        return data;
    }

    public boolean isObserved() {
        return observed;
    }

}
