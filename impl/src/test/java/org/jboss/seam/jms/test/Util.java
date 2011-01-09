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
import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.impl.inject.ConnectionProducer;
import org.jboss.seam.jms.impl.wrapper.JmsAnnotatedTypeWrapper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Util
{
   private static final String HORNETQ_JMS_DEPLOYMENT_CONFIG = "hornetq-jms.xml";

   public static Archive<?> createDeployment(Class<?>... classes)
   {
      JavaArchive ejbModule = ShrinkWrap.create(JavaArchive.class, "test.jar");
      ejbModule.addPackage(Util.class.getPackage());
      ejbModule.addPackage(Seam3JmsExtension.class.getPackage());
      ejbModule.addPackage(JmsSession.class.getPackage());
      ejbModule.addPackage(ConnectionProducer.class.getPackage());
      ejbModule.addPackage(JmsAnnotatedTypeWrapper.class.getPackage());
      ejbModule.addPackage(Route.class.getPackage());
      ejbModule.addManifestResource(EmptyAsset.INSTANCE, "beans.xml");
      ejbModule.addServiceProvider(Extension.class, Seam3JmsExtension.class);
      for (Class<?> c : classes)
      {
         ejbModule.addPackage(c.getPackage());
      }

      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
      war.addLibraries(ejbModule);
      war.addManifestResource(HORNETQ_JMS_DEPLOYMENT_CONFIG); // TODO Add this conditionally based on test profile to support other containers
      return war;
   }
}
