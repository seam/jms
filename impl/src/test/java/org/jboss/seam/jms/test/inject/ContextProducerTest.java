package org.jboss.seam.jms.test.inject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.naming.Context;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.impl.inject.ContextProducer;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ContextProducerTest {

	@Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(ContextProducerTest.class,ContextProducer.class);
    }
	
	@Inject
	Instance<Context> contextInstance;
	
	@Test
	public void testInjectionResolved() {
		Assert.assertFalse(contextInstance.isUnsatisfied());
		Assert.assertNotNull(contextInstance.get());
	}
}
