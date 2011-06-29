package org.jboss.seam.jms.test.routeBuilder;

import javax.inject.Inject;
import javax.jms.JMSException;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.bridge.RouteBuilderImpl;
import org.jboss.seam.jms.test.Util;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RouteBuilderImplTest {
	@Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(RouteBuilderImpl.class);
    }
	
	@Inject RouteBuilder routeBuilder;
	
	@Test
	public void testInit() throws JMSException {
		routeBuilder.init();
		Assert.assertTrue(true);
	}
}
