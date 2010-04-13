package org.jboss.seam.jms.impl.inject;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.jboss.seam.jms.annotations.Module;

public @ApplicationScoped
class ConnectionProducer
{
   @Produces
   @Module
   @Resource(mappedName = "ConnectionFactory")
   private ConnectionFactory cf;

   @Produces
   public Connection getConnection() throws Exception
   {
      return cf.createConnection();
   }

   public void closeConnection(@Disposes Connection c) throws JMSException
   {
      c.close();
   }
}
