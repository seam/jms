package org.jboss.seam.jms.test.bridge.intf;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.jms.annotations.Routing;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.bridge.RouteBuilderImpl;
import org.jboss.seam.jms.bridge.RouteType;
import org.jboss.seam.jms.impl.inject.DestinationProducer;
import org.jboss.seam.jms.impl.inject.MessagePubSubProducer;
import org.jboss.seam.jms.test.Util;
import org.jboss.solder.bean.ImmutableInjectionPoint;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BidirectionalTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return Util.createDeployment(ObserverInterface.class, ImmutableInjectionPoint.class,
                DestinationProducer.class, RouteBuilderImpl.class);
    }

    @Inject
    @Routing(RouteType.EGRESS)
    Event<Double> doubleEvent;
    @Inject
    RouteBuilder builder;

    private static boolean received = false;

    @Test
    public void testSendingAndReceiving() throws Exception {
        doubleEvent.fire(7.08);
        Thread.sleep(7000);
        Assert.assertTrue(received);
    }

    public void observeDoubleOverJms(@Observes @Routing(RouteType.INGRESS) Double d) {
        received = true;
    }
}
