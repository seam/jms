package org.jboss.seam.jms.example.statuswatcher.session;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.seam.jms.bridge.RouteBuilder;
import org.jboss.seam.jms.example.statuswatcher.model.Status;

@RequestScoped
@Named
public class SendingClient
{
   @Inject Event<Status> statusEvent;
   
   @Inject RouteBuilder rb;
   
   private Status status;
   
   @PostConstruct
   public void initialize()
   {
      this.status = new Status();
   }
   
   public void sendStatusUpdate() throws Exception
   {
      statusEvent.fire(status);
   }
   
   public Status getStatus()
   {
      return status;
   }

   public void setStatus(Status status)
   {
      this.status = status;
   }
}
