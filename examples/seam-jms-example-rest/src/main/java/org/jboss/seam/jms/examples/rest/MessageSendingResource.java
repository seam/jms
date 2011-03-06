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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.jboss.logging.Logger;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.bridge.RouteBuilder;

/**
 *
 * @author johnament
 */
@RequestScoped
@Path("/req")
public class MessageSendingResource {
    //@Resource(mappedName="jms/LongT3") Topic t;
    //@Inject IngressMessageListener iml;
    //@Inject Session session;
    @Resource(mappedName="/ConnectionFactory") ConnectionFactory connectionFactory;
    @Inject @JmsDestination(jndiName="jms/LongT2") TopicPublisher tp;
    @Inject @JmsDestination(jndiName="jms/LongT4") Topic t4;
    @Inject RouteBuilder rb;
    @Inject Event<String> dataEvent;
    @Inject MessageObserver mo;
    @Inject Logger logger;
    @Inject Connection connection;

    @PostConstruct
    public void init() throws Exception{
        //connection.start();
    }
    @GET
    @Path("/msg/{data}")
    @Produces("text/plain")
    public String sendData(@PathParam("data") String data) {
        return sendObjData(data);
    }

    private String sendObjData(String s) {
        try{
            dataEvent.fire(s);
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        return s;
    }

    @GET
    @Path("/pub/{data}")
    @Produces("text/plain")
    public String sendL4(@PathParam("data") Long data) throws Exception {
        Connection conn = connectionFactory.createConnection();
        logger.infof("Received a long %s",data);
        Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        conn.start();
        session.createProducer(t4).send(session.createObjectMessage(data));
        //session.close();
        conn.close();
        return data.toString();
    }

    
    @GET
    @Path("/long/{data}")
    @Produces("text/plain")
    public String sendData(@PathParam("data") Long data) {
        String s = data+"";
        return sendObjData(s);
    }
}
