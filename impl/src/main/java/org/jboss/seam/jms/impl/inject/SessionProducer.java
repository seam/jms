package org.jboss.seam.jms.impl.inject;

import static org.jboss.seam.jms.impl.inject.InjectionUtil.getExpectedQualifier;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.jboss.seam.jms.annotations.JmsSession;

public @RequestScoped
class SessionProducer
{
   @Produces
   public Session getGenericSession(InjectionPoint ip, Connection c) throws JMSException
   {
      return c.createSession(false, Session.AUTO_ACKNOWLEDGE);
   }

   @Produces
   @JmsSession
   public Session getSession(InjectionPoint ip, Connection c) throws JMSException
   {
      JmsSession s = getExpectedQualifier(JmsSession.class, ip.getQualifiers());
      return c.createSession(s.transacted(), s.acknowledgementType());
   }

   public void closeSession(@Disposes Session s) throws JMSException
   {
      s.close();
   }
}
