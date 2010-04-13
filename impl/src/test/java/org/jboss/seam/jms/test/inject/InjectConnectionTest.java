package org.jboss.seam.jms.test.inject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.Session;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InjectConnectionTest
{

   @Deployment
   public static JavaArchive createDeployment()
   {
      return Util.createDeployment(InjectConnectionTest.class);
   }

   @Inject
   private Instance<Connection> c;

   @Inject
   private Instance<Session> s;

   @Test
   public void injectConnection()
   {
      Assert.assertNotNull(c.get());
   }

   @Test
   public void injectSession()
   {
      Assert.assertNotNull(s.get());
   }

}
