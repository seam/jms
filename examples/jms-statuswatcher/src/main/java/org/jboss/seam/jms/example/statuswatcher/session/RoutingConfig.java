package org.jboss.seam.jms.example.statuswatcher.session;

import static org.jboss.seam.jms.bridge.RouteType.EGRESS;
import javax.enterprise.util.AnnotationLiteral;
import org.jboss.seam.jms.annotations.EventRouting;
import org.jboss.seam.jms.bridge.Route;
import org.jboss.seam.jms.bridge.RouteManager;
import org.jboss.seam.jms.example.statuswatcher.model.Status;
import org.jboss.seam.jms.example.statuswatcher.qualifiers.StatusBridge;


public class RoutingConfig
{
//   private static final AnnotationLiteral<StatusBridge> BRIDGED_VIA_ROUTE = new AnnotationLiteral<StatusBridge>()
//   {
//      private static final long serialVersionUID = 1L;
//   };
//
//   @EventRouting
//   public Route getRoute(RouteManager routeManager)
//   {
//      return routeManager.createRoute(EGRESS, Status.class).addQualifiers(BRIDGED_VIA_ROUTE).addDestinationJndiName("/jms/updateStatusQueue");
//   }
}

