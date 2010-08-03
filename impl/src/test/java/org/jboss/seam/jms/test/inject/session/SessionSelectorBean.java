package org.jboss.seam.jms.test.inject.session;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.jms.Session;

import org.jboss.seam.jms.annotations.JmsSessionSelector;

public class SessionSelectorBean
{

   class SessionQualifier extends AnnotationLiteral<JmsSessionSelector> implements JmsSessionSelector
   {
      public int acknowledgementMode()
      {
         return Session.DUPS_OK_ACKNOWLEDGE;
      }
      
      public boolean transacted()
      {
         return false;
      }
   }
   
   @Inject
   Instance<Session> s;
   
   public Session get()
   {
      return s.select(new SessionQualifier()).get();
   }
}
