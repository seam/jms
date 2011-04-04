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
import org.jboss.seam.jms.annotations.Routing;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.bridge.RouteType;

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
    @Inject @Routing(RouteType.EGRESS) Event<String> dataEvent;
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
