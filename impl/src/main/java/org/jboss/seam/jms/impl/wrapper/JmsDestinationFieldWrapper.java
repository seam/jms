package org.jboss.seam.jms.impl.wrapper;

import java.lang.reflect.Field;

import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;

import org.jboss.seam.jms.annotations.JmsDestination;

/**
 * Wraps {@link AnnotatedField}s that declare transitive annotations to
 * {@link JmsDestination} with the actual {@link JmsDestination}.
 * 
 * @author Jordan Ganoff
 */
public class JmsDestinationFieldWrapper<X> extends JmsDestinationAnnotatedWrapper implements AnnotatedField<X>
{
   public JmsDestinationFieldWrapper(AnnotatedField<X> decorated)
   {
      super(decorated);
   }

   @SuppressWarnings("unchecked")
   @Override
   protected AnnotatedField<X> decorated()
   {
      return AnnotatedField.class.cast(super.decorated());
   }

   public AnnotatedType<X> getDeclaringType()
   {
      return decorated().getDeclaringType();
   }

   public Field getJavaMember()
   {
      return decorated().getJavaMember();
   }

   public boolean isStatic()
   {
      return decorated().isStatic();
   }
}
