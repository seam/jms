package org.jboss.seam.jms.impl.inject;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.jms.annotations.Module;

public class ContextProducer
{
   @Produces
   @Module
   public Context getContext() throws NamingException
   {
      return new InitialContext();
   }

   public void disposeContext(@Disposes @Module Context ctx) throws NamingException
   {
      ctx.close();
   }
}
