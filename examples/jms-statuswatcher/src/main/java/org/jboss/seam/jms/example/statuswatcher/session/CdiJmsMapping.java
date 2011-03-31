package org.jboss.seam.jms.example.statuswatcher.session;

import javax.enterprise.event.Observes;
//import javax.enterprise.util.AnnotationLiteral;
//import javax.inject.Inject;
import javax.jms.Queue;
import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Routing;
//import org.jboss.seam.jms.bridge.EventBridge;
//import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.bridge.RouteType;
//import org.jboss.seam.jms.annotations.EventRouting;
import org.jboss.seam.jms.example.statuswatcher.model.Status;
//import org.jboss.seam.jms.example.statuswatcher.qualifiers.StatusBridged;

public interface CdiJmsMapping
{
   @Routing(RouteType.EGRESS)
   public void mapStatusToQueue(@Observes Status s, @JmsDestination(jndiName = "/jms/updateStatusQueue") Queue q);
}
