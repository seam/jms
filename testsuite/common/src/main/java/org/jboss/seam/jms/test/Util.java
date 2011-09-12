/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.jms.test;

import javax.enterprise.inject.spi.Extension;

import org.jboss.seam.jms.Seam3JmsExtension;
import org.jboss.seam.jms.annotations.JmsSession;
import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.impl.inject.SessionProducer;
import org.jboss.seam.jms.inject.JmsConnectionProducer;
import org.jboss.seam.jms.test.bridge.IngressInterfaceProducer;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public class Util {
    public static final String HORNETQ_JMS_DEPLOYMENT_CONFIG = "hornetq-jms.xml";
    
    public static WebArchive createDeployment(Class<?>... classes) {
        JavaArchive ejbModule = ShrinkWrap.create(JavaArchive.class, "test.jar");
        ejbModule.addPackage(Util.class.getPackage());
        ejbModule.addPackage(Seam3JmsExtension.class.getPackage());
        ejbModule.addPackage(JmsSession.class.getPackage());
        ejbModule.addPackage(SessionProducer.class.getPackage());
        ejbModule.addPackage(JmsConnectionProducer.class.getPackage());
        ejbModule.addPackage(Route.class.getPackage());
        ejbModule.addClasses(IngressInterfaceProducer.class);
        ejbModule.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        ejbModule.addAsServiceProvider(Extension.class, Seam3JmsExtension.class);
        for (Class<?> c : classes) {
            ejbModule.addPackage(c.getPackage());
        }
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        war.addAsLibraries(ejbModule);

        war.addAsWebInfResource(HORNETQ_JMS_DEPLOYMENT_CONFIG, HORNETQ_JMS_DEPLOYMENT_CONFIG);
        
        war.addAsLibraries(DependencyResolvers.use(MavenDependencyResolver.class)
                .configureFrom("../../settings.xml")
                .loadReposFromPom("pom.xml")
                .artifact("org.jboss.seam.solder:seam-solder")
                .resolveAs(JavaArchive.class));
        // Temporary workaround for SOLDER-119
        war.addAsWebInfResource(new StringAsset("<jboss-deployment-structure>\n" +
                "  <deployment>\n" +
                "    <dependencies>\n" +
                "      <module name=\"org.jboss.logmanager\" />\n" +
                "    </dependencies>\n" +
                "  </deployment>\n" +
                "</jboss-deployment-structure>"), "jboss-deployment-structure.xml");
        
        // TODO Add this conditionally based on test profile to support other containers
        return war;
    }
    
    public static void pause(long time) {
    	try{
			Thread.sleep(time);
		} catch (Exception e) { }
    }
}
