package org.jboss.seam.jms.impl.wrapper;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.AnnotatedMethod;

import org.jboss.seam.jms.annotations.JmsDestination;

/**
 * Wraps {@link AnnotatedMethod}s that declare transitive annotations to
 * {@link JmsDestination} with the actual {@link JmsDestination}.
 * 
 * @author Jordan Ganoff
 */
public class JmsDestinationMethodWrapper<X> extends JmsDestinationCallableWrapper<X> implements AnnotatedMethod<X>
{
   public JmsDestinationMethodWrapper(AnnotatedMethod<X> decorated)
   {
      super(decorated);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected AnnotatedMethod<X> decorated()
   {
      return AnnotatedMethod.class.cast(super.decorated());
   }

   public Method getJavaMember()
   {
      return decorated().getJavaMember();
   }
}
