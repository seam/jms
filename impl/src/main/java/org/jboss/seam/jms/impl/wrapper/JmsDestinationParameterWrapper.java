package org.jboss.seam.jms.impl.wrapper;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;

import org.jboss.seam.jms.annotations.JmsDestination;

/**
 * Wraps {@link AnnotatedParameter}s that declare transitive annotations to
 * {@link JmsDestination} with the actual {@link JmsDestination}.
 * 
 * @author Jordan Ganoff
 */
public class JmsDestinationParameterWrapper<X> extends JmsDestinationAnnotatedWrapper implements AnnotatedParameter<X>
{

   public JmsDestinationParameterWrapper(AnnotatedParameter<X> decorated)
   {
      super(decorated);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected AnnotatedParameter<X> decorated()
   {
      return AnnotatedParameter.class.cast(super.decorated());
   }

   public AnnotatedCallable<X> getDeclaringCallable()
   {
      return decorated().getDeclaringCallable();
   }

   public int getPosition()
   {
      return decorated().getPosition();
   }

}
