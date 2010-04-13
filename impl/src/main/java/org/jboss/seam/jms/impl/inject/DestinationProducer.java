package org.jboss.seam.jms.impl.inject;

import static org.jboss.seam.jms.impl.inject.InjectionUtil.getExpectedQualifier;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Module;

public @RequestScoped class DestinationProducer
{

   @Produces
   @JmsDestination
   public Topic getTopic(InjectionPoint ip, @Module Context c) throws NamingException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      return (Topic) c.lookup(d.jndiName());
   }
   
   @Produces
   @JmsDestination
   public Queue getQueue(InjectionPoint ip, @Module Context c) throws NamingException
   {
      JmsDestination d = getExpectedQualifier(JmsDestination.class, ip.getQualifiers());
      return (Queue) c.lookup(d.jndiName());
   }
}
