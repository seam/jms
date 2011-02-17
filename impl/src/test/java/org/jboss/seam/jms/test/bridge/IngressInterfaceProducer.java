/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.seam.jms.test.bridge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.Produces;
import org.jboss.seam.jms.test.bridge.intf.ObserverInterface;

/**
 *
 * @author johnament
 */
public class IngressInterfaceProducer {
    @Produces
    public Collection<Class<?>> produceIngressRoutes() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(ObserverInterface.class);
        return classes;
    }
}
