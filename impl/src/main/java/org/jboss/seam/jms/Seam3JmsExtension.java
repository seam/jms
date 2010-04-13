package org.jboss.seam.jms;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.seam.jms.impl.wrapper.JmsAnnotatedTypeWrapper;

/**
 * Seam 3 JMS Portable Extension
 * 
 * @author Jordan Ganoff
 */
public class Seam3JmsExtension implements Extension
{
   public <X> void decorateAnnotatedType(@Observes ProcessAnnotatedType<X> pat)
   {
      pat.setAnnotatedType(JmsAnnotatedTypeWrapper.decorate(pat.getAnnotatedType()));
   }
}
