/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.jms.test.bridge.intf;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

/**
 *
 * @author johnament
 */
public class SimpleListener implements javax.jms.MessageListener {
    private boolean observed=false;
    private String data=null;
    @Override
    public void onMessage(Message msg) {
        if(msg instanceof ObjectMessage) {
            observed =true;
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
