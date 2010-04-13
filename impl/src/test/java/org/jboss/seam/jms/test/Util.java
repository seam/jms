package org.jboss.seam.jms.test;

import javax.enterprise.inject.spi.Extension;

import org.jboss.seam.jms.Seam3JmsExtension;
import org.jboss.seam.jms.annotations.JmsSession;
import org.jboss.seam.jms.impl.inject.ConnectionProducer;
import org.jboss.seam.jms.impl.wrapper.JmsAnnotatedTypeWrapper;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;

public class Util
{

   public static JavaArchive createDeployment(Class<?> c)
   {
      JavaArchive archive = Archives.create("test.jar", JavaArchive.class);
      archive.addPackage(Util.class.getPackage());
      archive.addPackage(Seam3JmsExtension.class.getPackage());
      archive.addPackage(JmsSession.class.getPackage());
      archive.addPackage(ConnectionProducer.class.getPackage());
      archive.addPackage(JmsAnnotatedTypeWrapper.class.getPackage());
      archive.addManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml"));
      archive.addServiceProvider(Extension.class, Seam3JmsExtension.class);
      archive.addManifestResource("topic_T-service.xml");
      archive.addManifestResource("queue_Q-service.xml");

      archive.addPackage(c.getPackage());

      return archive;
   }
}
