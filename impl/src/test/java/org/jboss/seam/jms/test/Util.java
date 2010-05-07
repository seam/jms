/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

   public static JavaArchive addBeansXml(JavaArchive a, Class<?> c)
   {
      return addBeansXml(a, c, "beans.xml");
   }
   
   public static JavaArchive addBeansXml(JavaArchive a, Class<?> c, String beansXmlLocalName)
   {
      return addManifestResource(a, c, beansXmlLocalName, "beans.xml");
   }

   public static JavaArchive addManifestResource(JavaArchive a, Class<?> c, String name, String archivePath)
   {
      String basePkg = c.getPackage().getName().replaceAll("\\.", "/");
      return a.addManifestResource(basePkg + "/" + name, ArchivePaths.create(archivePath));
   }
}
