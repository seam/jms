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
package org.jboss.seam.jms.examples.rest;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import org.jboss.logging.Logger;

/**
 *
 * @author johnament
 */
@MessageDriven(name="T4_MDB", activationConfig =
{
@ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Topic"),
@ActivationConfigProperty(propertyName="Destination", propertyValue="jms/LongT2")
})
public class QueueHandlerMDB implements MessageListener{

    Logger logger = Logger.getLogger(QueueHandlerMDB.class);
    @Override
    public void onMessage(Message message) {
        logger.info("Handling onMessage");
        if(message instanceof TextMessage) {
            TextMessage tm = (TextMessage)message;
            try{
                logger.info("Received text: "+tm.getText());
            } catch (JMSException e) {
                logger.error("JMSException",e);
            }
        } else if(message instanceof ObjectMessage) {
            ObjectMessage om = (ObjectMessage)message;
            try{
                Object o = om.getObject();
                logger.info(o.getClass().getCanonicalName());
                logger.info("Object Data: "+o.toString());
            } catch (JMSException e) {
                logger.error("JMSException",e);
            }
        }
    }
}
