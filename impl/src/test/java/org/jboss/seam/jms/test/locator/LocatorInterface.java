/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.seam.jms.test.locator;

import javax.enterprise.event.Observes;
import javax.jms.Topic;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Routing;
import org.jboss.seam.jms.bridge.RouteType;

/**
 *
 * @author johnament
 */
public interface LocatorInterface {
    @Routing(RouteType.EGRESS)
    public void obsStringToTopic(@Observes String s, @JmsDestination(jndiName="jms/T2") Topic t);
}
