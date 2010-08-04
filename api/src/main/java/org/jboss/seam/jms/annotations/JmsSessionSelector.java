package org.jboss.seam.jms.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Target( { FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface JmsSessionSelector
{
   /**
    * @see javax.jms.Session#getTransacted()
    */
   @Nonbinding
   boolean transacted() default false;

   /**
    * @see javax.jms.Session#getAcknowledgeMode()
    */
   @Nonbinding
   int acknowledgementMode() default javax.jms.Session.AUTO_ACKNOWLEDGE;
   
   
   public class JmsSessionSelectorLiteral extends AnnotationLiteral<JmsSessionSelector> implements JmsSessionSelector
   {
      private static final long serialVersionUID = 7495801629674469699L;
      private int acknowledgementMode;
      private boolean transacted;
      
      public JmsSessionSelectorLiteral(boolean transacted, int acknowledgementMode)
      {
         this.transacted = transacted;
         this.acknowledgementMode = acknowledgementMode;
      }
      
      public boolean transacted()
      {
         return transacted;
      }
      
      public int acknowledgementMode()
      {
         return acknowledgementMode;
      }
   }
}
