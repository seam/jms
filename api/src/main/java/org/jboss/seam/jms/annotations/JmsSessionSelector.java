package org.jboss.seam.jms.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Target( { FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface JmsSessionSelector
{
   /**
    * @see javax.jms.Session#getTransacted()
    */
   boolean transacted() default false;

   /**
    * @see javax.jms.Session#getAcknowledgeMode()
    */
   int acknowledgementMode() default javax.jms.Session.AUTO_ACKNOWLEDGE;
}
