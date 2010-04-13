package org.jboss.seam.jms.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Seam 3 JMS Qualifier for {@link javax.jms.Session} types
 * 
 * @author Jordan Ganoff
 */
@Qualifier
@Documented
@Inherited
@Target( { FIELD, METHOD, TYPE })
@Retention(RUNTIME)
public @interface JmsSession
{
   @Nonbinding
   public abstract boolean transacted() default false;

   @Nonbinding
   public abstract int acknowledgementType() default javax.jms.Session.AUTO_ACKNOWLEDGE;
}