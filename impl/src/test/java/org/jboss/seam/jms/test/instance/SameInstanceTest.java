package org.jboss.seam.jms.test.instance;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.Connection;

import org.jboss.arquillian.api.Deployment;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

public class SameInstanceTest
{

   @Deployment
   public static JavaArchive createDeployment()
   {
      return Util.createDeployment(SameInstanceTest.class);
   }

   @Inject
   Instance<Connection> c1;
   
   @Inject
   Instance<Connection> c2;

   @Test
   public void sameConnection()
   {
      Assert.assertEquals(c1, c2);
   }
}
