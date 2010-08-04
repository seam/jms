package org.jboss.seam.jms.test.inject.session;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.Session;

import org.jboss.seam.jms.annotations.JmsSessionSelector.JmsSessionSelectorLiteral;

public class SessionSelectorBean
{
   @Inject
   @Any
   Instance<Session> s;

   public Session get()
   {
       return s.select(new JmsSessionSelectorLiteral(false, Session.DUPS_OK_ACKNOWLEDGE)).get();
   }
}
