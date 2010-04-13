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
 * Seam 3 JMS Qualifier for {@link javax.jms.Destination} types
 * 
 * @author Jordan Ganoff
 */
@Qualifier
@Documented
@Inherited
@Target( { FIELD, METHOD, TYPE })
@Retention(RUNTIME)
public @interface JmsDestination
{
   @Nonbinding
   String jndiName() default "";
}
