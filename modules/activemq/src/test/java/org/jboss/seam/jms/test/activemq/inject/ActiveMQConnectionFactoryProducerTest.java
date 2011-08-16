package org.jboss.seam.jms.test.activemq.inject;

import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.MessageManager;
import org.jboss.seam.jms.MessageManagerImpl;
import org.jboss.seam.jms.activemq.inject.ActiveMQConnectionFactoryProducer;
import org.jboss.seam.jms.annotations.EventRouting;
import org.jboss.seam.jms.annotations.JmsDefault;
import org.jboss.seam.jms.impl.inject.ContextProducer;
import org.jboss.seam.jms.impl.inject.DestinationProducer;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.seam.jms.inject.JmsConnectionFactoryProducer;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ActiveMQConnectionFactoryProducerTest {

	@Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
        		.addAsManifestResource(EmptyAsset.INSTANCE,"beans.xml")
        		.addPackage(JmsConnectionFactoryProducer.class.getPackage())
        		.addPackage(EventRouting.class.getPackage())
        		.addClasses(ActiveMQConnectionFactoryProducer.class, 
        				MessageManager.class,MessageManagerImpl.class,
        				ContextProducer.class, DestinationProducer.class,
        				MessagePubSubProducer.class);
        				
    }
	
	@Inject @JmsDefault("connectionFactory")
	ConnectionFactory connectionFactory;
	
	@Inject MessageManager mgr;
	
	@Test
	public void testJmsConnectionFactoryProducer() {
		System.out.println(connectionFactory.getClass().getCanonicalName());
		Assert.assertNotNull(connectionFactory);
	}

	@Test
	public void testProduceConnectionFactory() {
		//fail("Not yet implemented");
	}

}
