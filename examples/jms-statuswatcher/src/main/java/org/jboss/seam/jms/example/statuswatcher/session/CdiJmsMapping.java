package org.jboss.seam.jms.example.statuswatcher.session;

import javax.enterprise.event.Observes;
import javax.jms.Queue;

import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Outbound;
import org.jboss.seam.jms.example.statuswatcher.model.Status;

public interface CdiJmsMapping {
    @Outbound
    public void mapStatusToQueue(@Observes Status status, @JmsDestination(jndiName = "/jms/updateStatusQueue") Queue q);
}
