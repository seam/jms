package org.jboss.seam.jms.impl.wrapper;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;

import org.jboss.seam.jms.annotations.JmsDestination;

/**
 * Wraps {@link AnnotatedConstructor}s that declare transitive annotations to
 * {@link JmsDestination} with the actual {@link JmsDestination}.
 * 
 * @author Jordan Ganoff
 */
public class JmsDestinationConstructorWrapper<X> extends JmsDestinationCallableWrapper<X> implements AnnotatedConstructor<X>
{

   public JmsDestinationConstructorWrapper(AnnotatedConstructor<X> decorated)
   {
      super(decorated);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected AnnotatedConstructor<X> decorated()
   {
      return AnnotatedConstructor.class.cast(super.decorated());
   }

   public Constructor<X> getJavaMember()
   {
      return decorated().getJavaMember();
   }
}
