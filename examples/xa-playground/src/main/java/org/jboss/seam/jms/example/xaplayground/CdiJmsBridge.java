package org.jboss.seam.jms.example.xaplayground;

import org.jboss.seam.jms.annotations.JmsDestination;
import org.jboss.seam.jms.annotations.Outbound;

import javax.enterprise.event.Observes;
import javax.jms.Queue;

@SuppressWarnings({"CdiManagedBeanInconsistencyInspection"})
public interface CdiJmsBridge {
// -------------------------- OTHER METHODS --------------------------

    @Outbound
    void mapStringToQueue(@Observes String status, @JmsDestination(jndiName = Constants.DEFAULT_QUEUE_JNDI) Queue q);
}
